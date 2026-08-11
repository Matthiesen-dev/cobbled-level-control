package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;

import java.util.Map;

public record CatchingConfig(
        Boolean restrictCatching,
        String legendary,
        String mythical,
        String ultraBeast,
        String shiny,
        EvolutionStages evolutionStages,
        Map<String, Integer> tiers
) {
    public static CatchingConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        EvolutionStages evoStages = new EvolutionStages(
                SERVER_CONFIG.catchingModule_evoStages_singleEvo.get(),
                SERVER_CONFIG.catchingModule_evoStages_firstStageEvo.get(),
                SERVER_CONFIG.catchingModule_evoStages_secondStageEvo.get(),
                SERVER_CONFIG.catchingModule_evoStages_finalStageEvo.get()
        );
        Map<String, Integer> tiers = TierConfig.parseTiers(SERVER_CONFIG.catchingModule_tiersConfig.get());
        return new CatchingConfig(
                SERVER_CONFIG.catchingModule_restrictCatching.getAsBoolean(),
                SERVER_CONFIG.catchingModule_legendary.get(),
                SERVER_CONFIG.catchingModule_mythical.get(),
                SERVER_CONFIG.catchingModule_ultraBeast.get(),
                SERVER_CONFIG.catchingModule_shiny.get(),
                evoStages,
                tiers
        );
    }

    public boolean doNotRestrictCatching() {
        return !restrictCatching;
    }
}
