package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class BattleStartEventsListener {
    public static ObservableSubscription<BattleStartedEvent.Pre> register() {
        return CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, event -> {
            PokemonBattle battle = event.getBattle();
            var modInstance = CobbledLevelControl.INSTANCE;
            for (BattleActor actor : battle.getActors()) {
                if (actor.getType() != ActorType.PLAYER) return Unit.INSTANCE;
                ServerPlayer player = ((PlayerBattleActor) actor).getEntity();
                if (player == null) return Unit.INSTANCE;
                var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
                String playerDiffValue = playerData.getDifficulty();
                if (!playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
                RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
                var battleModule = difficulty.getBattleModule();
                if (!battleModule.doCheckBattles()) return Unit.INSTANCE;
                PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
                int maxLevel = 0;
                for (int i = 0; i < 6; i++) {
                    Pokemon pokemon = partyStore.get(i);
                    if (pokemon != null) {
                        int lvl = pokemon.getLevel();
                        if (lvl > maxLevel) {
                            maxLevel = lvl;
                        }
                    }
                }
                var levelingModule = difficulty.getLevelingModule();
                int levelingLevel = playerData.getLeveling();
                int maxLevelingLevel = levelingModule.tiers.get(levelingLevel);
                if (maxLevel > maxLevelingLevel) {
                    event.cancel();
                    var config = modInstance.getConfigManager().getMessagesConfig();
                    player.sendSystemMessage(Component.literal(config.errors.battle).withStyle(ChatFormatting.RED), config.errors.useActionBar);
                }
            }
            return Unit.INSTANCE;
        });
    }
}
