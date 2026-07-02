package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.config.*;
import dev.matthiesen.cobbled_level_control.common.runtime.*;
import dev.matthiesen.common.matthiesen_lib_api.config.ConfigFolderManager;
import dev.matthiesen.common.matthiesen_lib_api.config.ConfigManager;

public final class CobbledLevelControlConfigManager {
    private final CobbledLevelControl INSTANCE;

    private ConfigManager<MainConfig> MAIN_CONFIG;
    private ConfigManager<MessagesConfig> MESSAGES_CONFIG;

    private ConfigFolderManager<DifficultyConfig> DIFFICULTY_CONFIGS;

    public CobbledLevelControlConfigManager(CobbledLevelControl modInstance) {
        this.INSTANCE = modInstance;
    }

    public void init() {
        MAIN_CONFIG = INSTANCE.createConfigManager(MainConfig.class, "main");
        MESSAGES_CONFIG = INSTANCE.createConfigManager(MessagesConfig.class, "messages");
        DIFFICULTY_CONFIGS = INSTANCE.createConfigFolderManager(DifficultyConfig.class, "difficulties");

        loadConfigs();
    }

    public void loadConfigs() {
        INSTANCE.createInfoLog("Loading configs...");
        MAIN_CONFIG.loadConfig();
        MESSAGES_CONFIG.loadConfig();
        DIFFICULTY_CONFIGS.loadConfigs();

        INSTANCE.createInfoLog("Loaded configs! Loading difficulties...");

        // Ensure all registered difficulties have a config and are registered
        var difficulties = MAIN_CONFIG.getConfig().difficulties;
        for (String difficulty : difficulties) {
            var loadedConfig = DIFFICULTY_CONFIGS.loadConfig(difficulty);

            RuntimeDifficulty difficultyRuntime = new RuntimeDifficulty(difficulty, loadedConfig);
            difficultyRuntime.addToRuntime();
        }

        INSTANCE.createInfoLog("Loaded all difficulties!");
    }

    public MainConfig getMainConfig() {
        return MAIN_CONFIG.getConfig();
    }

    public MessagesConfig getMessagesConfig() {
        return MESSAGES_CONFIG.getConfig();
    }
}
