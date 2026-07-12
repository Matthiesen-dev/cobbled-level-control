package dev.matthiesen.cobbled_level_control.common.runtime.modules;

import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;

public final class LevelingModule {
    private final DifficultyConfig.LevelingConfig levelingConfig;

    public LevelingModule(DifficultyConfig.LevelingConfig levelingConfig) {
        this.levelingConfig = levelingConfig;
    }

    public boolean doNotRestrictLeveling() {
        return !this.levelingConfig.restrictLeveling;
    }

    public boolean doRestrictLeveling() {
        return this.levelingConfig.restrictLeveling;
    }

    public DifficultyConfig.LevelingConfig getConfig() {
        return this.levelingConfig;
    }
}
