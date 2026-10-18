package dev.matthiesen.cobbled_level_control.common.config.def;

public record EvolutionStages(
        PermSet singleEvo,
        PermSet firstStageEvo,
        PermSet secondStageEvo,
        PermSet finalStageEvo
) {
}
