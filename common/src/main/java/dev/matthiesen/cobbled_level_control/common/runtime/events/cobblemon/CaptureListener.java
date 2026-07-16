package dev.matthiesen.cobbled_level_control.common.runtime.events.cobblemon;

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
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Pair;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CaptureListener {
    public static ObservableSubscription<ThrownPokeballHitEvent> register() {
        return CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, event -> {
            PokemonEntity entity = event.getPokemon();
            Pokemon pokemon = entity.getPokemon();
            if (event.getPokeBall().getOwner() instanceof ServerPlayer player) {
                var modInstance = CobbledLevelControl.INSTANCE;
                var modConfig = modInstance.getConfigManager().getMessagesConfig();
                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
                String playerDiffValue = playerData.getDifficulty();
                if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
                RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
                var catchingModule = difficulty.getCatchingModule();
                if (catchingModule.doNotRestrictCatching()) return Unit.INSTANCE;
                if (pokemon.getShiny() && Util.conditionalCheck(player, catchingModule.getConfig().shiny, modConfig.errors.missingPermission, modConfig, () ->
                        doCancel(event, player, modConfig.errors.missingPermission))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isLegendary() && Util.conditionalCheck(player, catchingModule.getConfig().legendary, modConfig.errors.missingPermission, modConfig, () ->
                        doCancel(event, player, modConfig.errors.missingPermission))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isMythical() && Util.conditionalCheck(player, catchingModule.getConfig().mythical, modConfig.errors.missingPermission, modConfig, () ->
                        doCancel(event, player, modConfig.errors.missingPermission))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isUltraBeast() &&Util.conditionalCheck(player, catchingModule.getConfig().ultraBeast, modConfig.errors.missingPermission, modConfig, () ->
                        doCancel(event, player, modConfig.errors.missingPermission))) {
                    return Unit.INSTANCE;
                }
                PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                String perm = Util.getPermissionString(evoStage, catchingModule.getConfig());
                if (!perm.isEmpty() && Util.conditionalCheck(player, perm, modConfig.errors.missingPermission, modConfig, () ->
                        doCancel(event, player, modConfig.errors.missingPermission))) {
                    return Unit.INSTANCE;
                }
                int tierLevel = playerData.getCatching();
                int maxLevel = catchingModule.getConfig().tiers.get(Integer.toString(tierLevel));
                if (Util.conditionalCheck(player, pokemon.getLevel() > maxLevel, modConfig.errors.catchingTier, modConfig, () ->
                        doCancel(event, player, modConfig.errors.catchingTier))) {
                    return Unit.INSTANCE;
                }
            }
            return Unit.INSTANCE;
        });
    }

    @SuppressWarnings("SameReturnValue")
    public static void doCancel(ThrownPokeballHitEvent event, ServerPlayer player, String errorMessage) {
        event.cancel();

        if (PlayerExtensionsKt.isInBattle(player)) {
            Pair<PokemonBattle, BattleActor> battleInstance = PlayerExtensionsKt.getBattleState(player);
            if (battleInstance == null) {
                return;
            }
            PokemonBattle battle = battleInstance.component1();

            var catchAction = battle.getCaptureActions().stream()
                    .filter(action -> action.getTargetPokemon().getActor().isForPokemon(event.getPokemon()))
                    .findFirst()
                    .orElse(null);

            if (catchAction == null) {
                return;
            }

            battle.dispatchWaiting(2F, () -> {
                battle.broadcastChatMessage(Component.literal("Catch Canceled: " + errorMessage).withStyle(ChatFormatting.RED));
                return Unit.INSTANCE;
            });
            battle.sendUpdate(new BattleCaptureEndPacket(battleInstance.component1().getActivePokemon().iterator().next().getPNX(), false));
            battle.finishCaptureAction(catchAction);
        }
    }
}
