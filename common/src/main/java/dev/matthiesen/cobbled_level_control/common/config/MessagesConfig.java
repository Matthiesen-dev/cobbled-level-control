package dev.matthiesen.cobbled_level_control.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public final class MessagesConfig {
    @SerializedName("messages")
    public SuccessMessages messages = new SuccessMessages();

    @SerializedName("errors")
    public ErrorMessages errors = new ErrorMessages();

    public static class SuccessMessages {
        @SerializedName("targetCatchingLevelSet")
        public String targetCatchingLevelSet = "Your Catching level has been set to %level%.";

        @SerializedName("sourceCatchingLevelSet")
        public String sourceCatchingLevelSet = "Set Catching level of %target% to %level%.";

        @SerializedName("targetLevelingLevelSet")
        public String targetLevelingLevelSet = "Your Leveling level has been set to %level%.";

        @SerializedName("sourceLevelingLevelSet")
        public String sourceLevelingLevelSet = "Set Leveling level of %target% to %level%.";

        @SerializedName("targetSetDifficulty")
        public String targetSetDifficulty = "Your difficulty has been set to %difficulty%!";

        @SerializedName("sourceSetDifficulty")
        public String sourceSetDifficulty = "Set %target%'s difficulty to %difficulty%!";

        @SerializedName("targetCatchingTierSet")
        public String targetCatchingTierSet = "Your tier in catching has increased to %tier%!";

        @SerializedName("sourceCatchingTierSet")
        public String sourceCatchingTierSet = "Set %target%'s tier in catching to %tier%!";

        @SerializedName("targetLevelingTierSet")
        public String targetLevelingTierSet = "Your tier in leveling has increased to %tier%!";

        @SerializedName("sourceLevelingTierSet")
        public String sourceLevelingTierSet = "Set %target%'s tier in leveling to %tier%!";
    }

    public static class ErrorMessages {
        @SerializedName("useActionBar")
        public boolean useActionBar = false;

        @SerializedName("battle")
        public String battle = "One or more of your Pokemon exceeds your leveling cap! Please put it in your PC!";

        @SerializedName("catchingTier")
        public String catchingTier = "Your Catching Tier level is too low for this Pokemon!";

        @SerializedName("levelingTier")
        public String levelingTier = "Your Leveling Tier level is too low to level up this Pokemon!";

        @SerializedName("missingPermission")
        public String missingPermission = "You do not have permission to do that!";

        @SerializedName("invalidDifficulty")
        public String invalidDifficulty = "The difficulty you specified is invalid!";

        @SerializedName("missingDifficulty")
        public String missingDifficulty = "Target player does not have a difficulty set. Please set a difficulty first.";

        @SerializedName("difficultyDoesNotExist")
        public String difficultyDoesNotExist = "Difficulty %difficultyName% does not exist!";

        @SerializedName("catchingLevelToHigh")
        public String catchingLevelToHigh = "Level exceeds maximum level for Catching module. Max level is %maxLevel%.";

        @SerializedName("levelingLevelToHigh")
        public String levelingLevelToHigh = "Level exceeds maximum level for Leveling module. Max level is %maxLevel%.";

        @SerializedName("invalidModule")
        public String invalidModule = "Invalid module specified. Valid modules are: catch, level.";

        @SerializedName("catchingLevelAlreadyMax")
        public String catchingLevelAlreadyMax = "Target player is already at the maximum level for the Catching module.";

        @SerializedName("levelingLevelAlreadyMax")
        public String levelingLevelAlreadyMax = "Target player is already at the maximum level for the Leveling module.";
    }

    @SuppressWarnings("unused")
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
}
