package dev.matthiesen.cobbled_level_control.common.runtime.molang.data;

import dev.matthiesen.cobbled_level_control.common.runtime.PlayerAccountRecord;
import net.minecraft.world.entity.player.Player;

public interface ILevelControlData {
    Player player();
    PlayerAccountRecord accountRecord();

    default String baseJsonFields() {
        return "\"playerUUID\": \"" + player().getUUID() + "\"," +
                "\"accountRecord\": " + PlayerAccountRecord.makeString(accountRecord());
    }
}
