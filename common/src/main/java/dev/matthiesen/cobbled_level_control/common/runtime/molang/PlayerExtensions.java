package dev.matthiesen.cobbled_level_control.common.runtime.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import com.cobblemon.mod.common.api.molang.ObjectValue;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.PlayerAccountRecord;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.molang.data.LevelControlData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class PlayerExtensions {
    private static Object getPlayerStatus(Player player) {
        return CobbledLevelControl.INSTANCE.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID()).asMolangValue();
    }

    private static Object getPlayerStatus(LevelControlData data) {
        return data.accountRecord().asMolangValue();
    }

    private static Object setPlayerDiff(MoParams params, Player player) {
        String diff = params.getString(0);
        if (diff.isEmpty()) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;
        var messagesConfig = modInstance.getConfigManager().getMessagesConfig();

        var diffNames = modInstance.getConfigManager().getMainConfig().difficulties;
        if (!diffNames.contains(diff)) return 0;

        modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setDifficulty(diff));
        player.sendSystemMessage(Component.literal(
                messagesConfig.messages.targetSetDifficulty
                        .replace("%difficulty%", diff)
        ).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static Object playerLevelUp(MoParams params, Player player, @Nullable LevelControlData data) {
        String module = params.getString(0);
        if (module.isEmpty()) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;
        var messagesConfig = modInstance.getConfigManager().getMessagesConfig();

        PlayerAccountRecord playerData = data != null ? data.accountRecord() : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return 0;

        String playerDiffValue = playerData.getDifficulty();
        if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return 0;

        RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
        if (difficulty == null) return 0;

        int level;
        int nextLevel;

        switch (module.toLowerCase()) {
            case "catch" -> {
                var catchingModule = difficulty.getCatchingModule();
                level = playerData.getCatching();
                nextLevel = level + 1;
                int maxLevel = catchingModule.getConfig().tiers.size();
                if (nextLevel > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(nextLevel));
                player.sendSystemMessage(Component.literal(
                        messagesConfig.messages.targetCatchingTierSet
                                .replace("%tier%", Integer.toString(nextLevel))
                ).withStyle(ChatFormatting.AQUA));
                return 1;
            }
            case "level" -> {
                var levelingModule = difficulty.getLevelingModule();
                level = playerData.getLeveling();
                nextLevel = level + 1;
                int maxLevel = levelingModule.getConfig().tiers.size();
                if (nextLevel > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(nextLevel));
                player.sendSystemMessage(Component.literal(
                        messagesConfig.messages.targetLevelingTierSet
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
        var messagesConfig = modInstance.getConfigManager().getMessagesConfig();

        PlayerAccountRecord playerData = data != null ? data.accountRecord() : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (playerData == null) return 0;

        String playerDiffValue = playerData.getDifficulty();
        if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return 0;

        RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
        if (difficulty == null) return 0;

        switch (module.toLowerCase()) {
            case "catch" -> {
                var catchingModule = difficulty.getCatchingModule();
                int maxLevel = catchingModule.getConfig().tiers.size();
                if (level > maxLevel) return 0;

                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setCatching(level));
                player.sendSystemMessage(Component.literal(
                        messagesConfig.messages.targetCatchingLevelSet
                                .replace("%level%", Integer.toString(level))
                ).withStyle(ChatFormatting.GREEN));
                return 1;
            }
            case "level" -> {
                var levelingModule = difficulty.getLevelingModule();
                int maxLevel = levelingModule.getConfig().tiers.size();
                if (level > maxLevel) return 0;
                modInstance.getStoredPlayerAccountRecords().editPlayerAccountRecord(player.getUUID(), record -> record.setLeveling(level));
                player.sendSystemMessage(Component.literal(
                        messagesConfig.messages.targetLevelingLevelSet
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

    private static Map<String,? extends Function<MoParams, Object>> getPlayerLevelControlFunctions(LevelControlData data) {
        HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

        // q.player.level_control().status() returns following object or 0
        // { "difficulty": "string", "catching": int, "leveling": int }
        map.put("status", params -> getPlayerStatus(data));

        // q.player.level_control().setdiff(<diff string>) returns 1 for success or 0
        map.put("setdiff", params -> setPlayerDiff(params, data.player()));

        // q.player.level_control().lvlup(<module string>) returns 1 for success or 0
        map.put("lvlup", params -> playerLevelUp(params, data.player(), data));

        // q.player.level_control().setlvl(<module string>, <lvl int>) returns 1 for success or 0
        map.put("setlvl", params -> setPlayerLevel(params, data.player(), data));

        return map;
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

    public static void init() {
        CobbledLevelControl.INSTANCE.createInfoLog("Registering MoLang Player Extensions...");

        MoLangFunctions.INSTANCE.getPlayerFunctions().add(player -> {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.level_control()
            map.put("level_control", params -> buildLevelControlObject(player));

            // q.player.lvl_ctrl_status() returns following object or 0
            // { "difficulty": "string", "catching": int, "leveling": int }
            map.put("lvl_ctrl_status", params -> getPlayerStatus(player));

            // q.player.lvl_ctrl_setdiff(<diff string>) returns 1 for success or 0
            map.put("lvl_ctrl_setdiff", params -> setPlayerDiff(params, player));

            // q.player.lvl_ctrl_lvlup(<module string>) returns 1 for success or 0
            map.put("lvl_ctrl_lvlup", params -> playerLevelUp(params, player, null));

            // q.player.lvl_ctrl_setlvl(<module string>, <lvl int>) returns 1 for success or 0
            map.put("lvl_ctrl_setlvl", params -> setPlayerLevel(params, player, null));

            return map;
        });
    }
}
