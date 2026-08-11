package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleSide;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public final class BattleStartEventsListener {
    public static ObservableSubscription<BattleStartedEvent.Pre> register() {
        return CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.HIGHEST, event -> {
            PokemonBattle battle = event.getBattle();
            var modInstance = CobbledLevelControl.INSTANCE;
            for (BattleActor actor : battle.getActors()) {
                if (actor.getType() != ActorType.PLAYER) continue;
                ServerPlayer player = ((PlayerBattleActor) actor).getEntity();
                if (player == null) continue;
                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
                var battleModule = CLCConfig.getBattleConfig();
                if (battleModule.doNotRestrictBattles()) continue;
                if (!battle.isPvW()) return Unit.INSTANCE;
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
                var levelingModule = CLCConfig.getLevelingConfig();
                if (levelingModule.doRestrictLeveling()) {
                    int levelingLevel = playerData.getLeveling();
                    int maxLevelingLevel = levelingModule.tiers().get(Integer.toString(levelingLevel));
                    if (maxLevel > maxLevelingLevel) {
                        player.sendSystemMessage(Component.literal(CLCConfig.SERVER_CONFIG.messages_error_battle.get()).withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.get());
                        event.setReason(Component.literal(CLCConfig.SERVER_CONFIG.messages_error_battle.get()).withStyle(ChatFormatting.RED));
                        event.cancel();
                        return Unit.INSTANCE;
                    }
                }

                var battleSides = battle.getSides();

                BattleSide playerSide = null;
                BattleSide pokemonSide = null;

                for (BattleSide side : battleSides) {
                    if (Arrays.stream(side.getActors()).anyMatch(a -> a.getType() == ActorType.PLAYER)) {
                        playerSide = side;
                    } else {
                        pokemonSide = side;
                    }
                }

                if (playerSide == null || pokemonSide == null) continue;

                var activePokemon = pokemonSide.getActivePokemon();
                for (ActiveBattlePokemon activeMon : activePokemon) {
                    BattlePokemon battlePokemon = activeMon.getBattlePokemon();
                    if (battlePokemon == null) continue;
                    Pokemon pokemon = battlePokemon.getOriginalPokemon();

                    if (pokemon.getShiny() && Util.conditionalCheck(player, battleModule.shiny(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isLegendary() && Util.conditionalCheck(player, battleModule.legendary(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isMythical() && Util.conditionalCheck(player, battleModule.mythical(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isUltraBeast() && Util.conditionalCheck(player, battleModule.ultraBeast(), CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), event)) {
                        return Unit.INSTANCE;
                    }

                    PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                    String perm = Util.getPermissionString(evoStage, battleModule);
                    if (!perm.isEmpty() && Util.conditionalCheck(player, perm, CLCConfig.SERVER_CONFIG.messages_error_missingPermission.get(), event)) {
                        return Unit.INSTANCE;
                    }
                }
            }
            return Unit.INSTANCE;
        });
    }
}
