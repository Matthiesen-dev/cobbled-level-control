package dev.matthiesen.cobbled_level_control.common.utils;

import java.util.Random;

public final class Randomizer {
    public static Random random = new Random();

    public static int getRandomNumberBetween(int min, int max) {
        return random.nextInt(Math.max(1, max - min + 1)) + min;
    }

    public static boolean getRandomChance(double chance) {
        if (chance >= (double)1.0F) {
            return true;
        } else {
            return random.nextDouble() < chance;
        }
    }
}
