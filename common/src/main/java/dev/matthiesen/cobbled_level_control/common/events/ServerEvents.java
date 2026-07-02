package dev.matthiesen.cobbled_level_control.common.events;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.events.cobblemon.CobblemonSubscriptionsManager;
import dev.matthiesen.common.matthiesen_lib_api.core.interfaces.MatthiesenLibServerEventHandler;
import net.minecraft.server.MinecraftServer;

public final class ServerEvents implements MatthiesenLibServerEventHandler {
    @Override
    public void onServerStart(MinecraftServer server) {
        CobblemonSubscriptionsManager.registerSubscriptions();
    }

    @Override
    public void onServerStop(MinecraftServer server) {
        CobbledLevelControl.INSTANCE.getStoredPlayerAccountRecords().setDirty();
        CobblemonSubscriptionsManager.teardownAllActiveSubscriptions();
    }
}
