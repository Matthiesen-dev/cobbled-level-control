package dev.matthiesen.cobbled_level_control.common.utils;

import com.cobblemon.mod.common.Cobblemon;

public final class ScalingUtils {
    private static final String RandomParam = "random";
    private static final String PlusOrMinusParam = "+-";
    private static final String PlusParam = "+";

    public static int getNewLevel(int maxLevel, String scalingMethod) {
        String[] split = scalingMethod.split(" ");
        String function = split[0];
        String amount = split[1];

        int maxAmount;

        if (amount.contains(RandomParam)) {
            maxAmount = Randomizer.getRandomNumberBetween(1, Integer.parseInt(amount.replace(RandomParam, "")));
        } else {
            maxAmount = Integer.parseInt(amount);
        }

        int newLevel;

        switch (function) {
            case PlusOrMinusParam -> {
                if (Randomizer.getRandomChance(50)) {
                    newLevel = Math.min(Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel(), maxAmount + maxLevel);
                } else {
                    int value;
                    if (maxLevel > maxAmount) {
                        value = maxLevel - maxAmount;
                    } else {
                        value = maxAmount - maxLevel;
                    }
                    newLevel = Math.max(1, value);
                }
            }
            case PlusParam -> newLevel = Math.min(Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel(), maxAmount + maxLevel);
            default -> {
                int value;
                if (maxLevel > maxAmount) {
                    value = maxLevel - maxAmount;
                } else {
                    value = maxAmount - maxLevel;
                }
                newLevel = Math.max(1, value);
            }
        }

        return newLevel;
    }
}
