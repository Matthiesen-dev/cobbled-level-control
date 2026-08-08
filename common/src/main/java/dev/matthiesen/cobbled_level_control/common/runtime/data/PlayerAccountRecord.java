package dev.matthiesen.cobbled_level_control.common.runtime.data;

import com.cobblemon.mod.common.api.molang.ObjectValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;

public final class PlayerAccountRecord {
    public int catching = 1;
    public int leveling = 1;

    public PlayerAccountRecord() {}

    public PlayerAccountRecord(int catching, int leveling) {
        this.catching = catching;
        this.leveling = leveling;
    }

    public void setCatching(int catching) {
        this.catching = catching;
    }

    public void setLeveling(int leveling) {
        this.leveling = leveling;
    }

    public int getCatching() {
        return this.catching;
    }

    public int getLeveling() {
        return this.leveling;
    }

    public CompoundTag toNBT() {
        CompoundTag accountRecordNBT = new CompoundTag();
        accountRecordNBT.put("catching", IntTag.valueOf(this.catching));
        accountRecordNBT.put("leveling", IntTag.valueOf(this.leveling));
        return accountRecordNBT;
    }

    public static PlayerAccountRecord fromNBT(CompoundTag compoundTag) {
        int catching = compoundTag.getInt("catching");
        int leveling = compoundTag.getInt("leveling");
        return new PlayerAccountRecord(catching, leveling);
    }

    public static String makeString(PlayerAccountRecord data) {
        return "{" +
                "\"catching\": " + data.catching + ", " +
                "\"leveling\": " + data.leveling +
                "}";
    }

    public ObjectValue<PlayerAccountRecord> asMolangValue() {
        return new ObjectValue<>(this, PlayerAccountRecord::makeString, d -> 1.0);
    }
}
