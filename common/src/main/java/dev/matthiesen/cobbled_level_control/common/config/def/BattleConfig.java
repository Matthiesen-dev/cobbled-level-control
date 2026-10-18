package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;

public record BattleConfig(
        Boolean restrictBattles,
        PermSet legendary,
        PermSet mythical,
        PermSet ultraBeast,
        PermSet shiny,
        EvolutionStages evolutionStages
) {
    public static BattleConfig getConfig() {
        var SERVER_CONFIG = CLCConfig.SERVER_CONFIG;
        var legendaryPermSet = new PermSet(
                SERVER_CONFIG.battleModule_legendary.get(),
                PermissionHelpers.BATTLES_LEGENDARY_PERMISSION
        );
        var mythicalPermSet = new PermSet(
                SERVER_CONFIG.battleModule_mythical.get(),
                PermissionHelpers.BATTLES_MYTHICAL_PERMISSION
        );
        var ultraBeastPermSet = new PermSet(
                SERVER_CONFIG.battleModule_ultraBeast.get(),
                PermissionHelpers.BATTLES_ULTRA_BEAST_PERMISSION
        );
        var shinyPermSet = new PermSet(
                SERVER_CONFIG.battleModule_shiny.get(),
                PermissionHelpers.BATTLES_SHINY_PERMISSION
        );
        var evoStages = new EvolutionStages(
                new PermSet(SERVER_CONFIG.battleModule_evoStages_singleEvo.get(), PermissionHelpers.BATTLES_EVOSTAGES_SINGLE_PERMISSION),
                new PermSet(SERVER_CONFIG.battleModule_evoStages_firstStageEvo.get(), PermissionHelpers.BATTLES_EVOSTAGES_FIRST_PERMISSION),
                new PermSet(SERVER_CONFIG.battleModule_evoStages_secondStageEvo.get(), PermissionHelpers.BATTLES_EVOSTAGES_SECOND_PERMISSION),
                new PermSet(SERVER_CONFIG.battleModule_evoStages_finalStageEvo.get(), PermissionHelpers.BATTLES_EVOSTAGES_FINAL_PERMISSION)
        );
        return new BattleConfig(
                SERVER_CONFIG.battleModule_restrictBattles.getAsBoolean(),
                legendaryPermSet,
                mythicalPermSet,
                ultraBeastPermSet,
                shinyPermSet,
                evoStages
        );
    }

    public boolean doNotRestrictBattles() {
        return !restrictBattles;
    }
}
