package dev.matthiesen.cobbled_level_control.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class MainConfig {
    @SerializedName("difficulties")
    public List<String> difficulties = List.of(
            "default"
    );

    @SerializedName("defaults")
    public Defaults defaults = new Defaults();

    @SerializedName("spawnConfig")
    public SpawnConfig spawnConfig = new SpawnConfig();

    @SerializedName("errorMessages")
    public ErrorMessages errorMessages = new ErrorMessages();

    @SerializedName("saveConfig")
    public SaveConfig saveConfig = new SaveConfig();

    public static class Defaults {
        @SerializedName("default")
        public String defaultDifficulty = "default";

        @SerializedName("autoApplyDefault")
        public Boolean autoApplyDefault = true;
    }

    public static class SpawnConfig {
        @SerializedName("enableScaling")
        public Boolean enableScaling = true;

        @SerializedName("scalingMethod")
        public String scalingMethod = "+- random7";
    }

    public static class ErrorMessages {
        @SerializedName("battle")
        public String battle = "One or more of your Pokemon exceeds your leveling cap! Please put it in your PC!";

        @SerializedName("catchingTier")
        public String catchingTier = "Your Catching Tier level is too low for this Pokemon!";

        @SerializedName("levelingTier")
        public String levelingTier = "Your Leveling Tier level is too low to level up this Pokemon!";

        @SerializedName("missingPermission")
        public String missingPermission = "You do not have permission to do that!";
    }

    public static class SaveConfig {
        @SerializedName("enableAutoSave")
        public Boolean enableAutoSave = true;

        @SerializedName("saveIntervalTicks")
        public Integer saveIntervalTicks = 100;
    }

    @SuppressWarnings("unused")
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
}
