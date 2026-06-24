package dev.matthiesen.cobbled_level_control.common.events;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.events.cobblemon.CobblemonSubscriptionsManager;
import dev.matthiesen.common.matthiesen_lib_api.core.interfaces.MatthiesenLibServerEventHandler;
import net.minecraft.server.MinecraftServer;

public final class ServerEvents implements MatthiesenLibServerEventHandler {
    private int tickCount = 0;

    @Override
    public void onServerStart(MinecraftServer server) {
        CobblemonSubscriptionsManager.registerSubscriptions();
    }

    @Override
    public void onServerTick(MinecraftServer server) {
        var instance = CobbledLevelControl.INSTANCE;
        var config = instance.getConfigManager().getMainConfig();
        if (config.saveConfig.enableAutoSave) {
            tickCount++;
            if (tickCount >= config.saveConfig.saveIntervalTicks) {
                instance.getConfigManager().savePlayerAccounts();
                tickCount = 0;
            }
        }
    }

    @Override
    public void onServerStop(MinecraftServer server) {
        var instance = CobbledLevelControl.INSTANCE;
        instance.createInfoLog("Saving player accounts on server shutdown");
        instance.getConfigManager().savePlayerAccounts();
        instance.createInfoLog("Saved player accounts on server shutdown");
        CobblemonSubscriptionsManager.teardownAllActiveSubscriptions();
    }
}
