package dev.matthiesen.cobbled_level_control.common.config;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.*;
import dev.matthiesen.matthiesen_core.common.utility.config.ConfigFolderManager;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CobbledLevelControlConfigManager {
    public static final ServerConfig SERVER_CONFIG;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();
    }

    private final CobbledLevelControl INSTANCE;
    private ConfigFolderManager<DifficultyConfig> DIFFICULTY_CONFIGS;

    public CobbledLevelControlConfigManager(CobbledLevelControl modInstance) {
        this.INSTANCE = modInstance;
    }

    public void init() {
        DIFFICULTY_CONFIGS = INSTANCE.createConfigFolderManager(DifficultyConfig.class, "difficulties");

        loadConfigs();
    }

    public void loadConfigs() {
        INSTANCE.createInfoLog("Loaded configs! Loading difficulties...");
        DIFFICULTY_CONFIGS.loadConfigs();

        // Ensure all registered difficulties have a config and are registered
        var difficulties = CobbledLevelControlConfigManager.SERVER_CONFIG.difficulties.get();
        for (String difficulty : difficulties) {
            var loadedConfig = DIFFICULTY_CONFIGS.loadConfig(difficulty);

            RuntimeDifficulty difficultyRuntime = new RuntimeDifficulty(difficulty, loadedConfig);
            difficultyRuntime.addToRuntime();
        }

        INSTANCE.createInfoLog("Loaded all difficulties!");
    }
}
