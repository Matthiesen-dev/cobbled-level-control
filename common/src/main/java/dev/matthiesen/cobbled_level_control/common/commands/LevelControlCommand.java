package dev.matthiesen.cobbled_level_control.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CobbledLevelControlConfigManager;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.matthiesen_core.common.api.command.CoreCommand;
import dev.matthiesen.matthiesen_core.common.utility.chat.ChatTableBuilder;
import dev.matthiesen.matthiesen_core.common.utility.commands.CommandBuilder;
import dev.matthiesen.matthiesen_core.common.utility.player_data.ServerUser;
import dev.matthiesen.matthiesen_core.common.utility.player_data.ServerUserArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class LevelControlCommand implements CoreCommand {
    public static final LevelControlCommand CMD = new LevelControlCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection context) {
        dispatcher.register(
                new CommandBuilder("level-control")
                        .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_ROOT_PERMISSION))
                        .then("reload", reload -> reload
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_RELOAD_PERMISSION))
                                .executes(this::reload))
                        .then("level-up", levelUp -> levelUp
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_LEVEL_UP_PERMISSION))
                                .then(Commands.argument("player", ServerUserArgument.playerArg())
                                        .then(Commands.argument("module", StringArgumentType.string())
                                                .suggests(modulesProvider())
                                                .executes(this::levelUp)
                                        )
                                )
                        )
                        .then("set-difficulty", setDifficulty -> setDifficulty
                                .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_SET_DIFFICULTY_PERMISSION))
                                .then(Commands.argument("player", ServerUserArgument.playerArg())
                                        .then(Commands.argument("difficulty", StringArgumentType.string())
                                                .suggests((_ctx, builder) -> {
                                                    var diffNames = CobbledLevelControlConfigManager.SERVER_CONFIG.difficulties.get();
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
                                .then(Commands.argument("player", ServerUserArgument.playerArg())
                                                .then(Commands.argument("level", IntegerArgumentType.integer(1))
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
                                .then(Commands.argument("player", ServerUserArgument.playerArg())
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

    public int action(CommandContext<CommandSourceStack> context) {
        try {
            var source = context.getSource();
            var modInstance = CobbledLevelControl.INSTANCE;
            ServerUser targetPlayer;

            // status-other includes a player argument, while status resolves to the command source player.
            boolean hasPlayerArgument = context.getNodes().stream()
                    .anyMatch(node -> "player".equals(node.getNode().getName()));
            if (hasPlayerArgument) {
                targetPlayer = ServerUserArgument.getUser(context, "player");
            } else {
                targetPlayer = new ServerUser(source.getPlayerOrException());
            }

            var playerRecord = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(targetPlayer.getUUID());
            if (playerRecord == null) {
                source.sendFailure(Component.literal("No account record found for " + targetPlayer.getUsername() + ".").withStyle(ChatFormatting.RED));
                return 0;
            }

            String difficulty = playerRecord.getDifficulty();
            if (difficulty.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
                difficulty = "Not set";
            }

            var builder = new ChatTableBuilder("Account record for " + targetPlayer.getUsername())
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
        modInstance.reload().run();
        context.getSource().sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_reloaded.get()).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    public int setLevel(CommandContext<CommandSourceStack> context) {
        var modInstance = CobbledLevelControl.INSTANCE;
        var source = context.getSource();
        ServerUser player = ServerUserArgument.getUser(context, "player");
        String module = StringArgumentType.getString(context, "module");
        int level = IntegerArgumentType.getInteger(context, "level");
        var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        String playerDiffValue = playerData.getDifficulty();
        if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
            source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_missingDifficulty.get()).withStyle(ChatFormatting.YELLOW));
            return 0;
        }
        RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);

        ServerPlayer onlinePlayer = player.getOnlinePlayer();

        switch (module) {
            case "catch" -> {
                var catchingModule = difficulty.getCatchingModule();
                int maxLevel = catchingModule.getConfig().tiers.size();
                if (level > maxLevel) {
                    source.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_catchingLevelToHigh.get()
                                    .replace("%maxLevel%", Integer.toString(maxLevel))
                    ).withStyle(ChatFormatting.RED));
                    return 0;
                }
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(level));

                if (onlinePlayer != null) {
                    onlinePlayer.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_targetCatchingLevelSet.get()
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN), CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_useActionBar.getAsBoolean());
                }

                source.sendSystemMessage(Component.literal(
                        CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_sourceCatchingLevelSet.get()
                                .replace("%target%", player.getUsername())
                                .replace("%level%", Integer.toString(level))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            case "level" -> {
                var levelingModule = difficulty.getLevelingModule();
                int maxLevel = levelingModule.getConfig().tiers.size();
                if (level > maxLevel) {
                    source.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_levelingLevelToHigh.get()
                                    .replace("%maxLevel%", Integer.toString(maxLevel))
                    ).withStyle(ChatFormatting.RED));
                    return 0;
                }
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(level));

                if (onlinePlayer != null) {
                    onlinePlayer.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_targetLevelingLevelSet.get()
                                    .replace("%level%", Integer.toString(level))
                    ).withStyle(ChatFormatting.GREEN), CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_useActionBar.getAsBoolean());
                }

                source.sendSystemMessage(Component.literal(
                        CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_sourceLevelingLevelSet.get()
                                .replace("%target%", player.getUsername())
                                .replace("%level%", Integer.toString(level))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            default -> {
                source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_invalidModule.get()).withStyle(ChatFormatting.RED));
                return 0;
            }
        }
    }

    public int setDifficulty(CommandContext<CommandSourceStack> context) {
        var modInstance = CobbledLevelControl.INSTANCE;
        var source = context.getSource();
        ServerUser player = ServerUserArgument.getUser(context, "player");
        String difficultyName = StringArgumentType.getString(context, "difficulty");
        if (difficultyName == null) {
            source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_invalidDifficulty.get()).withStyle(ChatFormatting.RED));
            return 0;
        }
        String difficulty = null;
        if (CobbledLevelControlConfigManager.SERVER_CONFIG.difficulties.get().contains(difficultyName)) {
            difficulty = difficultyName;
        }
        if (difficulty == null) {
            source.sendFailure(Component.literal(
                    CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_difficultyDoesNotExist.get()
                            .replace("%difficultyName%", difficultyName)
            ).withStyle(ChatFormatting.RED));
            return 0;
        }
        final String finalDifficulty = difficulty;

        ServerPlayer onlinePlayer = player.getOnlinePlayer();

        modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setDifficulty(finalDifficulty));

        if (onlinePlayer != null) {
            onlinePlayer.sendSystemMessage(Component.literal(
                    CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_targetSetDifficulty.get()
                            .replace("%difficulty%", finalDifficulty)
            ).withStyle(ChatFormatting.GREEN), CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_useActionBar.getAsBoolean());
        }

        source.sendSystemMessage(Component.literal(
                CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_sourceSetDifficulty.get()
                        .replace("%target%", player.getUsername())
                        .replace("%difficulty%", finalDifficulty)
        ).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    public int levelUp(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        ServerUser player = ServerUserArgument.getUser(context, "player");
        String module = StringArgumentType.getString(context, "module");
        var modInstance = CobbledLevelControl.INSTANCE;
        var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        String playerDiffValue = playerData.getDifficulty();
        if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) {
            source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_missingDifficulty.get()).withStyle(ChatFormatting.YELLOW));
            return 0;
        }
        RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
        int level;
        int nextLevel;

        ServerPlayer onlinePlayer = player.getOnlinePlayer();

        switch (module.toLowerCase()) {
            case "catch" -> {
                var catchingModule = difficulty.getCatchingModule();
                level = playerData.getCatching();
                nextLevel = level + 1;
                int maxLevel = catchingModule.getConfig().tiers.size();
                if (nextLevel > maxLevel) {
                    source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_catchingLevelAlreadyMax.get()).withStyle(ChatFormatting.YELLOW));
                    return 0;
                }
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(nextLevel));

                if (onlinePlayer != null) {
                    onlinePlayer.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_targetCatchingTierSet.get()
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.AQUA), CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_useActionBar.getAsBoolean());
                }

                source.sendSystemMessage(Component.literal(
                        CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_sourceCatchingTierSet.get()
                                .replace("%target%", player.getUsername())
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
                    source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_levelingLevelAlreadyMax.get()).withStyle(ChatFormatting.YELLOW));
                    return 0;
                }
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(nextLevel));
                if (onlinePlayer != null) {
                    onlinePlayer.sendSystemMessage(Component.literal(
                            CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_targetLevelingTierSet.get()
                                    .replace("%tier%", Integer.toString(nextLevel))
                    ).withStyle(ChatFormatting.AQUA), CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_useActionBar.getAsBoolean());
                }
                source.sendSystemMessage(Component.literal(
                        CobbledLevelControlConfigManager.SERVER_CONFIG.messages_success_sourceLevelingTierSet.get()
                                .replace("%target%", player.getUsername())
                                .replace("%tier%", Integer.toString(nextLevel))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            default -> {
                source.sendSystemMessage(Component.literal(CobbledLevelControlConfigManager.SERVER_CONFIG.messages_error_invalidModule.get()).withStyle(ChatFormatting.RED));
                return 0;
            }
        }
    }
}
