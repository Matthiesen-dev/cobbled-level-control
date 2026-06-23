package dev.matthiesen.cobbled_level_control.neoforge;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import net.neoforged.fml.common.Mod;

@Mod(CobbledLevelControl.MOD_ID)
public final class CobbledLevelControlNeoForge {
    public CobbledLevelControlNeoForge() {
        CobbledLevelControl.INSTANCE.createInfoLog("Loading for NeoForge Mod Loader");
        CobbledLevelControl.INSTANCE.initialize();
    }
}
