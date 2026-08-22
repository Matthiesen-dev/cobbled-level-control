package dev.matthiesen.cobbled_level_control.common;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import dev.matthiesen.cobbled_level_control.common.commands.LevelControlCommand;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.network.CLCStatusHudSyncS2CPacket;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.data.StoredPlayerAccountRecords;
import dev.matthiesen.cobbled_level_control.common.runtime.events.CobblemonSubscriptionsManager;
import dev.matthiesen.cobbled_level_control.common.runtime.molang.PlayerExtensions;
import dev.matthiesen.libs.faststats.Token;
import dev.matthiesen.matthiesen_core.common.AbstractCommonMod;
import dev.matthiesen.matthiesen_core.common.api.events.PlatformEvents;
import dev.matthiesen.matthiesen_core.common.api.events.server.PlayerEvent;
import dev.matthiesen.matthiesen_core.common.api.events.server.ServerEvent;
import dev.matthiesen.matthiesen_core.common.api.platform.loader.ModConfigType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class CobbledLevelControl extends AbstractCommonMod {
    public static final String MOD_ID = "cobbled_level_control";
    public static final String MOD_NAME = "Cobbled Level Control";
    private static @Token final String METRICS_TOKEN = "00c30fedc5bd584dd1060bada0f2637a";
    private StoredPlayerAccountRecords storedPlayerAccountRecords;

    public static final CobbledLevelControl INSTANCE = new CobbledLevelControl();

    public CobbledLevelControl() {
        super(MOD_ID, MOD_NAME);
    }

    @Override
    public void initialize() {
        super.initialize();

        registerModConfig(MOD_ID, ModConfigType.STARTUP, CLCConfig.PERMISSIONS_SPEC, "cobbled_level_control/permissions.toml");
        registerModConfig(MOD_ID, ModConfigType.SERVER, CLCConfig.SERVER_SPEC, "cobbled_level_control/server.toml");

        PlatformEvents.SERVER_STARTED.subscribe(this::onServerStarted);
        PlatformEvents.SERVER_STOPPING.subscribe(this::onServerStopping);
        PlatformEvents.SERVER_END_TICK.subscribe(this::onServerEndTick);
        PlatformEvents.SERVER_RELOAD.subscribe(this::onServerReload);
        PlatformEvents.PLAYER_JOIN.subscribe(this::onPlayerJoin);

        getNetworkingManager().registerOptionalS2C(CLCStatusHudSyncS2CPacket.TYPE, CLCStatusHudSyncS2CPacket.CODEC, (packet, context) -> {
            if (context.player() == null || !context.player().level().isClientSide()) {
                return;
            }
            context.enqueue(() -> {
                dev.matthiesen.cobbled_level_control.common.client.HudClientState.applySnapshot(packet.snapshot());
            });
        });

        PermissionHelpers.init();
        getCommandsRegistryManager().registerCommand(LevelControlCommand.CMD);
        PlayerExtensions.init();

        createInfoLog("Initialized");
    }

    private boolean isServerRunning = false;
    private boolean pendingHudBroadcast;
    private long pendingHudBroadcastAtMillis;

    public void onServerStarted(ServerEvent.Started event) {
        isServerRunning = true;
        CobblemonSubscriptionsManager.registerSubscriptions();
    }

    public void onServerStopping(ServerEvent.Stopping event) {
        isServerRunning = false;
        pendingHudBroadcast = false;
        getStoredPlayerAccountRecords().setDirty();
        CobblemonSubscriptionsManager.teardownAllActiveSubscriptions();
    }

    public void onPlayerJoin(PlayerEvent.Join event) {
        if (!isServerRunning) return;
        var registry = getStoredPlayerAccountRecords();
        if (!registry.hasPlayerAccountRecord(event.player().getUUID())) {
            registry.createNewPlayerAccountRecord(event.player().getUUID());
        }
        sendHudSnapshot(event.player());
    }

    public void onServerReload(ServerEvent.Reload event) {
        scheduleHudSnapshotBroadcast();
    }

    public void onServerEndTick(ServerEvent.EndTick event) {
        if (!pendingHudBroadcast) return;
        if (System.currentTimeMillis() < pendingHudBroadcastAtMillis) return;
        pendingHudBroadcast = false;
        broadcastHudSnapshots();
    }

    @Override
    public @Token @NotNull String getMetricsToken() {
        return METRICS_TOKEN;
    }

    public StoredPlayerAccountRecords getStoredPlayerAccountRecords() {
        if (storedPlayerAccountRecords == null) {
            storedPlayerAccountRecords = StoredPlayerAccountRecords.getInstance();
        }
        return storedPlayerAccountRecords;
    }

    public void sendHudSnapshot(ServerPlayer player) {
        if (player == null) return;
        if (!getNetworkingManager().canSendToPlayer(player, CLCStatusHudSyncS2CPacket.CHANNEL_ID)) return;

        var accountRecord = getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        if (accountRecord == null) return;

        CompoundTag tag = new CompoundTag();
        var catchingConfig = CLCConfig.getCatchingConfig();
        var levelingConfig = CLCConfig.getLevelingConfig();
        var battleConfig = CLCConfig.getBattleConfig();

        int catchingTier = accountRecord.getCatching();
        int levelingTier = accountRecord.getLeveling();

        int catchingCap = resolveTierCap(catchingConfig.tiers(), catchingTier);
        int levelingCap = resolveTierCap(levelingConfig.tiers(), levelingTier);
        int nextCatchingCap = resolveTierCap(catchingConfig.tiers(), catchingTier + 1);
        int nextLevelingCap = resolveTierCap(levelingConfig.tiers(), levelingTier + 1);

        boolean hasPermissionLocks = hasAnyPermissionLock(player);
        boolean capExceeded = isPartyOverLevelingCap(player, levelingConfig.doRestrictLeveling(), levelingCap);
        boolean dataAvailable = catchingCap >= 0 && levelingCap >= 0;

        tag.putInt("catchingTier", catchingTier);
        tag.putInt("catchingCap", catchingCap);
        tag.putInt("catchingNextCap", nextCatchingCap);
        tag.putInt("levelingTier", levelingTier);
        tag.putInt("levelingCap", levelingCap);
        tag.putInt("levelingNextCap", nextLevelingCap);

        tag.putBoolean("restrictBattles", !battleConfig.doNotRestrictBattles());
        tag.putBoolean("restrictCatching", !catchingConfig.doNotRestrictCatching());
        tag.putBoolean("restrictLeveling", levelingConfig.doRestrictLeveling());

        tag.putBoolean("hasPermissionLocks", hasPermissionLocks);
        tag.putBoolean("capExceeded", capExceeded);
        tag.putBoolean("dataAvailable", dataAvailable);
        tag.putString("warning", computeWarning(hasPermissionLocks, capExceeded, dataAvailable));

        getNetworkingManager().sendToPlayer(player, new CLCStatusHudSyncS2CPacket(tag));
    }

    public void broadcastHudSnapshots() {
        var server = getCommonUtils().getServer();
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendHudSnapshot(player);
        }
    }

    public void scheduleHudSnapshotBroadcast() {
        pendingHudBroadcast = true;
        pendingHudBroadcastAtMillis = System.currentTimeMillis() + 500L;
    }

    private int resolveTierCap(Map<String, Integer> tiers, int tier) {
        return tiers.getOrDefault(Integer.toString(tier), -1);
    }

    private boolean isPartyOverLevelingCap(ServerPlayer player, boolean restrictLeveling, int levelingCap) {
        if (!restrictLeveling || levelingCap <= 0) return false;

        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
        for (int i = 0; i < 6; i++) {
            var pokemon = partyStore.get(i);
            if (pokemon != null && pokemon.getLevel() > levelingCap) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyPermissionLock(ServerPlayer player) {
        var battleConfig = CLCConfig.getBattleConfig();
        var catchingConfig = CLCConfig.getCatchingConfig();
        var levelingConfig = CLCConfig.getLevelingConfig();

        return hasMissingPermission(player, battleConfig.legendary())
                || hasMissingPermission(player, battleConfig.mythical())
                || hasMissingPermission(player, battleConfig.ultraBeast())
                || hasMissingPermission(player, battleConfig.shiny())
                || hasMissingPermission(player, battleConfig.evolutionStages().singleEvo())
                || hasMissingPermission(player, battleConfig.evolutionStages().firstStageEvo())
                || hasMissingPermission(player, battleConfig.evolutionStages().secondStageEvo())
                || hasMissingPermission(player, battleConfig.evolutionStages().finalStageEvo())
                || hasMissingPermission(player, catchingConfig.legendary())
                || hasMissingPermission(player, catchingConfig.mythical())
                || hasMissingPermission(player, catchingConfig.ultraBeast())
                || hasMissingPermission(player, catchingConfig.shiny())
                || hasMissingPermission(player, catchingConfig.evolutionStages().singleEvo())
                || hasMissingPermission(player, catchingConfig.evolutionStages().firstStageEvo())
                || hasMissingPermission(player, catchingConfig.evolutionStages().secondStageEvo())
                || hasMissingPermission(player, catchingConfig.evolutionStages().finalStageEvo())
                || hasMissingPermission(player, levelingConfig.evolutionStages().singleEvo())
                || hasMissingPermission(player, levelingConfig.evolutionStages().firstStageEvo())
                || hasMissingPermission(player, levelingConfig.evolutionStages().secondStageEvo())
                || hasMissingPermission(player, levelingConfig.evolutionStages().finalStageEvo());
    }

    private boolean hasMissingPermission(ServerPlayer player, String permissionNode) {
        return !permissionNode.isEmpty() && PermissionHelpers.doesNotHavePermission(player, permissionNode);
    }

    private String computeWarning(boolean hasPermissionLocks, boolean capExceeded, boolean dataAvailable) {
        if (hasPermissionLocks) {
            return "Some restrictions are locked by permissions.";
        }
        if (capExceeded) {
            return "One or more Pokemon exceeds your leveling cap.";
        }
        if (!dataAvailable) {
            return "Status data is temporarily unavailable.";
        }
        return "";
    }
}
