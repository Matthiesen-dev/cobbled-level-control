package dev.matthiesen.cobbled_level_control.common.runtime;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;
import dev.matthiesen.cobbled_level_control.common.runtime.modules.BattleModule;
import dev.matthiesen.cobbled_level_control.common.runtime.modules.CatchingModule;
import dev.matthiesen.cobbled_level_control.common.runtime.modules.LevelingModule;

public final class RuntimeDifficulty {
    public static final String emptyDifficulty = "none";

    private final String difficulty;
    private final DifficultyConfig difficultyConfig;

    public RuntimeDifficulty(String difficulty, DifficultyConfig config) {
        this.difficulty = difficulty;
        this.difficultyConfig = config;
    }

    public void addToRuntime() {
        CobbledLevelControl.INSTANCE.addDifficulty(this);
    }

    public String getDifficultyName() {
        return this.difficulty;
    }

    public BattleModule getBattleModule() {
        return new BattleModule(this.difficultyConfig.battles);
    }

    public CatchingModule getCatchingModule() {
        return new CatchingModule(this.difficultyConfig.catching);
    }

    public LevelingModule getLevelingModule() {
        return new LevelingModule(this.difficultyConfig.leveling);
    }
}
