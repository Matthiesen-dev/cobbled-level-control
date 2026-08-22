package dev.matthiesen.cobbled_level_control.fabric;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControlClient;
import net.fabricmc.api.ClientModInitializer;

public final class CobbledLevelControlFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        var instance = CobbledLevelControlClient.INSTANCE;
        instance.createInfoLog("Loading for Fabric Mod Loader");
        instance.initialize();
    }
}
