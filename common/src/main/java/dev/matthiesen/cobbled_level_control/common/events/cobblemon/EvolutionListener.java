package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionTestedEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.Difficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.Leveling;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public final class EvolutionListener {
    public static ObservableSubscription<EvolutionTestedEvent> register() {
        return CobblemonEvents.EVOLUTION_TESTED.subscribe(Priority.NORMAL, event -> {
            var modInstance = CobbledLevelControl.INSTANCE;

            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player == null) return Unit.INSTANCE;

            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();

            if (playerDiffValue.equalsIgnoreCase("none")) return Unit.INSTANCE;

            Difficulty difficulty = modInstance.getDifficulty(playerDiffValue);

            Leveling levelingModule = difficulty.leveling();

            PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
            String perm;

            switch (evoStage) {
                case PokemonUtility.EvoStage.FINAL -> perm = levelingModule.config().evolutionStages.finalStageEvo;
                case PokemonUtility.EvoStage.FIRST -> perm = levelingModule.config().evolutionStages.firstStageEvo;
                case PokemonUtility.EvoStage.SECOND -> perm = levelingModule.config().evolutionStages.secondStageEvo;
                default -> perm = levelingModule.config().evolutionStages.singleEvo;
            }

            if (!perm.isEmpty() && conditionalCheck(player, perm)) {
                event.setResult(false);
                return Unit.INSTANCE;
            }

           return Unit.INSTANCE;
        });
    }

    private static boolean conditionalCheck(ServerPlayer player, String permissionNode) {
        if (!permissionNode.isEmpty()) {
            // We don't send a message here because this runs during a TEST event
            return PermissionHelpers.doesNotHavePermission(player, permissionNode);
        }
        return false;
    }
}
