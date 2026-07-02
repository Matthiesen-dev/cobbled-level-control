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
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleSide;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;
import dev.matthiesen.cobbled_level_control.common.config.MessagesConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public final class BattleStartEventsListener {
    public static ObservableSubscription<BattleStartedEvent.Pre> register() {
        return CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, event -> {
            PokemonBattle battle = event.getBattle();
            var modInstance = CobbledLevelControl.INSTANCE;
            var config = modInstance.getConfigManager().getMessagesConfig();
            for (BattleActor actor : battle.getActors()) {
                if (actor.getType() != ActorType.PLAYER) return Unit.INSTANCE;
                ServerPlayer player = ((PlayerBattleActor) actor).getEntity();
                if (player == null) return Unit.INSTANCE;
                var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
                String playerDiffValue = playerData.getDifficulty();
                if (!playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
                RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
                var battleModule = difficulty.getBattleModule();
                if (battleModule.doNotRestrictBattles()) return Unit.INSTANCE;

                // Only restrict battles that are Player vs Wild Pokemon
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

                var levelingModule = difficulty.getLevelingModule();
                if (levelingModule.doRestrictLeveling()) {
                    int levelingLevel = playerData.getLeveling();
                    int maxLevelingLevel = levelingModule.getConfig().tiers.get(levelingLevel);
                    if (conditionalCheck(player, maxLevel > maxLevelingLevel, config.errors.battle, config, event)) {
                        return Unit.INSTANCE;
                    }
                }

                var battleConfig = battleModule.getConfig();
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

                if (playerSide == null || pokemonSide == null) return Unit.INSTANCE;

                var activePokemon = pokemonSide.getActivePokemon();
                for (ActiveBattlePokemon activeMon : activePokemon) {
                    BattlePokemon battlePokemon = activeMon.getBattlePokemon();
                    if (battlePokemon == null) continue;
                    Pokemon pokemon = battlePokemon.getOriginalPokemon();

                    if (pokemon.getShiny() && conditionalCheck(player, battleConfig.shiny, config.errors.missingPermission, config, event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isLegendary() && conditionalCheck(player, battleConfig.legendary, config.errors.missingPermission, config, event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isMythical() && conditionalCheck(player, battleConfig.mythical, config.errors.missingPermission, config, event)) {
                        return Unit.INSTANCE;
                    }
                    if (pokemon.isUltraBeast() && conditionalCheck(player, battleConfig.ultraBeast, config.errors.missingPermission, config, event)) {
                        return Unit.INSTANCE;
                    }

                    PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
                    String perm = getPermissionString(evoStage, battleConfig);
                    if (!perm.isEmpty() && conditionalCheck(player, perm, config.errors.missingPermission, config, event)) {
                        return Unit.INSTANCE;
                    }
                }
            }
            return Unit.INSTANCE;
        });
    }

    private static String getPermissionString(PokemonUtility.EvoStage evoStage, DifficultyConfig.BattleConfig battleConfig) {
        String perm;
        switch (evoStage) {
            case PokemonUtility.EvoStage.FINAL -> perm = battleConfig.evolutionStages.finalStageEvo;
            case PokemonUtility.EvoStage.FIRST -> perm = battleConfig.evolutionStages.firstStageEvo;
            case PokemonUtility.EvoStage.SECOND -> perm = battleConfig.evolutionStages.secondStageEvo;
            default -> perm = battleConfig.evolutionStages.singleEvo;
        }
        return perm;
    }

    private static boolean conditionalCheck(ServerPlayer player, boolean condition, String errorMessage, MessagesConfig modConfig, BattleStartedEvent.Pre event) {
        if (condition) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            doCancel(event);
            return true;
        }
        return false;
    }

    private static boolean conditionalCheck(ServerPlayer player, String permissionNode, String errorMessage, MessagesConfig modConfig, BattleStartedEvent.Pre event) {
        if (!permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode)) {
            player.sendSystemMessage(Component.literal(errorMessage).withStyle(ChatFormatting.RED), modConfig.errors.useActionBar);
            doCancel(event);
            return true;
        }
        return false;
    }

    @SuppressWarnings("SameReturnValue")
    public static void doCancel(BattleStartedEvent.Pre event) {
        PokemonBattle battle = event.getBattle();
        event.cancel();
        battle.stop();
    }
}
