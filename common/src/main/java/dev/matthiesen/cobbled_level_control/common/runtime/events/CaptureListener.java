package dev.matthiesen.cobbled_level_control.common.runtime.events;

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
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
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
                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
                var catchingModule = CLCConfig.getCatchingConfig();
                if (catchingModule.doNotRestrictCatching()) return Unit.INSTANCE;
                if (pokemon.getShiny() && Util.conditionalCheck(player, catchingModule.shiny(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get()))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isLegendary() && Util.conditionalCheck(player, catchingModule.legendary(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get()))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isMythical() && Util.conditionalCheck(player, catchingModule.mythical(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get()))) {
                    return Unit.INSTANCE;
                }
                if (pokemon.isUltraBeast() &&Util.conditionalCheck(player, catchingModule.ultraBeast(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get()))) {
                    return Unit.INSTANCE;
                }
                PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                String perm = Util.getPermissionString(evoStage, catchingModule);
                if (!perm.isEmpty() && Util.conditionalCheck(player, perm, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get()))) {
                    return Unit.INSTANCE;
                }
                int tierLevel = playerData.getCatching();
                int maxLevel = catchingModule.tiers().get(Integer.toString(tierLevel));
                if (Util.conditionalCheck(player, pokemon.getLevel() > maxLevel, CLCConfig.SERVER_CONFIG.messages_error_catchingTier.get(), () ->
                        doCancel(event, player, CLCConfig.SERVER_CONFIG.messages_error_catchingTier.get()))) {
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
