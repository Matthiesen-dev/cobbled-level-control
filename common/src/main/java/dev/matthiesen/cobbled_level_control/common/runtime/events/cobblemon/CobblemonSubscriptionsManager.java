package dev.matthiesen.cobbled_level_control.common.runtime.events.cobblemon;

import com.cobblemon.mod.common.api.reactive.ObservableSubscription;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CobblemonSubscriptionsManager {
    private static final Map<String, ObservableSubscription<?>> activeSubscriptions = new HashMap<>();
    private static final Map<String, Supplier<ObservableSubscription<?>>> subscriptionSuppliers = Map.of(
//            "battle-start", BattleStartEventsListener::register,
            "candy-use", CandyUseListener::register,
            "capture", CaptureListener::register,
            "experience", ExperienceGainedListener::register,
            "level-up", LevelUpListener::register,
            "spawn", PokemonSpawnListener::register,
            "evolution", EvolutionListener::register
    );

    public static void registerSubscriptions() {
        for (Map.Entry<String, Supplier<ObservableSubscription<?>>> entry : subscriptionSuppliers.entrySet()) {
            String key = entry.getKey();
            Supplier<ObservableSubscription<?>> supplier = entry.getValue();
            ObservableSubscription<?> subscription = supplier.get();
            activeSubscriptions.put(key, subscription);
        }
    }

    public static void teardownAllActiveSubscriptions() {
        for (Map.Entry<String, ObservableSubscription<?>> entry : activeSubscriptions.entrySet()) {
            ObservableSubscription<?> subscription = entry.getValue();
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
        activeSubscriptions.clear();
    }
}
