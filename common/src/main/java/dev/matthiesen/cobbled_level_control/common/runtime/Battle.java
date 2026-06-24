package dev.matthiesen.cobbled_level_control.common.runtime;

import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;

public record Battle(DifficultyConfig.BattleConfig config) {
    public boolean doCheckBattles() {
        return this.config.restrictBattles;
    }
}
