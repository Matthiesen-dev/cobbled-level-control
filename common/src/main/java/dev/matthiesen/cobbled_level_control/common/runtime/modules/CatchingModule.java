package dev.matthiesen.cobbled_level_control.common.runtime.modules;

import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;

public class CatchingModule {
    private final DifficultyConfig.CatchingConfig catchingConfig;

    public CatchingModule(DifficultyConfig.CatchingConfig catchingConfig) {
        this.catchingConfig = catchingConfig;
    }

    public boolean doNotRestrictCatching() {
        return !this.catchingConfig.restrictCatching;
    }

    public DifficultyConfig.CatchingConfig getConfig() {
        return this.catchingConfig;
    }
}
