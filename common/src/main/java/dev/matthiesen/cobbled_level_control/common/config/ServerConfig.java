package dev.matthiesen.cobbled_level_control.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ServerConfig {

    // Main Configuration
    public ModConfigSpec.ConfigValue<List<? extends String>> difficulties;

    // Defaults
    public ModConfigSpec.ConfigValue<String> defaults_defaultDifficulty;
    public ModConfigSpec.BooleanValue defaults_autoApplyDefault;

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
    public ModConfigSpec.ConfigValue<String> messages_success_targetSetDifficulty;
    public ModConfigSpec.ConfigValue<String> messages_success_sourceSetDifficulty;
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
    public ModConfigSpec.ConfigValue<String> messages_error_invalidDifficulty;
    public ModConfigSpec.ConfigValue<String> messages_error_missingDifficulty;
    public ModConfigSpec.ConfigValue<String> messages_error_difficultyDoesNotExist;
    public ModConfigSpec.ConfigValue<String> messages_error_catchingLevelToHigh;
    public ModConfigSpec.ConfigValue<String> messages_error_levelingLevelToHigh;
    public ModConfigSpec.ConfigValue<String> messages_error_invalidModule;
    public ModConfigSpec.ConfigValue<String> messages_error_catchingLevelAlreadyMax;
    public ModConfigSpec.ConfigValue<String> messages_error_levelingLevelAlreadyMax;

    public ServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Server Configuration").push("server");

        difficulties = builder.comment(
                        "List of difficulty names to load from the 'difficulties' folder",
                        "These should match the names of the difficulty config files (without the .json extension)"
                )
                .defineList(
                        "difficulties",
                        List.of("default"),
                        () -> "",
                        o -> o instanceof String
                );

        builder.comment("Defaults Configuration").push("defaults");
        defaults_defaultDifficulty = builder.comment(
                        "The default difficulty to apply to new players",
                        "This should match the name of a difficulty config file (without the .json extension)"
                )
                .define("defaultDifficulty", "default");
        defaults_autoApplyDefault = builder.comment(
                        "Whether to automatically apply the default difficulty to new players",
                        "If set to false, players will need to manually select a difficulty"
                )
                .define("autoApplyDefault", true);
        builder.pop(); // Closes "server.defaults"

        builder.comment("Scaling Configuration").push("scaling");
        scaling_enableScaling = builder.comment(
                        "Whether to enable scaling of wild Pokémon levels based on player level",
                        "If set to false, wild Pokémon will spawn at their default levels"
                )
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
                .define("scalingMethod", "+- random7");
        builder.pop(); // Closes "server.scaling"

        builder.comment("Messages Configuration").push("messages");

        builder.comment("Success Messages Configuration").push("success");
        messages_success_useActionBar = builder.comment(
                        "Whether to use the action bar for success messages",
                        "If set to false, success messages will be sent in chat",
                        "Note: useActionBar is only applied to the messages being sent to the target user, and not the user/console executing the command"
                )
                .define("useActionBar", false);
        messages_success_reloaded = builder.comment("Message sent to players when the plugin is reloaded")
                .define("reloaded", "Cobbled Level Control configuration reloaded successfully.");
        messages_success_targetCatchingLevelSet = builder.comment("Message sent to players when their catching level is set")
                .define("targetCatchingLevelSet", "Your Catching level has been set to %level%.");
        messages_success_sourceCatchingLevelSet = builder.comment("Message sent to players when they set another player's catching level")
                .define("sourceCatchingLevelSet", "Set Catching level of %target% to %level%.");
        messages_success_targetLevelingLevelSet = builder.comment("Message sent to players when their leveling level is set")
                .define("targetLevelingLevelSet", "Your Leveling level has been set to %level%.");
        messages_success_sourceLevelingLevelSet = builder.comment("Message sent to players when they set another player's leveling level")
                .define("sourceLevelingLevelSet", "Set Leveling level of %target% to %level%.");
        messages_success_targetSetDifficulty = builder.comment("Message sent to players when their difficulty is set")
                .define("targetSetDifficulty", "Your difficulty has been set to %difficulty%!");
        messages_success_sourceSetDifficulty = builder.comment("Message sent to players when they set another player's difficulty")
                .define("sourceSetDifficulty", "Set %target%'s difficulty to %difficulty%!");
        messages_success_targetCatchingTierSet = builder.comment("Message sent to players when their catching tier is set")
                .define("targetCatchingTierSet", "Your tier in catching has increased to %tier%!");
        messages_success_sourceCatchingTierSet = builder.comment("Message sent to players when they set another player's catching tier")
                .define("sourceCatchingTierSet", "Set %target%'s tier in catching to %tier%!");
        messages_success_targetLevelingTierSet = builder.comment("Message sent to players when their leveling tier is set")
                .define("targetLevelingTierSet", "Your tier in leveling has increased to %tier%!");
        messages_success_sourceLevelingTierSet = builder.comment("Message sent to players when they set another player's leveling tier")
                .define("sourceLevelingTierSet", "Set %target%'s tier in leveling to %tier%!");
        builder.pop(); // Closes "server.messages.success"

        builder.comment("Error Messages Configuration").push("error");
        messages_error_useActionBar = builder.comment(
                        "Whether to use the action bar for error messages",
                        "If set to false, error messages will be sent in chat",
                        "Note: useActionBar is only applied to the messages being sent to the target user, and not the user/console executing the command"
                )
                .define("useActionBar", false);
        messages_error_battle = builder.comment("Message sent to players when they try to battle with a Pokémon that exceeds their leveling cap")
                .define("battle", "One or more of your Pokemon exceeds your leveling cap! Please put it in your PC!");
        messages_error_catchingTier = builder.comment("Message sent to players when they try to catch a Pokémon that exceeds their catching tier")
                .define("catchingTier", "Your Catching Tier level is too low for this Pokemon!");
        messages_error_levelingTier = builder.comment("Message sent to players when they try to level up a Pokémon that exceeds their leveling tier")
                .define("levelingTier", "Your Leveling Tier level is too low to level up this Pokemon!");
        messages_error_missingPermission = builder.comment("Message sent to players when they try to execute a command without the required permission")
                .define("missingPermission", "You do not have permission to do that!");
        messages_error_invalidDifficulty = builder.comment("Message sent to players when they try to set a difficulty that does not exist")
                .define("invalidDifficulty", "The difficulty you specified is invalid!");
        messages_error_missingDifficulty = builder.comment("Message sent to players when they try to set a difficulty for a player that does not have a difficulty set")
                .define("missingDifficulty", "Target player does not have a difficulty set. Please set a difficulty first.");
        messages_error_difficultyDoesNotExist = builder.comment("Message sent to players when they try to set a difficulty that does not exist")
                .define("difficultyDoesNotExist", "Difficulty %difficultyName% does not exist!");
        messages_error_catchingLevelToHigh = builder.comment("Message sent to players when they try to set a catching level that exceeds the maximum allowed for their difficulty")
                .define("catchingLevelToHigh", "Level exceeds maximum level for Catching module. Max level is %maxLevel%.");
        messages_error_levelingLevelToHigh = builder.comment("Message sent to players when they try to set a leveling level that exceeds the maximum allowed for their difficulty")
                .define("levelingLevelToHigh", "Level exceeds maximum level for Leveling module. Max level is %maxLevel%.");
        messages_error_invalidModule = builder.comment("Message sent to players when they try to set a module that does not exist")
                .define("invalidModule", "Invalid module specified. Valid modules are: catch, level.");
        messages_error_catchingLevelAlreadyMax = builder.comment("Message sent to players when they try to set a catching level that is already at the maximum allowed for their difficulty")
                .define("catchingLevelAlreadyMax", "Target player is already at the maximum level for the Catching module.");
        messages_error_levelingLevelAlreadyMax = builder.comment("Message sent to players when they try to set a leveling level that is already at the maximum allowed for their difficulty")
                .define("levelingLevelAlreadyMax", "Target player is already at the maximum level for the Leveling module.");
        builder.pop(); // Closes "server.messages.error"

        builder.pop(); // Closes "server.messages"

        builder.pop(); // Closes "server"
    }
}
