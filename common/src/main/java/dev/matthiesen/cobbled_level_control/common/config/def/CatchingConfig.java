package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;

import java.util.Map;

public record CatchingConfig(
        Boolean restrictCatching,
        PermSet legendary,
        PermSet mythical,
        PermSet ultraBeast,
        PermSet shiny,
        EvolutionStages evolutionStages,
        Map<String, Integer> tiers
) {
    public static CatchingConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        var legendaryPermSet = new PermSet(
                SERVER_CONFIG.catchingModule_legendary.get(),
                PermissionHelpers.CATCHING_LEGENDARY_PERMISSION
        );
        var mythicalPermSet = new PermSet(
                SERVER_CONFIG.catchingModule_mythical.get(),
                PermissionHelpers.CATCHING_MYTHICAL_PERMISSION
        );
        var ultraBeastPermSet = new PermSet(
                SERVER_CONFIG.catchingModule_ultraBeast.get(),
                PermissionHelpers.CATCHING_ULTRA_BEAST_PERMISSION
        );
        var shinyPermSet = new PermSet(
                SERVER_CONFIG.catchingModule_shiny.get(),
                PermissionHelpers.CATCHING_SHINY_PERMISSION
        );
        EvolutionStages evoStages = new EvolutionStages(
                new PermSet(SERVER_CONFIG.catchingModule_evoStages_singleEvo.get(), PermissionHelpers.CATCHING_EVOSTAGES_SINGLE_PERMISSION),
                new PermSet(SERVER_CONFIG.catchingModule_evoStages_firstStageEvo.get(), PermissionHelpers.CATCHING_EVOSTAGES_FIRST_PERMISSION),
                new PermSet(SERVER_CONFIG.catchingModule_evoStages_secondStageEvo.get(), PermissionHelpers.CATCHING_EVOSTAGES_SECOND_PERMISSION),
                new PermSet(SERVER_CONFIG.catchingModule_evoStages_finalStageEvo.get(), PermissionHelpers.CATCHING_EVOSTAGES_FINAL_PERMISSION)
        );
        Map<String, Integer> tiers = TierConfig.parseTiers(SERVER_CONFIG.catchingModule_tiersConfig.get());
        return new CatchingConfig(
                SERVER_CONFIG.catchingModule_restrictCatching.getAsBoolean(),
                legendaryPermSet,
                mythicalPermSet,
                ultraBeastPermSet,
                shinyPermSet,
                evoStages,
                tiers
        );
    }

    public boolean doNotRestrictCatching() {
        return !restrictCatching;
    }
}
