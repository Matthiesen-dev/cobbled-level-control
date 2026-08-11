package dev.matthiesen.cobbled_level_control.common.runtime.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.molang.ObjectValue;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.runtime.data.PlayerAccountRecord;
import dev.matthiesen.cobbled_level_control.common.runtime.data.LevelControlData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class PlayerExtensions {
    private static Object getPlayerStatus(LevelControlData data) {
        return data.accountRecord().asMolangValue();
    }

    private static Object playerLevelUp(MoParams params, Player player, @Nullable LevelControlData data) {
        String module = params.getString(0);
        if (module.isEmpty()) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;

        PlayerAccountRecord playerData = data != null ? data.accountRecord() : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return 0;

        int level;
        int nextLevel;

        switch (module.toLowerCase()) {
            case "catch" -> {
                var catchingModule = CLCConfig.getCatchingConfig();
                level = playerData.getCatching();
                nextLevel = level + 1;
                int maxLevel = catchingModule.tiers().size();
                if (nextLevel > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(nextLevel));
                player.sendSystemMessage(Component.literal(
                        CLCConfig.SERVER_CONFIG.messages_success_targetCatchingTierSet.get()
                                .replace("%tier%", Integer.toString(nextLevel))
                ).withStyle(ChatFormatting.AQUA));
                return 1;
            }
            case "level" -> {
                var levelingModule = CLCConfig.getLevelingConfig();
                level = playerData.getLeveling();
                nextLevel = level + 1;
                int maxLevel = levelingModule.tiers().size();
                if (nextLevel > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(nextLevel));
                player.sendSystemMessage(Component.literal(
                        CLCConfig.SERVER_CONFIG.messages_success_targetLevelingTierSet.get()
                                .replace("%tier%", Integer.toString(nextLevel))
                ).withStyle(ChatFormatting.AQUA));
                return 1;
            }
            default -> {
                CobbledLevelControl.INSTANCE.createInfoLog("Invalid module string passed to q.player.level_control().lvlup: " + module);
                return 0;
            }
        }
    }

    private static Object setPlayerLevel(MoParams params, Player player, @Nullable LevelControlData data) {
        String module = params.getString(0);
        int level = params.getInt(1);
        if (module.isEmpty() || level == 0) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;

        PlayerAccountRecord playerData = data != null ? data.accountRecord() : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return 0;

        switch (module.toLowerCase()) {
            case "catch" -> {
                var catchingModule = CLCConfig.getCatchingConfig();
                int maxLevel = catchingModule.tiers().size();
                if (level > maxLevel) return 0;

                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(level));
                player.sendSystemMessage(Component.literal(
                        CLCConfig.SERVER_CONFIG.messages_success_targetCatchingTierSet.get()
                                .replace("%level%", Integer.toString(level))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            case "level" -> {
                var levelingModule = CLCConfig.getLevelingConfig();
                int maxLevel = levelingModule.tiers().size();
                if (level > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(level));
                player.sendSystemMessage(Component.literal(
                        CLCConfig.SERVER_CONFIG.messages_success_targetLevelingTierSet.get()
                                .replace("%level%", Integer.toString(level))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            default -> {
                CobbledLevelControl.INSTANCE.createInfoLog("Invalid module string passed to q.player.level_control().setlvl: " + module);
                return 0;
            }
        }
    }

    private static ObjectValue<LevelControlData> buildLevelControlObject(Player player) {
        var modInstance = CobbledLevelControl.INSTANCE;
        var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return null;
        var data = new LevelControlData(player, playerData);
        var value = data.asMolangValue();
        value.functions.putAll(getPlayerLevelControlFunctions(data));
        return value;
    }

    private static Map<String,? extends Function<MoParams, Object>> getPlayerLevelControlFunctions(LevelControlData data) {
        HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

        // q.player.level_control().status() returns following object or 0
        // { "difficulty": "string", "catching": int, "leveling": int }
        map.put("status", params -> getPlayerStatus(data));

        // q.player.level_control().lvlup(<module string>) returns 1 for success or 0
        map.put("lvlup", params -> playerLevelUp(params, data.player(), data));

        // q.player.level_control().setlvl(<module string>, <lvl int>) returns 1 for success or 0
        map.put("setlvl", params -> setPlayerLevel(params, data.player(), data));

        return map;
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
