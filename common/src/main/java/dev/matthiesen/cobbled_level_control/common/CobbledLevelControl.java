package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.commands.LevelControlCommand;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.Difficulty;
import dev.matthiesen.cobbled_level_control.common.config.ConfigRegistry;
import dev.matthiesen.cobbled_level_control.common.events.*;
import dev.matthiesen.common.matthiesen_lib_api.abstracts.AbstractCommonMod;
import dev.matthiesen.libs.faststats.Token;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CobbledLevelControl extends AbstractCommonMod {
    public static final String MOD_ID = "cobbled_level_control";
    public static final String MOD_NAME = "Cobbled Level Control";
    private static @Token final String METRICS_TOKEN = "00c30fedc5bd584dd1060bada0f2637a";
    private boolean initialized;
    private final ConfigRegistry configRegistry;
    private final Map<String, Difficulty> difficulties = new HashMap<>();

    public static final CobbledLevelControl INSTANCE = new CobbledLevelControl();

    public CobbledLevelControl() {
        super(MOD_ID, MOD_NAME);
        configRegistry = new ConfigRegistry(this);
    }

    @Override
    public void initialize() {
        super.initialize();
        configRegistry.init();
        PermissionHelpers.init();
        registerServerEventHandler(new ServerEvents());
        registerPlayerEventHandler(new PlayerEvents());
        registerCommand(LevelControlCommand.CMD);

        if (!initialized) {
            initialized = true;
            createInfoLog("Initialized");
        }
    }

    @Override
    public Runnable reload() {
        return () -> {
            if (initialized) {
                configRegistry.savePlayerAccounts();
            }
            configRegistry.loadConfigs();
            createInfoLog("Saved Player Account Records, and reloaded configs!");
        };
    }

    @Override
    public @Token @NotNull String getMetricsToken() {
        return METRICS_TOKEN;
    }

    public ConfigRegistry getConfigRegistry() {
        return configRegistry;
    }

    public void addDifficulty(Difficulty difficulty) {
        difficulties.put(difficulty.difficulty(), difficulty);
    }

    public Difficulty getDifficulty(String key) {
        return difficulties.get(key);
    }

    public List<String> getDifficultyNames() {
        return difficulties.keySet().stream().toList();
    }
}
