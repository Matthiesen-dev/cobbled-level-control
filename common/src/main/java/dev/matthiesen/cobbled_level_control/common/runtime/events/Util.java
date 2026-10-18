package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.config.def.*;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class Util {
    public static PermSet getPermSet(PokemonUtility.EvoStage evoStage, EvolutionStages evolutionStages) {
        return switch (evoStage) {
            case FINAL -> evolutionStages.finalStageEvo();
            case FIRST -> evolutionStages.firstStageEvo();
            case SECOND -> evolutionStages.secondStageEvo();
            default -> evolutionStages.singleEvo();
        };
    }

    public static boolean conditionalCheck(ServerPlayer player, boolean condition, String errorMessage, Runnable action) {
        if (condition) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.getAsBoolean());
            action.run();
            return true;
        }
        return false;
    }

    public static boolean conditionalCheck(ServerPlayer player, PermSet permissionNode, String errorMessage, Runnable action) {
        if (PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.getAsBoolean());
            action.run();
            return true;
        }
        return false;
    }

    public static boolean conditionalCheck(ServerPlayer player, PermSet permissionNode, String errorMessage, BattleStartedEvent.Pre event) {
        if (PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.getAsBoolean());
            event.setReason(Component.literal(errorMessage).withStyle(ChatFormatting.RED));
            event.cancel();
            return true;
        }
        return false;
    }
}
