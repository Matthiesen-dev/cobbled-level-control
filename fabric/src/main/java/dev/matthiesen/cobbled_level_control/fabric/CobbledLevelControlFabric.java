package dev.matthiesen.cobbled_level_control.fabric;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import net.fabricmc.api.ModInitializer;

public final class CobbledLevelControlFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CobbledLevelControl.INSTANCE.createInfoLog("Loading for Fabric Mod Loader");
        CobbledLevelControl.INSTANCE.initialize();
    }
}
