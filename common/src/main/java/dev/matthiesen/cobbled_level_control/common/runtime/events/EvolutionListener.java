package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionTestedEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.utils.PokemonUtility;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public final class EvolutionListener {
    public static ObservableSubscription<EvolutionTestedEvent> register() {
        return CobblemonEvents.EVOLUTION_TESTED.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player == null) return Unit.INSTANCE;
            var levelingModule = CLCConfig.getLevelingConfig();
            if (levelingModule.doNotRestrictLeveling()) return Unit.INSTANCE;
            PokemonUtility.EvoStage evoStage = PokemonUtility.getEvoStage(pokemon);
            String perm = Util.getPermissionString(evoStage, levelingModule);
            if (!perm.isEmpty() && Util.conditionalCheck(player, perm)) {
                event.setResult(false);
                return Unit.INSTANCE;
            }
           return Unit.INSTANCE;
        });
    }
}
