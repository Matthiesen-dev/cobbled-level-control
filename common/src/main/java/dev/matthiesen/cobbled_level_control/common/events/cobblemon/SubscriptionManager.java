package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.pokeball.ThrownPokeballHitEvent;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;

public final class SubscriptionManager {
    public static ObservableSubscription<BattleStartedEvent.Pre> battleStartSubscription;
    public static ObservableSubscription<ExperienceCandyUseEvent.Pre> experienceCandyUseSubscription;
    public static ObservableSubscription<ThrownPokeballHitEvent> thrownPokeballHitSubscription;

    public static void setupSubscriptions() {
        battleStartSubscription = BattleStartEventsListener.register();
        experienceCandyUseSubscription = CandyUseListener.register();
        thrownPokeballHitSubscription = CaptureListener.register();
    }

    public static void teardownSubscriptions() {
        if (battleStartSubscription != null) {
            battleStartSubscription.unsubscribe();
            battleStartSubscription = null;
        }
        if (experienceCandyUseSubscription != null) {
            experienceCandyUseSubscription.unsubscribe();
            battleStartSubscription = null;
        }
        if (thrownPokeballHitSubscription != null) {
            thrownPokeballHitSubscription.unsubscribe();
            battleStartSubscription = null;
        }
    }
}
