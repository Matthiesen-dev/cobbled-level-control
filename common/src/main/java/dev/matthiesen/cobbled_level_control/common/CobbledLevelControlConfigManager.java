package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.config.*;
import dev.matthiesen.cobbled_level_control.common.runtime.*;
import dev.matthiesen.cobbled_level_control.common.utils.PlayerAccountRecord;
import dev.matthiesen.common.matthiesen_lib_api.config.ConfigFolderManager;
import dev.matthiesen.common.matthiesen_lib_api.config.ConfigManager;

import java.util.UUID;
import java.util.function.Consumer;

public final class CobbledLevelControlConfigManager {
    private final CobbledLevelControl INSTANCE;

    private ConfigManager<MainConfig> MAIN_CONFIG;
    private ConfigManager<PlayerAccountsConfig> PLAYER_ACCOUNTS_CONFIG;
    private ConfigFolderManager<DifficultyConfig> DIFFICULTY_CONFIGS;

    public CobbledLevelControlConfigManager(CobbledLevelControl modInstance) {
        this.INSTANCE = modInstance;
    }

    public void init() {
        MAIN_CONFIG = INSTANCE.createConfigManager(MainConfig.class, "main");
        PLAYER_ACCOUNTS_CONFIG = INSTANCE.createConfigManager(PlayerAccountsConfig.class, "player_accounts");
        DIFFICULTY_CONFIGS = INSTANCE.createConfigFolderManager(DifficultyConfig.class, "difficulties");

        loadConfigs();
        PLAYER_ACCOUNTS_CONFIG.loadConfig();
    }

    public void loadConfigs() {
        INSTANCE.createInfoLog("Loading configs...");
        MAIN_CONFIG.loadConfig();
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

    public void savePlayerAccounts() {
        PLAYER_ACCOUNTS_CONFIG.saveConfig();
    }

    public MainConfig getMainConfig() {
        return MAIN_CONFIG.getConfig();
    }

    public boolean hasPlayerAccountRecord(UUID playerUUID) {
        PlayerAccountsConfig accountsConfig = getPlayerAccountsConfig();
        return accountsConfig.accounts.containsKey(playerUUID);
    }

    public void createNewPlayerAccountRecord(UUID playerUUID) {
        var newAccount = createNewPlayerAccountsRecord();
        setPlayerAccountRecord(playerUUID, newAccount);
    }

    public PlayerAccountRecord getPlayerAccountRecord(UUID playerUUID) {
        PlayerAccountsConfig accountsConfig = getPlayerAccountsConfig();
        return accountsConfig.accounts.getOrDefault(playerUUID, createNewPlayerAccountsRecord());
    }

    public void setPlayerAccountRecord(UUID playerUUID, PlayerAccountRecord record) {
        PlayerAccountsConfig accountsConfig = getPlayerAccountsConfig();
        accountsConfig.accounts.put(playerUUID, record);
        updatePlayerAccounts(accountsConfig);
    }

    public void editPlayerAccountRecord(UUID playerUUID, Consumer<PlayerAccountRecord> record) {
        PlayerAccountsConfig accountsConfig = getPlayerAccountsConfig();
        PlayerAccountRecord accountRecord = accountsConfig.accounts.getOrDefault(playerUUID, createNewPlayerAccountsRecord());
        record.accept(accountRecord);
        accountsConfig.accounts.put(playerUUID, accountRecord);
        updatePlayerAccounts(accountsConfig);
    }

    // Internal Methods

    private void updatePlayerAccounts(PlayerAccountsConfig accountsConfig) {
        PLAYER_ACCOUNTS_CONFIG.setConfig(accountsConfig);
    }

    private PlayerAccountRecord createNewPlayerAccountsRecord() {
        return new PlayerAccountRecord(
                getMainConfig().defaults.autoApplyDefault ? getMainConfig().defaults.defaultDifficulty : null
        );
    }

    private PlayerAccountsConfig getPlayerAccountsConfig() {
        PlayerAccountsConfig accountsConfig = PLAYER_ACCOUNTS_CONFIG.getConfig();
        if (accountsConfig == null) {
            accountsConfig = PLAYER_ACCOUNTS_CONFIG.loadConfig();
        }
        return accountsConfig;
    }
}
