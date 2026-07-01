package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.battle.BattleCaptureEndPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;
import dev.matthiesen.cobbled_level_control.common.config.MessagesConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Pair;
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
                var modConfig = modInstance.getConfigManager().getMessagesConfig();
                var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
                String playerDiffValue = playerData.getDifficulty();
                if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
                RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
                var catchingModule = difficulty.getCatchingModule();
                ItemStack ball = event.getPokeBall().getPokeBall().item().getDefaultInstance();
                ball.setCount(1);
                if (pokemon.getShiny() && conditionalCheck(player, catchingModule.shiny, modConfig.errors.missingPermission, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isLegendary() && conditionalCheck(player, catchingModule.legendary, modConfig.errors.missingPermission, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isMythical() && conditionalCheck(player, catchingModule.mythical, modConfig.errors.missingPermission, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isUltraBeast() && conditionalCheck(player, catchingModule.ultraBeast, modConfig.errors.missingPermission, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
                PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                String perm = getPermissionString(evoStage, catchingModule);
                if (!perm.isEmpty() && conditionalCheck(player, perm, modConfig.errors.missingPermission, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
                int tierLevel = playerData.getCatching();
                int maxLevel = catchingModule.tiers.get(tierLevel);
                if (conditionalCheck(player, pokemon.getLevel() > maxLevel, modConfig.errors.catchingTier, ball, modConfig, event)) {
                    return Unit.INSTANCE;
                }
            }
            return Unit.INSTANCE;
        });
    }

    @SuppressWarnings("SameReturnValue")
    public static void doCancel(ThrownPokeballHitEvent event, ServerPlayer player, String errorMessage) {
        event.cancel();

        CobbledLevelControl.INSTANCE.createInfoLog("Catch canceled for player " + player.getName().getString() + " due to: " + errorMessage);

        if (PlayerExtensionsKt.isInBattle(player)) {
            CobbledLevelControl.INSTANCE.createInfoLog("Player is in battle, sending capture end packet and finishing capture action.");
            Pair<PokemonBattle, BattleActor> battleInstance = PlayerExtensionsKt.getBattleState(player);
            if (battleInstance == null) {
                CobbledLevelControl.INSTANCE.createInfoLog("Battle instance is null, cannot send capture end packet or finish capture action.");
                return;
            }
            PokemonBattle battle = battleInstance.component1();

            var catchAction = battle.getCaptureActions().stream()
                    .filter(action -> action.getTargetPokemon().getActor().isForPokemon(event.getPokemon()))
                    .findFirst()
                    .orElse(null);

            if (catchAction == null) {
                CobbledLevelControl.INSTANCE.createInfoLog("No capture action found for the target Pokemon, cannot finish capture action.");
                return;
            }

            battle.dispatchWaiting(2F, () -> {
                battle.broadcastChatMessage(Component.literal("Catch Canceled: " + errorMessage).withStyle(ChatFormatting.RED));
                return Unit.INSTANCE;
            });
            battle.sendUpdate(new BattleCaptureEndPacket(battleInstance.component1().getActivePokemon().iterator().next().getPNX(), false));
            battle.finishCaptureAction(catchAction);

            CobbledLevelControl.INSTANCE.createInfoLog("Capture action finished for player " + player.getName().getString() + ". Capture canceled successfully.");
        }
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

    private static boolean conditionalCheck(ServerPlayer player, boolean condition, String errorMessage, ItemStack ball, MessagesConfig modConfig, ThrownPokeballHitEvent event) {
        if (condition) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            player.getInventory().add(ball);
            doCancel(event, player, errorMessage);
            return true;
        }
        return false;
    }

    private static boolean conditionalCheck(ServerPlayer player, String permissionNode, String errorMessage, ItemStack ball, MessagesConfig modConfig, ThrownPokeballHitEvent event) {
        if (!permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            player.getInventory().add(ball);
            doCancel(event, player, errorMessage);
            return true;
        }
        return false;
    }
}
