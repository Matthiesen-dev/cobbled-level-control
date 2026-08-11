package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;

public record BattleConfig(
        Boolean restrictBattles,
        String legendary,
        String mythical,
        String ultraBeast,
        String shiny,
        EvolutionStages evolutionStages
) {
    public static BattleConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        EvolutionStages evoStages = new EvolutionStages(
                SERVER_CONFIG.battleModule_evoStages_singleEvo.get(),
                SERVER_CONFIG.battleModule_evoStages_firstStageEvo.get(),
                SERVER_CONFIG.battleModule_evoStages_secondStageEvo.get(),
                SERVER_CONFIG.battleModule_evoStages_finalStageEvo.get()
        );
        return new BattleConfig(
                SERVER_CONFIG.battleModule_restrictBattles.getAsBoolean(),
                SERVER_CONFIG.battleModule_legendary.get(),
                SERVER_CONFIG.battleModule_mythical.get(),
                SERVER_CONFIG.battleModule_ultraBeast.get(),
                SERVER_CONFIG.battleModule_shiny.get(),
                evoStages
        );
    }

    public boolean doNotRestrictBattles() {
        return !restrictBattles;
    }
}
