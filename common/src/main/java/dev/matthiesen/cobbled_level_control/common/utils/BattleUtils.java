package dev.matthiesen.cobbled_level_control.common.utils;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class BattleUtils {
    public static <T extends BattlePokemon> boolean isRestricted(T battlePokemon) {
        PokemonBattle battle = battlePokemon.getActor().getBattle();
        Pokemon pokemon = battlePokemon.getEffectedPokemon();
        ServerPlayer player = getPokemonOwner(pokemon);
        if (player == null) return false;

        var modInstance = CobbledLevelControl.INSTANCE;
        var modConfig = modInstance.getConfigManager();

        var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
        String playerDiffValue = playerData.getDifficulty();
        if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return false;
        RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
        var battleModule = difficulty.getBattleModule();
        if (battleModule.doNotRestrictBattles()) return false;

        if (!battle.isPvW()) return false;

        var levelingModule = difficulty.getLevelingModule();
        if (levelingModule.doRestrictLeveling()) {
            int pokemonLevel = pokemon.getLevel();

            int levelingLevel = playerData.getLeveling();
            int levelingMaxLevel = levelingModule.getConfig().tiers.get(Integer.toString(levelingLevel));
            if (pokemonLevel > levelingMaxLevel) {
                Component message = Component.literal(modConfig.getMessagesConfig().errors.battle).withStyle(ChatFormatting.RED);
                player.sendSystemMessage(message, modConfig.getMessagesConfig().errors.useActionBar);
                battle.broadcastChatMessage(message);
                return true;
            }
        }

        return false;
    }

    public static ServerPlayer getPokemonOwner(Pokemon pokemon) {
        return pokemon.getOwnerPlayer();
    }
}
