package dev.matthiesen.cobbled_level_control.common.runtime;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;

public record Difficulty(String difficulty, Catching catching, Leveling leveling,
                         Battle battle) {

    public void add() {
        CobbledLevelControl.INSTANCE.addDifficulty(this);
    }
}
