package dev.matthiesen.cobbled_level_control.common.runtime.data;

import com.bedrockk.molang.runtime.MoParams;
import com.cobblemon.mod.common.api.molang.ObjectValue;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record LevelControlData(Player player, PlayerAccountRecord accountRecord) implements ILevelControlData {


    private Object getPlayerStatus(MoParams params) {
        return accountRecord().asMolangValue();
    }

    private Object playerLevelUp(MoParams params) {
        String module = params.getString(0);
        if (module.isEmpty()) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;

        PlayerAccountRecord playerData = accountRecord != null ? accountRecord : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
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

    private Object setPlayerLevel(MoParams params) {
        String module = params.getString(0);
        int level = params.getInt(1);
        if (module.isEmpty() || level == 0) return 0;

        var modInstance = CobbledLevelControl.INSTANCE;

        PlayerAccountRecord playerData = accountRecord != null ? accountRecord : modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
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

    private Map<String,? extends Function<MoParams, Object>> getPlayerLevelControlFunctions() {
        HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

        // q.player.level_control().status() returns following object or 0
        // { "difficulty": "string", "catching": int, "leveling": int }
        map.put("status", this::getPlayerStatus);

        // q.player.level_control().lvlup(<module string>) returns 1 for success or 0
        map.put("lvlup", this::playerLevelUp);

        // q.player.level_control().setlvl(<module string>, <lvl int>) returns 1 for success or 0
        map.put("setlvl", this::setPlayerLevel);

        return map;
    }

    public ObjectValue<LevelControlData> asMolangValue() {
        ObjectValue<LevelControlData> value = new ObjectValue<>(this, this::makeString, d -> 1.0);
        value.functions.putAll(getPlayerLevelControlFunctions());
        return value;
    }
}
