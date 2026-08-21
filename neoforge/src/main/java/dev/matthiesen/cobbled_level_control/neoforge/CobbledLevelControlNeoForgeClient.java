package dev.matthiesen.cobbled_level_control.neoforge;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControlClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = CobbledLevelControl.MOD_ID, dist = Dist.CLIENT)
public final class CobbledLevelControlNeoForgeClient {
    public CobbledLevelControlNeoForgeClient() {
        var instance = CobbledLevelControlClient.INSTANCE;
        instance.createInfoLog("Loading for NeoForge Mod Loader");
        instance.initialize();
    }
}
