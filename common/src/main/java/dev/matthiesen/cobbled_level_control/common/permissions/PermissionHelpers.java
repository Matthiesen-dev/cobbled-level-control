package dev.matthiesen.cobbled_level_control.common.permissions;

import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import dev.matthiesen.cobbled_level_control.common.config.def.PermSet;
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
    public static Permission COMMAND_CONFIGURE_PERMISSION = register(
            "command.level-control.configure",
            CLCConfig.PERMISSIONS_CONFIG.command_levelControl_configure.get().getLevel()
    );
    public static Permission BATTLES_LEGENDARY_PERMISSION = register(
            "battles.legendary",
            CLCConfig.PERMISSIONS_CONFIG.battles_legendary.get().getLevel()
    );
    public static Permission BATTLES_MYTHICAL_PERMISSION = register(
            "battles.mythical",
            CLCConfig.PERMISSIONS_CONFIG.battles_mythical.get().getLevel()
    );
    public static Permission BATTLES_ULTRA_BEAST_PERMISSION = register(
            "battles.ultra-beast",
            CLCConfig.PERMISSIONS_CONFIG.battles_ultraBeast.get().getLevel()
    );
    public static Permission BATTLES_SHINY_PERMISSION = register(
            "battles.shiny",
            CLCConfig.PERMISSIONS_CONFIG.battles_shiny.get().getLevel()
    );
    public static Permission BATTLES_EVOSTAGES_SINGLE_PERMISSION = register(
            "battles.evo-stages.single-evo",
            CLCConfig.PERMISSIONS_CONFIG.battles_evoStages_singleEvo.get().getLevel()
    );
    public static Permission BATTLES_EVOSTAGES_FIRST_PERMISSION = register(
            "battles.evo-stages.first-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.battles_evoStages_firstStageEvo.get().getLevel()
    );
    public static Permission BATTLES_EVOSTAGES_SECOND_PERMISSION = register(
            "battles.evo-stages.second-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.battles_evoStages_secondStageEvo.get().getLevel()
    );
    public static Permission BATTLES_EVOSTAGES_FINAL_PERMISSION = register(
            "battles.evo-stages.final-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.battles_evoStages_finalStageEvo.get().getLevel()
    );
    public static Permission CATCHING_LEGENDARY_PERMISSION = register(
            "catching.legendary",
            CLCConfig.PERMISSIONS_CONFIG.catching_legendary.get().getLevel()
    );
    public static Permission CATCHING_MYTHICAL_PERMISSION = register(
            "catching.mythical",
            CLCConfig.PERMISSIONS_CONFIG.catching_mythical.get().getLevel()
    );
    public static Permission CATCHING_ULTRA_BEAST_PERMISSION = register(
            "catching.ultra-beast",
            CLCConfig.PERMISSIONS_CONFIG.catching_ultraBeast.get().getLevel()
    );
    public static Permission CATCHING_SHINY_PERMISSION = register(
            "catching.shiny",
            CLCConfig.PERMISSIONS_CONFIG.catching_shiny.get().getLevel()
    );
    public static Permission CATCHING_EVOSTAGES_SINGLE_PERMISSION = register(
            "catching.evo-stages.single-evo",
            CLCConfig.PERMISSIONS_CONFIG.catching_evoStages_singleEvo.get().getLevel()
    );
    public static Permission CATCHING_EVOSTAGES_FIRST_PERMISSION = register(
            "catching.evo-stages.first-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.catching_evoStages_firstStageEvo.get().getLevel()
    );
    public static Permission CATCHING_EVOSTAGES_SECOND_PERMISSION = register(
            "catching.evo-stages.second-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.catching_evoStages_secondStageEvo.get().getLevel()
    );
    public static Permission CATCHING_EVOSTAGES_FINAL_PERMISSION = register(
            "catching.evo-stages.final-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.catching_evoStages_finalStageEvo.get().getLevel()
    );
    public static Permission LEVELING_EVOSTAGES_SINGLE_PERMISSION = register(
            "leveling.evo-stages.single-evo",
            CLCConfig.PERMISSIONS_CONFIG.leveling_evoStages_singleEvo.get().getLevel()
    );
    public static Permission LEVELING_EVOSTAGES_FIRST_PERMISSION = register(
            "leveling.evo-stages.first-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.leveling_evoStages_firstStageEvo.get().getLevel()
    );
    public static Permission LEVELING_EVOSTAGES_SECOND_PERMISSION = register(
            "leveling.evo-stages.second-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.leveling_evoStages_secondStageEvo.get().getLevel()
    );
    public static Permission LEVELING_EVOSTAGES_FINAL_PERMISSION = register(
            "leveling.evo-stages.final-stage-evo",
            CLCConfig.PERMISSIONS_CONFIG.leveling_evoStages_finalStageEvo.get().getLevel()
    );

    public static boolean checkPermission(CommandSourceStack source, Permission permission) {
        return CobbledLevelControl.INSTANCE.getPermissionsManager().getPermissionValidator().hasPermission(source, permission);
    }

    public static boolean doesNotHavePermission(ServerPlayer source, PermSet permission) {
        if (permission.enabled()) {
            return !CobbledLevelControl.INSTANCE.getPermissionsManager().getPermissionValidator().hasPermission(source, permission.permission());
        }
        return false;
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
