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

    public ModConfigSpec.EnumValue<PermissionLevel> battles_legendary;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_mythical;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_ultraBeast;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_shiny;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_evoStages_singleEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_evoStages_firstStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_evoStages_secondStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> battles_evoStages_finalStageEvo;

    public ModConfigSpec.EnumValue<PermissionLevel> catching_legendary;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_mythical;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_ultraBeast;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_shiny;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_evoStages_singleEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_evoStages_firstStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_evoStages_secondStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> catching_evoStages_finalStageEvo;

    public ModConfigSpec.EnumValue<PermissionLevel> leveling_evoStages_singleEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> leveling_evoStages_firstStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> leveling_evoStages_secondStageEvo;
    public ModConfigSpec.EnumValue<PermissionLevel> leveling_evoStages_finalStageEvo;

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

        builder.comment("Battles Configuration")
                .translation("cobbled_level_control.configuration.permissions.battles")
                .push("battles");
        battles_legendary = builder
                .comment(
                        "Permission level for battling legendary Pokémon",
                        "Permission Node: 'cobbled_level_control.battles.legendary'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.legendary")
                .defineEnum("legendary", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_mythical = builder
                .comment(
                        "Permission level for battling mythical Pokémon",
                        "Permission Node: 'cobbled_level_control.battles.mythical'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.mythical")
                .defineEnum("mythical", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_ultraBeast = builder
                .comment(
                        "Permission level for battling Ultra Beasts",
                        "Permission Node: 'cobbled_level_control.battles.ultra-beast'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.ultraBeast")
                .defineEnum("ultraBeast", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_shiny = builder
                .comment(
                        "Permission level for battling shiny Pokémon",
                        "Permission Node: 'cobbled_level_control.battles.shiny'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.shiny")
                .defineEnum("shiny", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        builder.comment("Evolution Stages Configuration")
                .translation("cobbled_level_control.configuration.permissions.battles.evoStages")
                .push("evoStages");
        battles_evoStages_singleEvo = builder
                .comment(
                        "Permission level for battling Pokémon with a single evolution stage",
                        "Permission Node: 'cobbled_level_control.battles.evo-stages.single-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.evoStages.singleEvo")
                .defineEnum("singleEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_evoStages_firstStageEvo = builder
                .comment(
                        "Permission level for battling Pokémon with a first stage evolution",
                        "Permission Node: 'cobbled_level_control.battles.evo-stages.first-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.evoStages.firstStageEvo")
                .defineEnum("firstStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_evoStages_secondStageEvo = builder
                .comment(
                        "Permission level for battling Pokémon with a second stage evolution",
                        "Permission Node: 'cobbled_level_control.battles.evo-stages.second-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.evoStages.secondStageEvo")
                .defineEnum("secondStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        battles_evoStages_finalStageEvo = builder
                .comment(
                        "Permission level for battling Pokémon with a final stage evolution",
                        "Permission Node: 'cobbled_level_control.battles.evo-stages.final-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.battles.evoStages.finalStageEvo")
                .defineEnum("finalStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        builder.pop(); // evoStages
        builder.pop(); // battles

        builder.comment("Catching Configuration")
                .translation("cobbled_level_control.configuration.permissions.catching")
                .push("catching");
        catching_legendary = builder
                .comment(
                        "Permission level for catching legendary Pokémon",
                        "Permission Node: 'cobbled_level_control.catching.legendary'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.legendary")
                .defineEnum("legendary", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_mythical = builder
                .comment(
                        "Permission level for catching mythical Pokémon",
                        "Permission Node: 'cobbled_level_control.catching.mythical'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.mythical")
                .defineEnum("mythical", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_ultraBeast = builder
                .comment(
                        "Permission level for catching Ultra Beasts",
                        "Permission Node: 'cobbled_level_control.catching.ultra-beast'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.ultraBeast")
                .defineEnum("ultraBeast", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_shiny = builder
                .comment(
                        "Permission level for catching shiny Pokémon",
                        "Permission Node: 'cobbled_level_control.catching.shiny'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.shiny")
                .defineEnum("shiny", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        builder.comment("Evolution Stages Configuration")
                .translation("cobbled_level_control.configuration.permissions.catching.evoStages")
                .push("evoStages");
        catching_evoStages_singleEvo = builder
                .comment(
                        "Permission level for catching Pokémon with a single evolution stage",
                        "Permission Node: 'cobbled_level_control.catching.evo-stages.single-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.evoStages.singleEvo")
                .defineEnum("singleEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_evoStages_firstStageEvo = builder
                .comment(
                        "Permission level for catching Pokémon with a first stage evolution",
                        "Permission Node: 'cobbled_level_control.catching.evo-stages.first-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.evoStages.firstStageEvo")
                .defineEnum("firstStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_evoStages_secondStageEvo = builder
                .comment(
                        "Permission level for catching Pokémon with a second stage evolution",
                        "Permission Node: 'cobbled_level_control.catching.evo-stages.second-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.evoStages.secondStageEvo")
                .defineEnum("secondStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        catching_evoStages_finalStageEvo = builder
                .comment(
                        "Permission level for catching Pokémon with a final stage evolution",
                        "Permission Node: 'cobbled_level_control.catching.evo-stages.final-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.catching.evoStages.finalStageEvo")
                .defineEnum("finalStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        builder.pop(); // evoStages
        builder.pop(); // catching

        builder.comment("Leveling Configuration")
                .translation("cobbled_level_control.configuration.permissions.leveling")
                .push("leveling");
        leveling_evoStages_singleEvo = builder
                .comment(
                        "Permission level for leveling Pokémon with a single evolution stage",
                        "Permission Node: 'cobbled_level_control.leveling.evo-stages.single-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.leveling.evoStages.singleEvo")
                .defineEnum("singleEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        leveling_evoStages_firstStageEvo = builder
                .comment(
                        "Permission level for leveling Pokémon with a first stage evolution",
                        "Permission Node: 'cobbled_level_control.leveling.evo-stages.first-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.leveling.evoStages.firstStageEvo")
                .defineEnum("firstStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        leveling_evoStages_secondStageEvo = builder
                .comment(
                        "Permission level for leveling Pokémon with a second stage evolution",
                        "Permission Node: 'cobbled_level_control.leveling.evo-stages.second-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.leveling.evoStages.secondStageEvo")
                .defineEnum("secondStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        leveling_evoStages_finalStageEvo = builder
                .comment(
                        "Permission level for leveling Pokémon with a final stage evolution",
                        "Permission Node: 'cobbled_level_control.leveling.evo-stages.final-stage-evo'"
                )
                .translation("cobbled_level_control.configuration.permissions.leveling.evoStages.finalStageEvo")
                .defineEnum("finalStageEvo", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
        builder.pop(); // leveling

        builder.pop();
    }
}
