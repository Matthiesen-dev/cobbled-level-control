package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;

import java.util.Map;

public record LevelingConfig(
        Boolean restrictLeveling,
        EvolutionStages evolutionStages,
        Map<String, Integer> tiers
) {
    public static LevelingConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        EvolutionStages evoStages = new EvolutionStages(
                new PermSet(SERVER_CONFIG.levelingModule_evoStages_singleEvo.get(), PermissionHelpers.LEVELING_EVOSTAGES_SINGLE_PERMISSION),
                new PermSet(SERVER_CONFIG.levelingModule_evoStages_firstStageEvo.get(), PermissionHelpers.LEVELING_EVOSTAGES_FIRST_PERMISSION),
                new PermSet(SERVER_CONFIG.levelingModule_evoStages_secondStageEvo.get(), PermissionHelpers.LEVELING_EVOSTAGES_SECOND_PERMISSION),
                new PermSet(SERVER_CONFIG.levelingModule_evoStages_finalStageEvo.get(), PermissionHelpers.LEVELING_EVOSTAGES_FINAL_PERMISSION)
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
