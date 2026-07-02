package dev.matthiesen.cobbled_level_control.common.runtime.data;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.PlayerAccountRecord;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class StoredPlayerAccountRecords extends SavedData {
    private final Map<String, CompoundTag> accounts = new HashMap<>();

    public StoredPlayerAccountRecords() {
    }

    public static StoredPlayerAccountRecords create() {
        return new StoredPlayerAccountRecords();
    }

    public static StoredPlayerAccountRecords load(CompoundTag nbt, HolderLookup.Provider provider) {
        StoredPlayerAccountRecords data = create();
        CompoundTag accountsNBT = nbt.getCompound("accounts");
        for (String key : accountsNBT.getAllKeys()) {
            data.accounts.put(key, accountsNBT.getCompound(key));
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag accountsNBT = new CompoundTag();
        accounts.forEach(accountsNBT::put);
        compoundTag.put("accounts", accountsNBT);
        return compoundTag;
    }

    public void updateAccountsStorage(Map<String, PlayerAccountRecord> runtimeMap) {
        accounts.clear();
        for (Map.Entry<String, PlayerAccountRecord> entry : runtimeMap.entrySet()) {
            accounts.put(entry.getKey(), entry.getValue().toNBT());
        }
        this.setDirty();
    }

    public Map<String, PlayerAccountRecord> getRuntimeAccounts() {
        Map<String, PlayerAccountRecord> runtimeMap = new HashMap<>();
        for (Map.Entry<String, CompoundTag> entry : accounts.entrySet()) {
            PlayerAccountRecord record = PlayerAccountRecord.fromNBT(entry.getValue());
            runtimeMap.put(entry.getKey(), record);
        }
        return runtimeMap;
    }

    public boolean hasPlayerAccountRecord(UUID playerUUID) {
        var accounts = getRuntimeAccounts();
        return accounts.containsKey(playerUUID.toString());
    }

    public void createNewPlayerAccountRecord(UUID playerUUID) {
        var accounts = getRuntimeAccounts();
        if (!accounts.containsKey(playerUUID.toString())) {
            PlayerAccountRecord newRecord = createNewPlayerAccountsRecord();
            setPlayerAccountRecord(playerUUID, newRecord);
        }
    }

    public PlayerAccountRecord getPlayerAccountRecord(UUID playerUUID) {
        var accounts = getRuntimeAccounts();
        return accounts.get(playerUUID.toString());
    }

    private PlayerAccountRecord createNewPlayerAccountsRecord() {
        var mainConfig = CobbledLevelControl.INSTANCE.getConfigManager().getMainConfig();
        return new PlayerAccountRecord(
                mainConfig.defaults.autoApplyDefault ? mainConfig.defaults.defaultDifficulty : null
        );
    }

    public void setPlayerAccountRecord(UUID playerUUID, PlayerAccountRecord record) {
        var accounts = getRuntimeAccounts();
        accounts.put(playerUUID.toString(), record);
        updateAccountsStorage(accounts);
    }

    public void editPlayerAccountRecord(UUID playerUUID, Consumer<PlayerAccountRecord> recordConsumer) {
        var accounts = getRuntimeAccounts();
        PlayerAccountRecord record = accounts.get(playerUUID.toString());
        if (record != null) {
            recordConsumer.accept(record);
            accounts.put(playerUUID.toString(), record);
            updateAccountsStorage(accounts);
        }
    }

    public static final SavedData.Factory<StoredPlayerAccountRecords> FACTORY = new Factory<>(
            StoredPlayerAccountRecords::create,
            StoredPlayerAccountRecords::load,
            null
    );

    public static StoredPlayerAccountRecords getInstance() {
        MinecraftServer server = CobbledLevelControl.INSTANCE.getMinecraftServer();
        if (server == null) return null;
        ServerLevel level = server.overworld();
        return level.getDataStorage().computeIfAbsent(StoredPlayerAccountRecords.FACTORY, CobbledLevelControl.MOD_ID + "_player_account_records");
    }
}
