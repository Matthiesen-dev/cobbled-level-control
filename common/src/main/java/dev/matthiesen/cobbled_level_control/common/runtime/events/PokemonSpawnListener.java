package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.utils.ScalingUtils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

public final class PokemonSpawnListener {
    public static ObservableSubscription<SpawnEvent<PokemonEntity>> register() {
        return CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {
            var modInstance = CobbledLevelControl.INSTANCE;
            if (!CLCConfig.SERVER_CONFIG.scaling_enableScaling.get()) return Unit.INSTANCE;
            PokemonEntity entity = event.getEntity();
            Pokemon pokemon = entity.getPokemon();
            if (pokemon.isPlayerOwned()) return Unit.INSTANCE;
            if (event.getSpawnablePosition().getCause().getEntity() instanceof ServerPlayer player) {
                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
                var catchingModule = CLCConfig.getCatchingConfig();
                if (catchingModule.doNotRestrictCatching()) return Unit.INSTANCE;
                int tierLevel = playerData.getCatching();
                int maxLevel = catchingModule.tiers().get(Integer.toString(tierLevel));
                String scalingMethod = CLCConfig.SERVER_CONFIG.scaling_scalingMethod.get();
                int newLevel = ScalingUtils.getNewLevel(maxLevel, scalingMethod);
                pokemon.setLevel(newLevel);
            }
            return Unit.INSTANCE;
        });
    }
}
