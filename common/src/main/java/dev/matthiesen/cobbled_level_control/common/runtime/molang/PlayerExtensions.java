package dev.matthiesen.cobbled_level_control.common.runtime.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.molang.ObjectValue;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.data.LevelControlData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.function.Function;

public final class PlayerExtensions {

    private static ObjectValue<LevelControlData> buildLevelControlObject(Player player) {
        var modInstance = CobbledLevelControl.INSTANCE;
        var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return null;
        var data = new LevelControlData(player, playerData);
        return data.asMolangValue();
    }

    public static void init() {
        CobbledLevelControl.INSTANCE.createInfoLog("Registering MoLang Player Extensions...");

        MoLangFunctions.INSTANCE.getPlayerFunctions().add(player -> {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.level_control() -> { "playerUUID": "string", "accountRecord": { "catching": number, "leveling": number } }
            // q.player.level_control.status() returns following object or 0
            // { "difficulty": "string", "catching": int, "leveling": int }
            // q.player.level_control.lvlup(<module string>) returns 1 for success or 0
            // q.player.level_control.setlvl(<module string>, <lvl int>) returns 1 for success or 0
            map.put("level_control", params -> buildLevelControlObject(player));

            return map;
        });
    }
}
