package dev.matthiesen.cobbled_level_control.common.config;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class PlayerAccountRecord {
    public String difficulty = "none";
    public int catching = 1;
    public int leveling = 1;

    public PlayerAccountRecord(@Nullable String difficulty) {
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
    }

    public PlayerAccountRecord(String difficulty, int catching, int leveling) {
        this.difficulty = difficulty;
        this.catching = catching;
        this.leveling = leveling;
    }

    public PlayerAccountRecord setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public PlayerAccountRecord setCatching(int catching) {
        this.catching = catching;
        return this;
    }

    public PlayerAccountRecord setLeveling(int leveling) {
        this.leveling = leveling;
        return this;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public int getCatching() {
        return this.catching;
    }

    public int getLeveling() {
        return this.leveling;
    }
}
