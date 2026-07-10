package dev.matthiesen.cobbled_level_control.common.runtime.molang;

import com.bedrockk.molang.runtime.MoParams;
import com.cobblemon.mod.common.api.molang.MoLangFunctions;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.function.Function;

public final class PlayerExtensions {
    public static void init() {
        CobbledLevelControl.INSTANCE.createInfoLog("Registering MoLang Player Extensions...");

        MoLangFunctions.INSTANCE.getPlayerFunctions().add(player -> {
            HashMap<String, Function<MoParams, Object>> map = new HashMap<>();

            // q.player.lvl_ctrl_status() returns following object or 0
            // { "difficulty": "string", "catching": int, "leveling": int }
            map.put("lvl_ctrl_status", params -> {
                var record = CobbledLevelControl.INSTANCE.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
                if (record == null) return 0;
                return record.asMolangValue();
            });

            // q.player.lvl_ctrl_setdiff(<diff string>) returns 1 for success or 0
            map.put("lvl_ctrl_setdiff", params -> {
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
            });

            // q.player.lvl_ctrl_lvlup(<module string>) returns 1 for success or 0
            map.put("lvl_ctrl_lvlup", params -> {
                String module = params.getString(0);
                if (module.isEmpty()) return 0;

                var modInstance = CobbledLevelControl.INSTANCE;
                var messagesConfig = modInstance.getConfigManager().getMessagesConfig();

                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
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
                        CobbledLevelControl.INSTANCE.createInfoLog("Invalid module string passed to q.player.lvl_ctrl_lvlup: " + module);
                        return 0;
                    }
                }
            });

            // q.player.lvl_ctrl_setlvl(<module string>, <lvl int>) returns 1 for success or 0
            map.put("lvl_ctrl_setlvl", params -> {
                String module = params.getString(0);
                int level = params.getInt(1);
                if (module.isEmpty() || level == 0) return 0;

                var modInstance = CobbledLevelControl.INSTANCE;
                var messagesConfig = modInstance.getConfigManager().getMessagesConfig();

                var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
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
                        CobbledLevelControl.INSTANCE.createInfoLog("Invalid module string passed to q.player.lvl_ctrl_lvlup: " + module);
                        return 0;
                    }
                }
            });

            return map;
        });
    }
}
