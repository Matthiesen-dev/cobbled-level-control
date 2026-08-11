package dev.matthiesen.cobbled_level_control.common.runtime.data;

import com.cobblemon.mod.common.api.molang.ObjectValue;
import net.minecraft.world.entity.player.Player;

public record LevelControlData(Player player, PlayerAccountRecord accountRecord) implements ILevelControlData {
    public ObjectValue<LevelControlData> asMolangValue() {
        return new ObjectValue<>(this, this::makeString, d -> 1.0);
    }
}
