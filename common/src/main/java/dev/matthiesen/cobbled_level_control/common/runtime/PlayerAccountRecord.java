package dev.matthiesen.cobbled_level_control.common.runtime;

import com.cobblemon.mod.common.api.molang.ObjectValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class PlayerAccountRecord {
    public String difficulty = RuntimeDifficulty.emptyDifficulty;
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

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCatching(int catching) {
        this.catching = catching;
    }

    public void setLeveling(int leveling) {
        this.leveling = leveling;
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

    public CompoundTag toNBT() {
        CompoundTag accountRecordNBT = new CompoundTag();
        accountRecordNBT.put("difficulty", StringTag.valueOf(this.difficulty));
        accountRecordNBT.put("catching", IntTag.valueOf(this.catching));
        accountRecordNBT.put("leveling", IntTag.valueOf(this.leveling));
        return accountRecordNBT;
    }

    public static PlayerAccountRecord fromNBT(CompoundTag compoundTag) {
        String difficulty = compoundTag.getString("difficulty");
        int catching = compoundTag.getInt("catching");
        int leveling = compoundTag.getInt("leveling");
        return new PlayerAccountRecord(difficulty, catching, leveling);
    }

    public static String makeString(PlayerAccountRecord data) {
        return "{" +
                "\"difficulty\": \"" + data.difficulty + "\", " +
                "\"catching\": " + data.catching + ", " +
                "\"leveling\": " + data.leveling +
                "}";
    }

    public ObjectValue<PlayerAccountRecord> asMolangValue() {
        return new ObjectValue<>(this, PlayerAccountRecord::makeString, d -> 1.0);
    }
}
