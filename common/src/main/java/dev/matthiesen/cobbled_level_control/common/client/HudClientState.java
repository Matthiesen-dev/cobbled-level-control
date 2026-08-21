package dev.matthiesen.cobbled_level_control.common.client;

import net.minecraft.nbt.CompoundTag;

public final class HudClientState {
    private static CompoundTag snapshot;

    private HudClientState() {}

    public static synchronized void applySnapshot(CompoundTag data) {
        snapshot = data == null ? null : data.copy();
    }

    public static synchronized CompoundTag getSnapshot() {
        return snapshot == null ? null : snapshot.copy();
    }

    public static synchronized void clear() {
        snapshot = null;
    }
}

