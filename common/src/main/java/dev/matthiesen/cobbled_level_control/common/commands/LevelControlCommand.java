package dev.matthiesen.cobbled_level_control.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.Catching;
import dev.matthiesen.cobbled_level_control.common.runtime.Difficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.Leveling;
import dev.matthiesen.common.matthiesen_lib_api.command.AbstractCommand;
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
                        .requires(src -> PermissionHelpers.checkPermission(src, PermissionHelpers.COMMAND_ADMIN_PERMISSION))
                        .then("reload", reload -> reload.executes(this::reload))

                        .then("level-up", levelUp -> levelUp
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("module", StringArgumentType.string())
                                                .suggests(modulesProvider())
                                                .executes(this::levelUp)
                                        )
                                )
                        )
                        .then("set-difficulty", setDifficulty -> setDifficulty
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("difficulty", StringArgumentType.string())
                                                .suggests((_ctx, builder) -> {
                                                    var diffNames = CobbledLevelControl.INSTANCE.getDifficultyNames();
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
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("module", StringArgumentType.string())
                                                .suggests(modulesProvider())
                                                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                        .executes(this::setLevel)
                                                )
                                        )
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
        return 0;
    }

    public int reload(CommandContext<CommandSourceStack> context) {
        CobbledLevelControl.INSTANCE.reload().run();
        return 1;
    }

    public int setLevel(CommandContext<CommandSourceStack> context) {
        try {
            var modInstance = CobbledLevelControl.INSTANCE;
            var source = context.getSource();

            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String module = StringArgumentType.getString(context, "module");
            int level = IntegerArgumentType.getInteger(context, "level");

            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();

            if (playerDiffValue.equalsIgnoreCase("none")) {
                source.sendSystemMessage(Component.literal("Target player does not have a difficulty set. Please set a difficulty first.").withStyle(ChatFormatting.YELLOW));
                return 0;
            }

            Difficulty difficulty = modInstance.getDifficulty(playerDiffValue);

            switch (module) {
                case "catch" -> {
                    Catching catchingModule = difficulty.catching();
                    int maxLevel = catchingModule.config().tiers.size();

                    if (level > maxLevel) {
                        source.sendSystemMessage(Component.literal("Level exceeds maximum level for Catching module. Max level is " + maxLevel + ".").withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    modInstance.getConfigManager().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(level));
                    player.sendSystemMessage(Component.literal("Your Catching level has been set to " + level + ".").withStyle(ChatFormatting.GREEN));
                    source.sendSystemMessage(Component.literal("Set Catching level of " + player.getName().getString() + " to " + level + ".").withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                case "level" -> {
                    Leveling levelingModule = difficulty.leveling();
                    int maxLevel = levelingModule.tierMap().size();

                    if (level > maxLevel) {
                        source.sendSystemMessage(Component.literal("Level exceeds maximum level for Leveling module. Max level is " + maxLevel + ".").withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    modInstance.getConfigManager().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(level));
                    player.sendSystemMessage(Component.literal("Your Leveling level has been set to " + level + ".").withStyle(ChatFormatting.GREEN));
                    source.sendSystemMessage(Component.literal("Set Leveling level of " + player.getName().getString() + " to " + level + ".").withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                default -> {
                    source.sendSystemMessage(Component.literal("Invalid module specified. Valid modules are: catch, level").withStyle(ChatFormatting.RED));
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
            var source = context.getSource();

            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String difficultyName = StringArgumentType.getString(context, "difficulty");

            String difficulty = null;

            if (modInstance.getDifficultyNames().contains(difficultyName)) {
                difficulty = difficultyName;
            }

            if (difficulty == null) {
                source.sendFailure(Component.literal("Difficulty " + difficultyName + " does not exist!").withStyle(ChatFormatting.RED));
                return 0;
            }

            final String finalDifficulty = difficulty;
            modInstance.getConfigManager().editPlayerAccountRecord(player.getUUID(), record -> record.setDifficulty(finalDifficulty));
            player.sendSystemMessage(Component.literal("Your difficulty has been set to " + difficulty + "!").withStyle(ChatFormatting.GREEN));
            source.sendSystemMessage(Component.literal("Set " + player.getName().getString() + "'s difficulty to " + difficulty + "!").withStyle(ChatFormatting.GREEN));
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
            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();

            if (playerDiffValue.equalsIgnoreCase("none")) {
                source.sendSystemMessage(Component.literal("Target player does not have a difficulty set. Please set a difficulty first.").withStyle(ChatFormatting.YELLOW));
                return 0;
            }

            Difficulty difficulty = modInstance.getDifficulty(playerDiffValue);

            int level;
            int nextLevel;

            switch (module.toLowerCase()) {
                case "catch" -> {
                    Catching catchingModule = difficulty.catching();

                    level = playerData.getCatching();
                    nextLevel = level + 1;
                    int maxLevel = catchingModule.config().tiers.size();

                    if (nextLevel > maxLevel) {
                        source.sendSystemMessage(Component.literal("Target player is already at the maximum level for the Catching module.").withStyle(ChatFormatting.YELLOW));
                        return 0;
                    }

                    modInstance.getConfigManager().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(nextLevel));
                    player.sendSystemMessage(Component.literal("Your tier in catching has increased to " + nextLevel + "!").withStyle(ChatFormatting.AQUA));
                    source.sendSystemMessage(Component.literal("Successfully leveled up " + player.getName().getString() + " in the Catching module to tier " + nextLevel + ".").withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                case "level" -> {
                    Leveling levelingModule = difficulty.leveling();

                    level = playerData.getLeveling();
                    nextLevel = level + 1;
                    int maxLevel = levelingModule.tierMap().size();

                    if (nextLevel > maxLevel) {
                        source.sendSystemMessage(Component.literal("Target player is already at the maximum level for the Leveling module.").withStyle(ChatFormatting.YELLOW));
                        return 0;
                    }

                    modInstance.getConfigManager().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(nextLevel));
                    player.sendSystemMessage(Component.literal("Your tier in leveling has increased to " + nextLevel + "!").withStyle(ChatFormatting.AQUA));
                    source.sendSystemMessage(Component.literal("Successfully leveled up " + player.getName().getString() + " in the Leveling module to tier " + nextLevel + ".").withStyle(ChatFormatting.GREEN));
                    return 1;
                }
                default -> {
                    source.sendSystemMessage(Component.literal("Invalid module specified. Please use 'catch' or 'level'.").withStyle(ChatFormatting.RED));
                    return 0;
                }
            }
        } catch (CommandSyntaxException e) {
            CobbledLevelControl.INSTANCE.createErrorLog("Failed to level up player", e);
            return 0;
        }
    }
}
