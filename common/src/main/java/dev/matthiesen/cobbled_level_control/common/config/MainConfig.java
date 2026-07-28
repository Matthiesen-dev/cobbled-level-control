package dev.matthiesen.cobbled_level_control.common.config;

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

    public static class Defaults {
        @SerializedName("defaultDifficulty")
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
}
