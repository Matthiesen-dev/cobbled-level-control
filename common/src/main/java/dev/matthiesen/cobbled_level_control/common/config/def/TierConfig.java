package dev.matthiesen.cobbled_level_control.common.config.def;

import com.electronwill.nightconfig.core.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record TierConfig(String tier, Integer level) {

    private static final List<TierConfig> DEFAULT_TIERS = List.of(
            new TierConfig("1", 10),
            new TierConfig("2", 20),
            new TierConfig("3", 40),
            new TierConfig("4", 80),
            new TierConfig("5", 100)
    );

    private static final List<Config> DEFAULT_TIERS_CONFIG = DEFAULT_TIERS.stream()
            .map(TierConfig::serialize)
            .toList();

    public static List<Config> getDefaultTiersConfig() {
        return DEFAULT_TIERS_CONFIG;
    }

    public static TierConfig deserialize(Config config) {
        String tier = config.get("tier");
        int level = config.getInt("level");
        return new TierConfig(tier, level);
    }

    public static boolean isValid(Object object) {
        if (!(object instanceof Config config)) {
            return false;
        }
        String tier = config.get("tier");
        int level = config.getInt("level");
        return tier != null && !tier.isEmpty() && level > 0;
    }

    public static Map<String, Integer> parseTiers(List<? extends Config> configs) {
        Map<String, Integer> tiers = new HashMap<>();
        for (Config config : configs) {
            TierConfig tierConfig = deserialize(config);
            tiers.put(tierConfig.tier(), tierConfig.level());
        }
        return tiers;
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("tier", tier());
        config.set("level", level());
        return config;
    }
}
