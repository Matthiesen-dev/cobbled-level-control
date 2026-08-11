package dev.matthiesen.cobbled_level_control.common.config;

import dev.matthiesen.matthiesen_core.common.api.permissions.PermissionLevel;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class PermissionsConfig {

    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl;
    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl_levelUp;
    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl_setLevel;
    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl_status;
    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl_statusOther;
    public ModConfigSpec.EnumValue<PermissionLevel> command_levelControl_configure;

    public PermissionsConfig(ModConfigSpec.Builder builder) {
        builder.comment("Permissions Configuration")
                .translation("cobbled_level_control.configuration.permissions")
                .push("permissions");

        builder.comment("Commands Configuration")
                        .translation("cobbled_level_control.configuration.permissions.commands")
                        .push("commands");
        command_levelControl = builder
                .comment(
                        "Permission level for the /levelControl command",
                        "Permission Node: 'cobbled_level_control.command.level-control'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl")
                .defineEnum("levelControl", PermissionLevel.NONE);
        command_levelControl_levelUp = builder
                .comment(
                        "Permission level for the /levelControl level-up command",
                        "Permission Node: 'cobbled_level_control.command.level-control.level-up'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl.levelUp")
                .defineEnum("levelControl_levelUp", PermissionLevel.ALL_COMMANDS);
        command_levelControl_setLevel = builder
                .comment(
                        "Permission level for the /levelControl set-level command",
                        "Permission Node: 'cobbled_level_control.command.level-control.set-level'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl.setLevel")
                .defineEnum("levelControl_setLevel", PermissionLevel.ALL_COMMANDS);
        command_levelControl_status = builder
                .comment(
                        "Permission level for the /levelControl status command",
                        "Permission Node: 'cobbled_level_control.command.level-control.status'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl.status")
                .defineEnum("levelControl_status", PermissionLevel.NONE);
        command_levelControl_statusOther = builder
                .comment(
                        "Permission level for the /levelControl status-other command",
                        "Permission Node: 'cobbled_level_control.command.level-control.status-other'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl.statusOther")
                .defineEnum("levelControl_statusOther", PermissionLevel.ALL_COMMANDS);
        command_levelControl_configure = builder
                .comment(
                        "Permission level for the /levelControl configure command",
                        "Permission Node: 'cobbled_level_control.command.level-control.configure'"
                )
                .translation("cobbled_level_control.configuration.permissions.commands.levelControl.configure")
                .defineEnum("levelControl_configure", PermissionLevel.ALL_COMMANDS);
        builder.pop();

        builder.pop();
    }
}
