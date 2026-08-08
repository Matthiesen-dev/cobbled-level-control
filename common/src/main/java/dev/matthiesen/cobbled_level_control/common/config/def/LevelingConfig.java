package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;

import java.util.Map;

public record LevelingConfig(
        Boolean restrictLeveling,
        EvolutionStages evolutionStages,
        Map<String, Integer> tiers
) {
    public static LevelingConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        EvolutionStages evoStages = new EvolutionStages(
                SERVER_CONFIG.levelingModule_evoStages_singleEvo.get(),
                SERVER_CONFIG.levelingModule_evoStages_firstStageEvo.get(),
                SERVER_CONFIG.levelingModule_evoStages_secondStageEvo.get(),
                SERVER_CONFIG.levelingModule_evoStages_finalStageEvo.get()
        );
        Map<String, Integer> tiers = TierConfig.parseTiers(SERVER_CONFIG.levelingModule_tiersConfig.get());
        return new LevelingConfig(
                SERVER_CONFIG.levelingModule_restrictLeveling.getAsBoolean(),
                evoStages,
                tiers
        );
    }

    public boolean doNotRestrictLeveling() {
        return !restrictLeveling;
    }

    public boolean doRestrictLeveling() {
        return restrictLeveling;
    }
}
