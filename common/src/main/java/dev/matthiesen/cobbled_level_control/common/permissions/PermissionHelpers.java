package dev.matthiesen.cobbled_level_control.common.permissions;

import dev.matthiesen.common.matthiesen_lib_api.MatthiesenLibApi;
import dev.matthiesen.common.matthiesen_lib_api.permission.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public final class PermissionHelpers {
    public static boolean checkPermission(CommandSourceStack source, Permission permission) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission);
    }
    public static boolean checkPermission(CommandSourceStack source, String permission, int level) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission, level);
    }
    public static boolean checkPermission(CommandSourceStack source, String permission) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission, 4);
    }

    public static boolean checkPermission(ServerPlayer source, Permission permission) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission);
    }
    public static boolean checkPermission(ServerPlayer source, String permission, int level) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission, level);
    }
    public static boolean checkPermission(ServerPlayer source, String permission) {
        return MatthiesenLibApi.getPermissionValidator().hasPermission(source, permission, 4);
    }
}
