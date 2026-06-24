package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;
import dev.matthiesen.cobbled_level_control.common.config.MainConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class CaptureListener {
    public static ObservableSubscription<ThrownPokeballHitEvent> register() {
        return CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, event -> {
            PokemonEntity entity = event.getPokemon();
            Pokemon pokemon = entity.getPokemon();
            if (event.getPokeBall().getOwner() instanceof ServerPlayer player) {
                var modInstance = CobbledLevelControl.INSTANCE;
                var modConfig = modInstance.getConfigManager().getMainConfig();
                var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
                String playerDiffValue = playerData.getDifficulty();
                if (playerDiffValue.equalsIgnoreCase("none")) return Unit.INSTANCE;
                RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
                var catchingModule = difficulty.getCatchingModule();
                ItemStack ball = event.getPokeBall().getPokeBall().item().getDefaultInstance();
                ball.setCount(1);
                if (pokemon.getShiny() && conditionalCheck(player, catchingModule.shiny, modConfig.errorMessages.missingPermission, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
                if (pokemon.isLegendary() && conditionalCheck(player, catchingModule.legendary, modConfig.errorMessages.missingPermission, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
                if (pokemon.isMythical() && conditionalCheck(player, catchingModule.mythical, modConfig.errorMessages.missingPermission, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
                if (pokemon.isUltraBeast() && conditionalCheck(player, catchingModule.ultraBeast, modConfig.errorMessages.missingPermission, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
                PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                String perm = getPermissionString(evoStage, catchingModule);
                if (!perm.isEmpty() && conditionalCheck(player, perm, modConfig.errorMessages.missingPermission, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
                int tierLevel = playerData.getCatching();
                int maxLevel = catchingModule.tiers.get(tierLevel);
                if (conditionalCheck(player, pokemon.getLevel() > maxLevel, modConfig.errorMessages.catchingTier, ball, modConfig)) {
                    event.cancel();
                    return Unit.INSTANCE;
                }
            }
            return Unit.INSTANCE;
        });
    }

    private static String getPermissionString(PokemonUtility.EvoStage evoStage, DifficultyConfig.CatchingConfig catchingModule) {
        String perm;
        switch (evoStage) {
            case PokemonUtility.EvoStage.FINAL -> perm = catchingModule.evolutionStages.finalStageEvo;
            case PokemonUtility.EvoStage.FIRST -> perm = catchingModule.evolutionStages.firstStageEvo;
            case PokemonUtility.EvoStage.SECOND -> perm = catchingModule.evolutionStages.secondStageEvo;
            default -> perm = catchingModule.evolutionStages.singleEvo;
        }
        return perm;
    }

    private static boolean conditionalCheck(ServerPlayer player, boolean condition, String errorMessage, ItemStack ball, MainConfig modConfig) {
        if (condition) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errorMessages.useActionBar);
            player.getInventory().add(ball);
            return true;
        }
        return false;
    }

    private static boolean conditionalCheck(ServerPlayer player, String permissionNode, String errorMessage, ItemStack ball, MainConfig modConfig) {
        if (!permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errorMessages.useActionBar);
            player.getInventory().add(ball);
            return true;
        }
        return false;
    }
}
