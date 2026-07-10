package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.commands.LevelControlCommand;
import dev.matthiesen.cobbled_level_control.common.config.CobbledLevelControlConfigManager;
import dev.matthiesen.cobbled_level_control.common.runtime.data.StoredPlayerAccountRecords;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.events.PlayerEvents;
import dev.matthiesen.cobbled_level_control.common.runtime.events.ServerEvents;
import dev.matthiesen.cobbled_level_control.common.runtime.molang.PlayerExtensions;
import dev.matthiesen.common.matthiesen_lib_api.abstracts.AbstractCommonMod;
import dev.matthiesen.libs.faststats.Token;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class CobbledLevelControl extends AbstractCommonMod {
    public static final String MOD_ID = "cobbled_level_control";
    public static final String MOD_NAME = "Cobbled Level Control";
    private static @Token final String METRICS_TOKEN = "00c30fedc5bd584dd1060bada0f2637a";
    private final CobbledLevelControlConfigManager configManager;
    private final Map<String, RuntimeDifficulty> difficulties = new HashMap<>();
    private StoredPlayerAccountRecords storedPlayerAccountRecords;

    public static final CobbledLevelControl INSTANCE = new CobbledLevelControl();

    public CobbledLevelControl() {
        super(MOD_ID, MOD_NAME);
        configManager = new CobbledLevelControlConfigManager(this);
    }

    @Override
    public void initialize() {
        super.initialize();
        configManager.init();
        PermissionHelpers.init();
        registerServerEventHandler(new ServerEvents());
        registerPlayerEventHandler(new PlayerEvents());
        registerCommand(LevelControlCommand.CMD);
        PlayerExtensions.init();

        createInfoLog("Initialized");
    }

    @Override
    public Runnable reload() {
        return () -> {
            difficulties.clear();
            configManager.loadConfigs();
            createInfoLog("Reloaded configs!");
        };
    }

    @Override
    public @Token @NotNull String getMetricsToken() {
        return METRICS_TOKEN;
    }

    public CobbledLevelControlConfigManager getConfigManager() {
        return configManager;
    }

    public void addDifficulty(RuntimeDifficulty difficulty) {
        difficulties.put(difficulty.getDifficultyName(), difficulty);
    }

    public RuntimeDifficulty getDifficulty(String key) {
        return difficulties.get(key);
    }

    public StoredPlayerAccountRecords getStoredPlayerAccountRecords() {
        if (storedPlayerAccountRecords == null) {
            storedPlayerAccountRecords = StoredPlayerAccountRecords.getInstance();
        }
        return storedPlayerAccountRecords;
    }
}
