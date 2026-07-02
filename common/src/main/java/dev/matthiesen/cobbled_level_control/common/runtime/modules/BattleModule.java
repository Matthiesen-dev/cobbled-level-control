package dev.matthiesen.cobbled_level_control.common.runtime.modules;

import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;

public class BattleModule {
    private final DifficultyConfig.BattleConfig battleConfig;

    public BattleModule(DifficultyConfig.BattleConfig battleConfig) {
        this.battleConfig = battleConfig;
    }

    public boolean doRestrictBattles() {
        return this.battleConfig.restrictBattles;
    }

    public DifficultyConfig.BattleConfig getConfig() {
        return this.battleConfig;
    }
}
