package dev.matthiesen.cobbled_level_control.common.runtime;

public record Battle(boolean checkBattles) {
    public boolean doCheckBattles() {
        return this.checkBattles;
    }
}
