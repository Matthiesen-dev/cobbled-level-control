package dev.matthiesen.cobbled_level_control.common.runtime.molang.data;

import com.cobblemon.mod.common.api.molang.ObjectValue;
import dev.matthiesen.cobbled_level_control.common.runtime.PlayerAccountRecord;
import net.minecraft.world.entity.player.Player;

public record LevelControlData(Player player, PlayerAccountRecord accountRecord) implements ILevelControlData {
    public String makeString(LevelControlData data) {
        return "{" + data.baseJsonFields() + "}";
    }

    public ObjectValue<LevelControlData> asMolangValue() {
        return new ObjectValue<>(this, this::makeString, d -> 1.0);
    }
}
