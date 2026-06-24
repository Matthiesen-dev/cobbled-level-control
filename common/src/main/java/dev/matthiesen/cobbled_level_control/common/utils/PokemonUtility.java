package dev.matthiesen.cobbled_level_control.common.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;

public final class PokemonUtility {
    public static EvoStage getEvoStage(Pokemon pokemon) {
        var preEvolution = pokemon.getForm().getPreEvolution();
        var evolutionsCount = pokemon.getForm().getEvolutions().size();

        if (preEvolution == null) {
            if (evolutionsCount == 0) return EvoStage.SINGLE;
            return EvoStage.FIRST;
        }
        if (evolutionsCount == 0) return EvoStage.FINAL;
        return EvoStage.SECOND;
    }

    public enum EvoStage {
        SINGLE,
        FIRST,
        SECOND,
        FINAL
    }
}
