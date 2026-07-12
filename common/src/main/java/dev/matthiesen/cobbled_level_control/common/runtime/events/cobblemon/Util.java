package dev.matthiesen.cobbled_level_control.common.runtime.events.cobblemon;

import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;
import dev.matthiesen.cobbled_level_control.common.config.MessagesConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class Util {
    public static String getPermissionString(PokemonUtility.EvoStage evoStage, DifficultyConfig.LevelingConfig levelingModule) {
        String perm;
        switch (evoStage) {
            case PokemonUtility.EvoStage.FINAL -> perm = levelingModule.evolutionStages.finalStageEvo;
            case PokemonUtility.EvoStage.FIRST -> perm = levelingModule.evolutionStages.firstStageEvo;
            case PokemonUtility.EvoStage.SECOND -> perm = levelingModule.evolutionStages.secondStageEvo;
            default -> perm = levelingModule.evolutionStages.singleEvo;
        }
        return perm;
    }

    public static String getPermissionString(PokemonUtility.EvoStage evoStage, DifficultyConfig.CatchingConfig catchingModule) {
        String perm;
        switch (evoStage) {
            case PokemonUtility.EvoStage.FINAL -> perm = catchingModule.evolutionStages.finalStageEvo;
            case PokemonUtility.EvoStage.FIRST -> perm = catchingModule.evolutionStages.firstStageEvo;
            case PokemonUtility.EvoStage.SECOND -> perm = catchingModule.evolutionStages.secondStageEvo;
            default -> perm = catchingModule.evolutionStages.singleEvo;
        }
        return perm;
    }

    public static String getPermissionString(PokemonUtility.EvoStage evoStage, DifficultyConfig.BattleConfig battleConfig) {
        String perm;
        switch (evoStage) {
            case PokemonUtility.EvoStage.FINAL -> perm = battleConfig.evolutionStages.finalStageEvo;
            case PokemonUtility.EvoStage.FIRST -> perm = battleConfig.evolutionStages.firstStageEvo;
            case PokemonUtility.EvoStage.SECOND -> perm = battleConfig.evolutionStages.secondStageEvo;
            default -> perm = battleConfig.evolutionStages.singleEvo;
        }
        return perm;
    }

    public static boolean conditionalCheck(ServerPlayer player, String permissionNode) {
        if (!permissionNode.isEmpty()) return PermissionHelpers.doesNotHavePermission(player, permissionNode);
        return false;
    }

    public static boolean conditionalCheck(ServerPlayer player, boolean condition, String errorMessage, MessagesConfig modConfig, Runnable action) {
        if (condition) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            action.run();
            return true;
        }
        return false;
    }

    public static boolean conditionalCheck(ServerPlayer player, String permissionNode, String errorMessage, MessagesConfig modConfig, Runnable action) {
        if (!permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            action.run();
            return true;
        }
        return false;
    }

    public static boolean conditionalCheck(ServerPlayer player, String permissionNode, String errorMessage, MessagesConfig modConfig, BattleStartedEvent.Pre event) {
        if (!permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            event.setReason(Component.literal(errorMessage).withStyle(ChatFormatting.RED));
            event.cancel();
            return true;
        }
        return false;
    }
}
