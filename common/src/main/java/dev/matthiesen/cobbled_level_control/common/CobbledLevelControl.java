package dev.matthiesen.cobbled_level_control.common;

import dev.matthiesen.cobbled_level_control.common.commands.LevelControlCommand;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.runtime.data.StoredPlayerAccountRecords;
import dev.matthiesen.cobbled_level_control.common.permissions.PermissionHelpers;
import dev.matthiesen.cobbled_level_control.common.runtime.events.CobblemonSubscriptionsManager;
import dev.matthiesen.cobbled_level_control.common.runtime.molang.PlayerExtensions;
import dev.matthiesen.libs.faststats.Token;
import dev.matthiesen.matthiesen_core.common.AbstractCommonMod;
import dev.matthiesen.matthiesen_core.common.api.events.PlatformEvents;
import dev.matthiesen.matthiesen_core.common.api.events.server.PlayerEvent;
import dev.matthiesen.matthiesen_core.common.api.events.server.ServerEvent;
import dev.matthiesen.matthiesen_core.common.api.platform.loader.ModConfigType;
import org.jetbrains.annotations.NotNull;

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
        PlatformEvents.PLAYER_JOIN.subscribe(this::onPlayerJoin);

        PermissionHelpers.init();
        getCommandsRegistryManager().registerCommand(LevelControlCommand.CMD);
        PlayerExtensions.init();

        createInfoLog("Initialized");
    }

    private boolean isServerRunning = false;

    public void onServerStarted(ServerEvent.Started event) {
        isServerRunning = true;
        CobblemonSubscriptionsManager.registerSubscriptions();
    }

    public void onServerStopping(ServerEvent.Stopping event) {
        isServerRunning = false;
        getStoredPlayerAccountRecords().setDirty();
        CobblemonSubscriptionsManager.teardownAllActiveSubscriptions();
    }

    public void onPlayerJoin(PlayerEvent.Join event) {
        if (!isServerRunning) return;
        var registry = getStoredPlayerAccountRecords();
        if (!registry.hasPlayerAccountRecord(event.player().getUUID())) {
            registry.createNewPlayerAccountRecord(event.player().getUUID());
        }
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
}
