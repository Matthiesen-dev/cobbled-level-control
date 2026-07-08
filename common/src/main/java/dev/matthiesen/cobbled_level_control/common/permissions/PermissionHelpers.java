package dev.matthiesen.cobbled_level_control.common.permissions;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.common.matthiesen_lib_api.MatthiesenLibApi;
import dev.matthiesen.common.matthiesen_lib_api.permission.AbstractPermission;
import dev.matthiesen.common.matthiesen_lib_api.permission.Permission;
import dev.matthiesen.common.matthiesen_lib_api.permission.PermissionLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public final class PermissionHelpers {
    public static Permission COMMAND_ROOT_PERMISSION = register(
            "command.level-control",
            0
    );
    public static Permission COMMAND_RELOAD_PERMISSION = register(
            "command.level-control.reload",
            4
    );
    public static Permission COMMAND_LEVEL_UP_PERMISSION = register(
            "command.level-control.level-up",
            4
    );
    public static Permission COMMAND_SET_DIFFICULTY_PERMISSION = register(
            "command.level-control.set-difficulty",
            4
    );
    public static Permission COMMAND_SET_LEVEL_PERMISSION = register(
            "command.level-control.set-level",
            4
    );
    public static Permission COMMAND_STATUS_PERMISSION = register(
            "command.level-control.status",
            0
    );
    public static Permission COMMAND_STATUS_OTHER_PERMISSION = register(
            "command.level-control.status-other",
            4
    );

    public static boolean checkPermission(CommandSourceStack source, Permission permission) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission);
    }

    public static boolean doesNotHavePermission(ServerPlayer source, String permission) {
        return !MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission, 4);
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
        MatthiesenLibApi.registerPermission(newPermission);
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
