package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

public final class CobblemonSubscriptionsManager {
    private static ObservableSubscription<BattleStartedEvent.Pre> battleStartSubscription;
    private static ObservableSubscription<ExperienceCandyUseEvent.Pre> experienceCandyUseSubscription;
    private static ObservableSubscription<ThrownPokeballHitEvent> thrownPokeballHitSubscription;
    private static ObservableSubscription<ExperienceGainedEvent.Pre> experienceGainedSubscription;
    private static ObservableSubscription<LevelUpEvent> levelUpSubscription;
    private static ObservableSubscription<SpawnEvent<PokemonEntity>> spawnSubscription;

    public static void setupSubscriptions() {
        battleStartSubscription = BattleStartEventsListener.register();
        experienceCandyUseSubscription = CandyUseListener.register();
        thrownPokeballHitSubscription = CaptureListener.register();
        experienceGainedSubscription = ExperienceGainedListener.register();
        levelUpSubscription = LevelUpListener.register();
        spawnSubscription = PokemonSpawnListener.register();
    }

    public static void teardownSubscriptions() {
        if (battleStartSubscription != null) {
            battleStartSubscription.unsubscribe();
            battleStartSubscription = null;
        }
        if (experienceCandyUseSubscription != null) {
            experienceCandyUseSubscription.unsubscribe();
            experienceCandyUseSubscription = null;
        }
        if (thrownPokeballHitSubscription != null) {
            thrownPokeballHitSubscription.unsubscribe();
            thrownPokeballHitSubscription = null;
        }
        if (experienceGainedSubscription != null) {
            experienceGainedSubscription.unsubscribe();
            experienceGainedSubscription = null;
        }
        if (levelUpSubscription != null) {
            levelUpSubscription.unsubscribe();
            levelUpSubscription = null;
        }
        if (spawnSubscription != null) {
            spawnSubscription.unsubscribe();
            spawnSubscription = null;
        }
    }
}
