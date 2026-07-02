package dev.matthiesen.cobbled_level_control.common.runtime.events;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.common.matthiesen_lib_api.core.interfaces.MatthiesenLibPlayerEventHandler;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerEvents implements MatthiesenLibPlayerEventHandler {
    @Override
    public void onPlayerJoin(ServerPlayer player) {
        var registry = CobbledLevelControl.INSTANCE.getStoredPlayerAccountRecords();
        if (!registry.hasPlayerAccountRecord(player.getUUID()))
            registry.createNewPlayerAccountRecord(player.getUUID());
    }
}
