package dev.matthiesen.cobbled_level_control.common.config;

import com.electronwill.nightconfig.core.Config;
import dev.matthiesen.cobbled_level_control.common.config.def.TierConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ServerConfig {

    // Main Configuration

    // Battle Module Config
    public ModConfigSpec.BooleanValue battleModule_restrictBattles;
    public ModConfigSpec.ConfigValue<String> battleModule_legendary;
    public ModConfigSpec.ConfigValue<String> battleModule_mythical;
    public ModConfigSpec.ConfigValue<String> battleModule_ultraBeast;
    public ModConfigSpec.ConfigValue<String> battleModule_shiny;
    public ModConfigSpec.ConfigValue<String> battleModule_evoStages_singleEvo;
    public ModConfigSpec.ConfigValue<String> battleModule_evoStages_firstStageEvo;
    public ModConfigSpec.ConfigValue<String> battleModule_evoStages_secondStageEvo;
    public ModConfigSpec.ConfigValue<String> battleModule_evoStages_finalStageEvo;

    // Catching Module Config
    public ModConfigSpec.BooleanValue catchingModule_restrictCatching;
    public ModConfigSpec.ConfigValue<String> catchingModule_legendary;
    public ModConfigSpec.ConfigValue<String> catchingModule_mythical;
    public ModConfigSpec.ConfigValue<String> catchingModule_ultraBeast;
    public ModConfigSpec.ConfigValue<String> catchingModule_shiny;
    public ModConfigSpec.ConfigValue<String> catchingModule_evoStages_singleEvo;
    public ModConfigSpec.ConfigValue<String> catchingModule_evoStages_firstStageEvo;
    public ModConfigSpec.ConfigValue<String> catchingModule_evoStages_secondStageEvo;
    public ModConfigSpec.ConfigValue<String> catchingModule_evoStages_finalStageEvo;
    public ModConfigSpec.ConfigValue<List<? extends Config>> catchingModule_tiersConfig;

    // Leveling Module Config
    public ModConfigSpec.BooleanValue levelingModule_restrictLeveling;
    public ModConfigSpec.ConfigValue<String> levelingModule_evoStages_singleEvo;
    public ModConfigSpec.ConfigValue<String> levelingModule_evoStages_firstStageEvo;
    public ModConfigSpec.ConfigValue<String> levelingModule_evoStages_secondStageEvo;
    public ModConfigSpec.ConfigValue<String> levelingModule_evoStages_finalStageEvo;
    public ModConfigSpec.ConfigValue<List<? extends Config>> levelingModule_tiersConfig;

    // Spawn Config
    public ModConfigSpec.BooleanValue scaling_enableScaling;
    public ModConfigSpec.ConfigValue<String> scaling_scalingMethod;

    // Messages

    // Success Messages
    public ModConfigSpec.BooleanValue messages_success_useActionBar;
    public ModConfigSpec.ConfigValue<String> messages_success_reloaded;
    public ModConfigSpec.ConfigValue<String> messages_success_targetCatchingLevelSet;
    public ModConfigSpec.ConfigValue<String> messages_success_sourceCatchingLevelSet;
    public ModConfigSpec.ConfigValue<String> messages_success_targetLevelingLevelSet;
    public ModConfigSpec.ConfigValue<String> messages_success_sourceLevelingLevelSet;
    public ModConfigSpec.ConfigValue<String> messages_success_targetCatchingTierSet;
    public ModConfigSpec.ConfigValue<String> messages_success_sourceCatchingTierSet;
    public ModConfigSpec.ConfigValue<String> messages_success_targetLevelingTierSet;
    public ModConfigSpec.ConfigValue<String> messages_success_sourceLevelingTierSet;

    // Error Messages
    public ModConfigSpec.BooleanValue messages_error_useActionBar;
    public ModConfigSpec.ConfigValue<String> messages_error_battle;
    public ModConfigSpec.ConfigValue<String> messages_error_catchingTier;
    public ModConfigSpec.ConfigValue<String> messages_error_levelingTier;
    public ModConfigSpec.ConfigValue<String> messages_error_missingPermission;
    public ModConfigSpec.ConfigValue<String> messages_error_catchingLevelToHigh;
    public ModConfigSpec.ConfigValue<String> messages_error_levelingLevelToHigh;
    public ModConfigSpec.ConfigValue<String> messages_error_invalidModule;
    public ModConfigSpec.ConfigValue<String> messages_error_catchingLevelAlreadyMax;
    public ModConfigSpec.ConfigValue<String> messages_error_levelingLevelAlreadyMax;

    public ServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Server Configuration")
                .translation("cobbled_level_control.configuration.server")
                .push("server");

        builder.comment("Main Configuration")
                .translation("cobbled_level_control.configuration.server.main")
                .push("main");

        builder.comment("Battle Module Configuration")
                .translation("cobbled_level_control.configuration.server.main.battleModule")
                .push("battleModule");
        battleModule_restrictBattles = builder.comment(
                        "Whether to restrict battles based on player level",
                        "If set to false, players will be able to battle with any Pokémon regardless of their level"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.restrictBattles")
                .define("restrictBattles", true);
        battleModule_legendary = builder.comment(
                        "The Required permission node for legendary Pokémon that players can battle with",
                        "If a player tries to battle with a legendary Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.legendary")
                .define("legendary", "cobbled_level_control.battles.legendary");
        battleModule_mythical = builder.comment(
                        "The Required permission node for mythical Pokémon that players can battle with",
                        "If a player tries to battle with a mythical Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.mythical")
                .define("mythical", "cobbled_level_control.battles.mythical");
        battleModule_ultraBeast = builder.comment(
                        "The Required permission node for ultra beast Pokémon that players can battle with",
                        "If a player tries to battle with an ultra beast Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.ultraBeast")
                .define("ultraBeast", "cobbled_level_control.battles.ultraBeast");
        battleModule_shiny = builder.comment(
                        "The Required permission node for shiny Pokémon that players can battle with",
                        "If a player tries to battle with a shiny Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.shiny")
                .define("shiny", "cobbled_level_control.battles.shiny");
        battleModule_evoStages_singleEvo = builder.comment(
                        "The Required permission node for single evolution stage Pokémon that players can battle with",
                        "If a player tries to battle with a single evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.evoStages.singleEvo")
                .define("evoStages.singleEvo", "");
        battleModule_evoStages_firstStageEvo = builder.comment(
                        "The Required permission node for first evolution stage Pokémon that players can battle with",
                        "If a player tries to battle with a first evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.evoStages.firstStageEvo")
                .define("evoStages.firstStageEvo", "");
        battleModule_evoStages_secondStageEvo = builder.comment(
                        "The Required permission node for second evolution stage Pokémon that players can battle with",
                        "If a player tries to battle with a second evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.evoStages.secondStageEvo")
                .define("evoStages.secondStageEvo", "cobbled_level_control.battles.evoStages.secondStageEvo");
        battleModule_evoStages_finalStageEvo = builder.comment(
                        "The Required permission node for final evolution stage Pokémon that players can battle with",
                        "If a player tries to battle with a final evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.battleModule.evoStages.finalStageEvo")
                .define("evoStages.finalStageEvo", "cobbled_level_control.battles.evoStages.finalStageEvo");
        builder.pop(); // Closes "server.main.battleModule"

        builder.comment("Catching Module Configuration")
                .translation("cobbled_level_control.configuration.server.main.catchingModule")
                .push("catchingModule");
        catchingModule_restrictCatching = builder.comment(
                        "Whether to restrict catching based on player level",
                        "If set to false, players will be able to catch any Pokémon regardless of their level"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.restrictCatching")
                .define("restrictCatching", true);
        catchingModule_legendary = builder.comment(
                        "The Required permission node for legendary Pokémon that players can catch",
                        "If a player tries to catch a legendary Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.legendary")
                .define("legendary", "cobbled_level_control.catching.legendary");
        catchingModule_mythical = builder.comment(
                        "The Required permission node for mythical Pokémon that players can catch",
                        "If a player tries to catch a mythical Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.mythical")
                .define("mythical", "cobbled_level_control.catching.mythical");
        catchingModule_ultraBeast = builder.comment(
                        "The Required permission node for ultra beast Pokémon that players can catch",
                        "If a player tries to catch an ultra beast Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.ultraBeast")
                .define("ultraBeast", "cobbled_level_control.catching.ultraBeast");
        catchingModule_shiny = builder.comment(
                        "The Required permission node for shiny Pokémon that players can catch",
                        "If a player tries to catch a shiny Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.shiny")
                .define("shiny", "cobbled_level_control.catching.shiny");
        catchingModule_evoStages_singleEvo = builder.comment(
                        "The Required permission node for single evolution stage Pokémon that players can catch",
                        "If a player tries to catch a single evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.evoStages.singleEvo")
                .define("evoStages.singleEvo", "");
        catchingModule_evoStages_firstStageEvo = builder.comment(
                        "The Required permission node for first evolution stage Pokémon that players can catch",
                        "If a player tries to catch a first evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.evoStages.firstStageEvo")
                .define("evoStages.firstStageEvo", "");
        catchingModule_evoStages_secondStageEvo = builder.comment(
                        "The Required permission node for second evolution stage Pokémon that players can catch",
                        "If a player tries to catch a second evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.evoStages.secondStageEvo")
                .define("evoStages.secondStageEvo", "cobbled_level_control.catching.evoStages.secondStageEvo");
        catchingModule_evoStages_finalStageEvo = builder.comment(
                        "The Required permission node for final evolution stage Pokémon that players can catch",
                        "If a player tries to catch a final evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.evoStages.finalStageEvo")
                .define("evoStages.finalStageEvo", "cobbled_level_control.catching.evoStages.finalStageEvo");
        catchingModule_tiersConfig = builder.comment(
                        "The configuration for the catching tiers",
                        "This configuration defines the tiers for catching and the required levels for each tier",
                        "The configuration is a list of objects, each object representing a tier",
                        "Each object must have the following fields:",
                        "- 'tier': The name of the tier (string)",
                        "- 'level': The level of the tier (integer)"
                )
                .translation("cobbled_level_control.configuration.server.main.catchingModule.tiersConfig")
                        .defineList(
                                List.of("tiersConfig"),
                                TierConfig::getDefaultTiersConfig,
                                null,
                                o -> o instanceof Config && TierConfig.isValid(o)
                        );
        builder.pop(); // Closes "server.main.catchingModule"

        builder.comment("Leveling Module Configuration")
                .translation("cobbled_level_control.configuration.server.main.levelingModule")
                .push("levelingModule");
        levelingModule_restrictLeveling = builder.comment(
                        "Whether to restrict leveling based on player level",
                        "If set to false, players will be able to level with any Pokémon regardless of their level"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.restrictLeveling")
                .define("restrictLeveling", true);
        levelingModule_evoStages_singleEvo = builder.comment(
                        "The Required permission node for single evolution stage Pokémon that players can level with",
                        "If a player tries to level with a single evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.evoStages.singleEvo")
                .define("evoStages.singleEvo", "");
        levelingModule_evoStages_firstStageEvo = builder.comment(
                        "The Required permission node for first evolution stage Pokémon that players can level with",
                        "If a player tries to level with a first evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.evoStages.firstStageEvo")
                .define("evoStages.firstStageEvo", "");
        levelingModule_evoStages_secondStageEvo = builder.comment(
                        "The Required permission node for second evolution stage Pokémon that players can level with",
                        "If a player tries to level with a second evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.evoStages.secondStageEvo")
                .define("evoStages.secondStageEvo", "cobbled_level_control.leveling.evoStages.secondStageEvo");
        levelingModule_evoStages_finalStageEvo = builder.comment(
                        "The Required permission node for final evolution stage Pokémon that players can level with",
                        "If a player tries to level with a final evolution stage Pokémon without the required permission, they will be prevented from doing so",
                        "Note: If this field is empty, there will be no restriction"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.evoStages.finalStageEvo")
                .define("evoStages.finalStageEvo", "cobbled_level_control.leveling.evoStages.finalStageEvo");
        levelingModule_tiersConfig = builder.comment(
                        "The configuration for the leveling tiers",
                        "This configuration defines the tiers for leveling and the required levels for each tier",
                        "The configuration is a list of objects, each object representing a tier",
                        "Each object must have the following fields:",
                        "- 'tier': The name of the tier (string)",
                        "- 'level': The level of the tier (integer)"
                )
                .translation("cobbled_level_control.configuration.server.main.levelingModule.tiersConfig")
                .defineList(
                        List.of("tiersConfig"),
                        TierConfig::getDefaultTiersConfig,
                        null,
                        o -> o instanceof Config && TierConfig.isValid(o)
                );
        builder.pop(); // Closes "server.main.levelingModule"

        builder.comment("Scaling Configuration")
                .translation("cobbled_level_control.configuration.server.main.scaling")
                .push("scaling");
        scaling_enableScaling = builder.comment(
                        "Whether to enable scaling of wild Pokémon levels based on player level",
                        "If set to false, wild Pokémon will spawn at their default levels"
                )
                .translation("cobbled_level_control.configuration.server.main.scaling.enableScaling")
                .define("enableScaling", true);
        scaling_scalingMethod = builder.comment(
                        "The method to use for scaling wild Pokémon levels",
                        "Supported Scaling Methods:",
                        "- '+- randomX': This method will randomly add or subtract a number between 1 and X levels from the player’s max catching level",
                        "- '+ X': This method will add X levels to the player’s max catching level",
                        "- '- randomX': This method will randomly subtract a number between 1 and X levels from the player’s max catching level",
                        "- '+ 0': This method will set the wild pokemon spawn level to be exactly at the player’s max catching level",
                        "Default: '+- random7'"
                )
                .translation("cobbled_level_control.configuration.server.main.scaling.scalingMethod")
                .define("scalingMethod", "+- random7");
        builder.pop(); // Closes "server.main.scaling"

        builder.pop(); // Closes "server.main"

        builder.comment("Messages Configuration")
                .translation("cobbled_level_control.configuration.server.messages")
                .push("messages");

        builder.comment("Success Messages Configuration")
                .translation("cobbled_level_control.configuration.server.messages.success")
                .push("success");
        messages_success_useActionBar = builder.comment(
                        "Whether to use the action bar for success messages",
                        "If set to false, success messages will be sent in chat",
                        "Note: useActionBar is only applied to the messages being sent to the target user, and not the user/console executing the command"
                )
                .translation("cobbled_level_control.configuration.server.messages.success.useActionBar")
                .define("useActionBar", false);
        messages_success_reloaded = builder.comment("Message sent to players when the plugin is reloaded")
                .translation("cobbled_level_control.configuration.server.messages.success.reloaded")
                .define("reloaded", "Cobbled Level Control configuration reloaded successfully.");
        messages_success_targetCatchingLevelSet = builder.comment("Message sent to players when their catching level is set")
                .translation("cobbled_level_control.configuration.server.messages.success.targetCatchingLevelSet")
                .define("targetCatchingLevelSet", "Your Catching level has been set to %level%.");
        messages_success_sourceCatchingLevelSet = builder.comment("Message sent to players when they set another player's catching level")
                .translation("cobbled_level_control.configuration.server.messages.success.sourceCatchingLevelSet")
                .define("sourceCatchingLevelSet", "Set Catching level of %target% to %level%.");
        messages_success_targetLevelingLevelSet = builder.comment("Message sent to players when their leveling level is set")
                .translation("cobbled_level_control.configuration.server.messages.success.targetLevelingLevelSet")
                .define("targetLevelingLevelSet", "Your Leveling level has been set to %level%.");
        messages_success_sourceLevelingLevelSet = builder.comment("Message sent to players when they set another player's leveling level")
                .translation("cobbled_level_control.configuration.server.messages.success.sourceLevelingLevelSet")
                .define("sourceLevelingLevelSet", "Set Leveling level of %target% to %level%.");
        messages_success_targetCatchingTierSet = builder.comment("Message sent to players when their catching tier is set")
                .translation("cobbled_level_control.configuration.server.messages.success.targetCatchingTierSet")
                .define("targetCatchingTierSet", "Your tier in catching has increased to %tier%!");
        messages_success_sourceCatchingTierSet = builder.comment("Message sent to players when they set another player's catching tier")
                .translation("cobbled_level_control.configuration.server.messages.success.sourceCatchingTierSet")
                .define("sourceCatchingTierSet", "Set %target%'s tier in catching to %tier%!");
        messages_success_targetLevelingTierSet = builder.comment("Message sent to players when their leveling tier is set")
                .translation("cobbled_level_control.configuration.server.messages.success.targetLevelingTierSet")
                .define("targetLevelingTierSet", "Your tier in leveling has increased to %tier%!");
        messages_success_sourceLevelingTierSet = builder.comment("Message sent to players when they set another player's leveling tier")
                .translation("cobbled_level_control.configuration.server.messages.success.sourceLevelingTierSet")
                .define("sourceLevelingTierSet", "Set %target%'s tier in leveling to %tier%!");
        builder.pop(); // Closes "server.messages.success"

        builder.comment("Error Messages Configuration")
                .translation("cobbled_level_control.configuration.server.messages.error")
                .push("error");
        messages_error_useActionBar = builder.comment(
                        "Whether to use the action bar for error messages",
                        "If set to false, error messages will be sent in chat",
                        "Note: useActionBar is only applied to the messages being sent to the target user, and not the user/console executing the command"
                )
                .translation("cobbled_level_control.configuration.server.messages.error.useActionBar")
                .define("useActionBar", false);
        messages_error_battle = builder.comment("Message sent to players when they try to battle with a Pokémon that exceeds their leveling cap")
                .translation("cobbled_level_control.configuration.server.messages.error.battle")
                .define("battle", "One or more of your Pokemon exceeds your leveling cap! Please put it in your PC!");
        messages_error_catchingTier = builder.comment("Message sent to players when they try to catch a Pokémon that exceeds their catching tier")
                .translation("cobbled_level_control.configuration.server.messages.error.catchingTier")
                .define("catchingTier", "Your Catching Tier level is too low for this Pokemon!");
        messages_error_levelingTier = builder.comment("Message sent to players when they try to level up a Pokémon that exceeds their leveling tier")
                .translation("cobbled_level_control.configuration.server.messages.error.levelingTier")
                .define("levelingTier", "Your Leveling Tier level is too low to level up this Pokemon!");
        messages_error_missingPermission = builder.comment("Message sent to players when they try to execute a command without the required permission")
                .translation("cobbled_level_control.configuration.server.messages.error.missingPermission")
                .define("missingPermission", "You do not have permission to do that!");
        messages_error_catchingLevelToHigh = builder.comment("Message sent to players when they try to set a catching level that exceeds the maximum allowed for their difficulty")
                .translation("cobbled_level_control.configuration.server.messages.error.catchingLevelToHigh")
                .define("catchingLevelToHigh", "Level exceeds maximum level for Catching module. Max level is %maxLevel%.");
        messages_error_levelingLevelToHigh = builder.comment("Message sent to players when they try to set a leveling level that exceeds the maximum allowed for their difficulty")
                .translation("cobbled_level_control.configuration.server.messages.error.levelingLevelToHigh")
                .define("levelingLevelToHigh", "Level exceeds maximum level for Leveling module. Max level is %maxLevel%.");
        messages_error_invalidModule = builder.comment("Message sent to players when they try to set a module that does not exist")
                .translation("cobbled_level_control.configuration.server.messages.error.invalidModule")
                .define("invalidModule", "Invalid module specified. Valid modules are: catch, level.");
        messages_error_catchingLevelAlreadyMax = builder.comment("Message sent to players when they try to set a catching level that is already at the maximum allowed for their difficulty")
                .translation("cobbled_level_control.configuration.server.messages.error.catchingLevelAlreadyMax")
                .define("catchingLevelAlreadyMax", "Target player is already at the maximum level for the Catching module.");
        messages_error_levelingLevelAlreadyMax = builder.comment("Message sent to players when they try to set a leveling level that is already at the maximum allowed for their difficulty")
                .translation("cobbled_level_control.configuration.server.messages.error.levelingLevelAlreadyMax")
                .define("levelingLevelAlreadyMax", "Target player is already at the maximum level for the Leveling module.");
        builder.pop(); // Closes "server.messages.error"

        builder.pop(); // Closes "server.messages"

        builder.pop(); // Closes "server"
    }
}
