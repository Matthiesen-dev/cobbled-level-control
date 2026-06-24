package dev.matthiesen.cobbled_level_control.common.config;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.common.matthiesen_lib_api.config.ConfigManager;

import java.util.UUID;
import java.util.function.Consumer;

public final class ConfigRegistry {
    private final CobbledLevelControl INSTANCE;

    private ConfigManager<MainConfig> MAIN_CONFIG;
    private ConfigManager<PlayerAccountsConfig> PLAYER_ACCOUNTS_CONFIG;

    public ConfigRegistry(CobbledLevelControl modInstance) {
        this.INSTANCE = modInstance;
    }

    public void init() {
        MAIN_CONFIG = INSTANCE.createConfigManager(MainConfig.class, "main");
        PLAYER_ACCOUNTS_CONFIG = INSTANCE.createConfigManager(PlayerAccountsConfig.class, "player_accounts");

        loadConfigs();
    }

    public void loadConfigs() {
        MAIN_CONFIG.loadConfig();
        PLAYER_ACCOUNTS_CONFIG.loadConfig();
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
        if (accountsConfig.accounts.containsKey(playerUUID)) {
            accountsConfig.accounts.replace(playerUUID, record);
        } else {
            accountsConfig.accounts.put(playerUUID, record);
        }
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
