package dev.matthiesen.cobbled_level_control.common.runtime;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.DifficultyConfig;

public final class RuntimeDifficulty {
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

    public DifficultyConfig.CatchingConfig getCatchingModule() {
        return this.difficultyConfig.catching;
    }

    public DifficultyConfig.LevelingConfig getLevelingModule() {
        return this.difficultyConfig.leveling;
    }

    public static class BattleModule {
        private final DifficultyConfig.BattleConfig battleConfig;

        public BattleModule(DifficultyConfig.BattleConfig battleConfig) {
            this.battleConfig = battleConfig;
        }

        public boolean doCheckBattles() {
            return this.battleConfig.restrictBattles;
        }
    }
}
