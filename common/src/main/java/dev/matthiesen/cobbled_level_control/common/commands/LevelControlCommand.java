package dev.matthiesen.cobbled_level_control.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.common.matthiesen_lib_api.command.AbstractCommand;
import dev.matthiesen.common.matthiesen_lib_api.utility.ChatTableBuilder;
import dev.matthiesen.common.matthiesen_lib_api.utility.CommandBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class LevelControlCommand extends AbstractCommand {
    public static final LevelControlCommand CMD = new LevelControlCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection context) {
        dispatcher.register(
                new CommandBuilder("level-control")
                        .then("reload", reload -> reload
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_RELOAD_PERMISSION))
                                .executes(this::reload))
                        .then("level-up", levelUp -> levelUp
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_LEVEL_UP_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("module", StringArgumentType.string())
                                                .suggests(modulesProvider())
                                                .executes(this::levelUp)
                                        )
                                )
                        )
                        .then("set-difficulty", setDifficulty -> setDifficulty
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_SET_DIFFICULTY_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("difficulty", StringArgumentType.string())
                                                .suggests((_ctx, builder) -> {
                                                    var diffNames = CobbledLevelControl.INSTANCE.getConfigManager().getMainConfig().difficulties;
                                                    for (var difficulty : diffNames) {
                                                        builder.suggest(difficulty);
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(this::setDifficulty)
                                        )
                                )
                        )
                        .then("set-level", setLevel -> setLevel
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_SET_LEVEL_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("level", IntegerArgumentType.integer())
                                                        .executes(this::setLevel)
                                                )
                                        )
                        )
                        .then("status", status -> status
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_STATUS_PERMISSION))
                                .executes(this::action)
                        )
                        .then("status-other", statusOther -> statusOther
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_STATUS_OTHER_PERMISSION))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(this::action)
                                )
                        )

                        .build()
        );
    }

    public SuggestionProvider<CommandSourceStack> modulesProvider() {
        return (_ctx, builder) -> {
            String[] modules = {"catch", "level"};
            for (var module : modules) {
                builder.suggest(module);
            }
            return builder.buildFuture();
        };
    }

    @Override
    public int action(CommandContext<CommandSourceStack> context) {
        try {
            var source = context.getSource();
            var modInstance = CobbledLevelControl.INSTANCE;
            ServerPlayer targetPlayer;

            // status-other includes a player argument, while status resolves to the command source player.
            boolean hasPlayerArgument = context.getNodes().stream()
                    .anyMatch(node -> "player".equals(node.getNode().getName()));
            if (hasPlayerArgument) {
                targetPlayer = EntityArgument.getPlayer(context, "player");
            } else {
                targetPlayer = source.getPlayerOrException();
            }

            var playerRecord = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(targetPlayer.getUUID());
            if (playerRecord == null) {
                source.sendFailure(Component.literal("No account record found for " + targetPlayer.getName().getString() + ".").withStyle(ChatFormatting.RED));
                return 0;
            }

            String difficulty = playerRecord.getDifficulty();
            if (difficulty.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
                difficulty = "Not set";
            }

            var builder = new ChatTableBuilder("Account record for " + targetPlayer.getName().getString())
                    .addRow("- Difficulty", difficulty)
                    .addRow("- Catching", Integer.toString(playerRecord.getCatching()))
                    .addRow("- Leveling", Integer.toString(playerRecord.getLeveling()))
                            .build();
            source.sendSystemMessage(builder);
            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("This command requires a player target.").withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    public int reload(CommandContext<CommandSourceStack> context) {
        var modInstance = CobbledLevelControl.INSTANCE;
        var messagesConfig = modInstance.getConfigManager().getMessagesConfig();
        modInstance.reload().run();
        context.getSource().sendSystemMessage(Component.literal(messagesConfig.messages.reloaded).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    public int setLevel(CommandContext<CommandSourceStack> context) {
        try {
            var modInstance = CobbledLevelControl.INSTANCE;
            var messagesConfig = modInstance.getConfigManager().getMessagesConfig();
            var source = context.getSource();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String module = StringArgumentType.getString(context, "module");
            int level = IntegerArgumentType.getInteger(context, "level");
            var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();
            if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
                source.sendSystemMessage(Component.literal(messagesConfig.errors.missingDifficulty).withStyle(ChatFormatting.YELLOW));
                return 0;
            }
            RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
            switch (module) {
                case "catch" -> {
                    var catchingModule = difficulty.getCatchingModule();
                    int maxLevel = catchingModule.getConfig().tiers.size();
                    if (level > maxLevel) {
                        source.sendSystemMessage(Component.literal(
                                messagesConfig.errors.catchingLevelToHigh
                                        .replace("%maxLevel%", Integer.toString(maxLevel))
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                    modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(level));
                    player.sendSystemMessage(Component.literal(
                            messagesConfig.messages.targetCatchingLevelSet
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN), messagesConfig.messages.useActionBar);
                    source.sendSystemMessage(Component.literal(
                            messagesConfig.messages.sourceCatchingLevelSet
                                    .replace("%target%", player.getName().getString())
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                case "level" -> {
                    var levelingModule = difficulty.getLevelingModule();
                    int maxLevel = levelingModule.getConfig().tiers.size();
                    if (level > maxLevel) {
                        source.sendSystemMessage(Component.literal(
                                messagesConfig.errors.levelingLevelToHigh.replace("%maxLevel%", Integer.toString(maxLevel))
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                    modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(level));
                    player.sendSystemMessage(Component.literal(
                            messagesConfig.messages.targetLevelingLevelSet
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN), messagesConfig.messages.useActionBar);
                    source.sendSystemMessage(Component.literal(
                            messagesConfig.messages.sourceLevelingLevelSet
                                    .replace("%target%", player.getName().getString())
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                default -> {
                    source.sendSystemMessage(Component.literal(messagesConfig.errors.invalidModule).withStyle(ChatFormatting.RED));
                    return 0;
                }
            }
        } catch (CommandSyntaxException e) {
            CobbledLevelControl.INSTANCE.createErrorLog(e.getMessage(), e);
            return 0;
        }
    }

    public int setDifficulty(CommandContext<CommandSourceStack> context) {
        try {
            var modInstance = CobbledLevelControl.INSTANCE;
            var messagesConfig = modInstance.getConfigManager().getMessagesConfig();
            var source = context.getSource();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String difficultyName = StringArgumentType.getString(context, "difficulty");
            if (difficultyName == null) {
                source.sendSystemMessage(Component.literal(messagesConfig.errors.invalidDifficulty).withStyle(ChatFormatting.RED));
                return 0;
            }
            String difficulty = null;
            if (modInstance.getConfigManager().getMainConfig().difficulties.contains(difficultyName)) {
                difficulty = difficultyName;
            }
            if (difficulty == null) {
                source.sendFailure(Component.literal(
                        messagesConfig.errors.difficultyDoesNotExist
                                .replace("%difficultyName%", difficultyName)
                ).withStyle(ChatFormatting.RED));
                return 0;
            }
            final String finalDifficulty = difficulty;
            modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setDifficulty(finalDifficulty));
            player.sendSystemMessage(Component.literal(
                    messagesConfig.messages.targetSetDifficulty
                            .replace("%difficulty%", finalDifficulty)
            ).withStyle(ChatFormatting.GREEN), messagesConfig.messages.useActionBar);
            source.sendSystemMessage(Component.literal(
                    messagesConfig.messages.sourceSetDifficulty
                            .replace("%target%", player.getName().getString())
                            .replace("%difficulty%", finalDifficulty)
            ).withStyle(ChatFormatting.GREEN));
            return 1;
        } catch (CommandSyntaxException e) {
            CobbledLevelControl.INSTANCE.createErrorLog(e.getMessage(), e);
            return 0;
        }
    }

    public int levelUp(CommandContext<CommandSourceStack> context) {
        try {
            var source = context.getSource();
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String module = StringArgumentType.getString(context, "module");
            var modInstance = CobbledLevelControl.INSTANCE;
            var messagesConfig = modInstance.getConfigManager().getMessagesConfig();
            var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();
            if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
                source.sendSystemMessage(Component.literal(messagesConfig.errors.missingDifficulty).withStyle(ChatFormatting.YELLOW));
                return 0;
            }
            RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
            int level;
            int nextLevel;
            switch (module.toLowerCase()) {
                case "catch" -> {
                    var catchingModule = difficulty.getCatchingModule();
                    level = playerData.getCatching();
                    nextLevel = level + 1;
                    int maxLevel = catchingModule.getConfig().tiers.size();
                    if (nextLevel > maxLevel) {
                        source.sendSystemMessage(Component.literal(messagesConfig.errors.catchingLevelAlreadyMax).withStyle(ChatFormatting.YELLOW));
                        return 0;
                    }
                    modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(nextLevel));
                    player.sendSystemMessage(Component.literal(
                            messagesConfig.messages.targetCatchingTierSet
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.AQUA), messagesConfig.messages.useActionBar);
                    source.sendSystemMessage(Component.literal(
                            messagesConfig.messages.sourceCatchingTierSet
                                    .replace("%target%", player.getName().getString())
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                case "level" -> {
                    var levelingModule = difficulty.getLevelingModule();
                    level = playerData.getLeveling();
                    nextLevel = level + 1;
                    int maxLevel = levelingModule.getConfig().tiers.size();
                    if (nextLevel > maxLevel) {
                        source.sendSystemMessage(Component.literal(messagesConfig.errors.levelingLevelAlreadyMax).withStyle(ChatFormatting.YELLOW));
                        return 0;
                    }
                    modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(nextLevel));
                    player.sendSystemMessage(Component.literal(
                            messagesConfig.messages.targetLevelingTierSet
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.AQUA), messagesConfig.messages.useActionBar);
                    source.sendSystemMessage(Component.literal(
                            messagesConfig.messages.sourceLevelingTierSet
                                    .replace("%target%", player.getName().getString())
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                default -> {
                    source.sendSystemMessage(Component.literal(messagesConfig.errors.invalidModule).withStyle(ChatFormatting.RED));
                    return 0;
                }
            }
        } catch (CommandSyntaxException e) {
            CobbledLevelControl.INSTANCE.createErrorLog("Failed to level up player", e);
            return 0;
        }
    }
}
