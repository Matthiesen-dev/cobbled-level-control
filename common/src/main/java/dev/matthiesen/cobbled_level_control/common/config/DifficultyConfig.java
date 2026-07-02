package dev.matthiesen.cobbled_level_control.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public final class DifficultyConfig {
    @SerializedName("battles")
    public BattleConfig battles = new BattleConfig();

    @SerializedName("catching")
    public CatchingConfig catching = new CatchingConfig();

    @SerializedName("leveling")
    public LevelingConfig leveling = new LevelingConfig();

    public static class BattleConfig {
        @SerializedName("restrictBattles")
        public Boolean restrictBattles = true;
    }

    public static class CatchingConfig {
        @SerializedName("restrictCatching")
        public Boolean restrictCatching = true;

        @SerializedName("evolutionStages")
        public CatchingEvolutionStagePermissions evolutionStages = new CatchingEvolutionStagePermissions();

        @SerializedName("legendary")
        public String legendary = "cobbled_level_control.catching.legendary";

        @SerializedName("mythical")
        public String mythical = "cobbled_level_control.catching.mythical";

        @SerializedName("ultraBeast")
        public String ultraBeast = "cobbled_level_control.catching.ultra_beast";

        @SerializedName("shiny")
        public String shiny = "cobbled_level_control.catching.shiny";

        @SerializedName("tiers")
        public Map<Integer, Integer> tiers = tierConfig;
    }

    public static class LevelingConfig {
        @SerializedName("restrictLeveling")
        public Boolean restrictLeveling = true;

        @SerializedName("evolutionStages")
        public LevelingEvolutionStagePermissions evolutionStages = new LevelingEvolutionStagePermissions();

        @SerializedName("tiers")
        public Map<Integer, Integer> tiers = tierConfig;
    }

    public static class CatchingEvolutionStagePermissions {
        public String singleEvo = "";
        public String firstStageEvo = "";
        public String secondStageEvo = "cobbled_level_control.catching.second_stage_evo";
        public String finalStageEvo = "cobbled_level_control.catching.final_stage_evo";
    }

    public static class LevelingEvolutionStagePermissions {
        public String singleEvo = "";
        public String firstStageEvo = "";
        public String secondStageEvo = "cobbled_level_control.leveling.second_stage_evo";
        public String finalStageEvo = "cobbled_level_control.leveling.final_stage_evo";
    }

    public static final Map<Integer, Integer> tierConfig = Map.of(
            1, 10,
            2, 20,
            3, 40,
            4, 80,
            5, 100
    );

    @SuppressWarnings("unused")
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
}
