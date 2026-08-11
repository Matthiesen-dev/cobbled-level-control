package dev.matthiesen.cobbled_level_control.common.permissions;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.matthiesen_core.common.api.permissions.Permission;
import dev.matthiesen.matthiesen_core.common.api.permissions.PermissionLevel;
import dev.matthiesen.matthiesen_core.common.utility.AbstractPermission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public final class PermissionHelpers {
    public static Permission COMMAND_ROOT_PERMISSION = register(
            "command.level-control",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl.get().getLevel()
    );
    public static Permission COMMAND_LEVEL_UP_PERMISSION = register(
            "command.level-control.level-up",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl_levelUp.get().getLevel()
    );
    public static Permission COMMAND_SET_LEVEL_PERMISSION = register(
            "command.level-control.set-level",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl_setLevel.get().getLevel()
    );
    public static Permission COMMAND_STATUS_PERMISSION = register(
            "command.level-control.status",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl_status.get().getLevel()
    );
    public static Permission COMMAND_STATUS_OTHER_PERMISSION = register(
            "command.level-control.status-other",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl_statusOther.get().getLevel()
    );

    public static boolean checkPermission(CommandSourceStack source, Permission permission) {
        return CobbledLevelControl.INSTANCE.getPermissionsManager().getPermissionValidator().hasPermission(source, permission);
    }

    public static boolean doesNotHavePermission(ServerPlayer source, String permission) {
        return !CobbledLevelControl.INSTANCE.getPermissionsManager().getPermissionValidator().hasPermission(source, permission, 4);
    }

    public static PermissionLevel toPermLevel(int permLevel) {
        for (PermissionLevel value : PermissionLevel.values()) {
            if (value.ordinal() == permLevel) {
                return value;
            }
        }
        return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS;
    }

    public static void init() {}

    @SuppressWarnings("SameParameterValue")
    private static Permission register(String node, int level) {
        var newPermission = modPermission(node, toPermLevel(level));
        CobbledLevelControl.INSTANCE.getPermissionsManager().registerPermission(newPermission);
        return newPermission;
    }

    private static Permission modPermission(String node, PermissionLevel level) {
        return new AbstractPermission(node, level) {
            @Override
            protected String getModId() {
                return CobbledLevelControl.MOD_ID;
            }

            @Override
            protected String getPermissionNamespace() {
                return "CobbledLevelControl";
            }
        };
    }
}
