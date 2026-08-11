package dev.matthiesen.cobbled_level_control.common.runtime.data;

import net.minecraft.world.entity.player.Player;

public interface ILevelControlData {
    Player player();
    PlayerAccountRecord accountRecord();

    default String makeString(ILevelControlData data) {
        return "{" +
                "\"playerUUID\": \"" + data.player().getUUID() + "\"," +
                "\"accountRecord\": " + PlayerAccountRecord.makeString(data.accountRecord()) +
                "}";
    }
}
