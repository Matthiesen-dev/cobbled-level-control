package dev.matthiesen.cobbled_level_control.common.config;

import dev.matthiesen.cobbled_level_control.common.config.def.BattleConfig;
import dev.matthiesen.cobbled_level_control.common.config.def.CatchingConfig;
import dev.matthiesen.cobbled_level_control.common.config.def.LevelingConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CLCConfig {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ServerConfig SERVER_CONFIG;
    public static final ModConfigSpec SERVER_SPEC;

    public static final PermissionsConfig PERMISSIONS_CONFIG;
    public static final ModConfigSpec PERMISSIONS_SPEC;

    static {
        Pair<ClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();

        Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = specPair.getLeft();
        SERVER_SPEC = specPair.getRight();

        Pair<PermissionsConfig, ModConfigSpec> permissionsSpecPair = new ModConfigSpec.Builder().configure(PermissionsConfig::new);
        PERMISSIONS_CONFIG = permissionsSpecPair.getLeft();
        PERMISSIONS_SPEC = permissionsSpecPair.getRight();
    }

    public static BattleConfig getBattleConfig() {
        return BattleConfig.getConfig();
    }

    public static CatchingConfig getCatchingConfig() {
        return CatchingConfig.getConfig();
    }

    public static LevelingConfig getLevelingConfig() {
        return LevelingConfig.getConfig();
    }
}
