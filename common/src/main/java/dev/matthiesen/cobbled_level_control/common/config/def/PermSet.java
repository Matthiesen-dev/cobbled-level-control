package dev.matthiesen.cobbled_level_control.common.config.def;

import dev.matthiesen.matthiesen_core.common.api.permissions.Permission;

public record PermSet(
        Boolean enabled,
        Permission permission
) {
}
