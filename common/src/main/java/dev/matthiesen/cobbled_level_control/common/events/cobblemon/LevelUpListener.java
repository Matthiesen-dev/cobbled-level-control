package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.Difficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.Leveling;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class LevelUpListener {
    public static ObservableSubscription<LevelUpEvent> register() {
        return CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.NORMAL, event -> {
            var modInstance = CobbledLevelControl.INSTANCE;
            var modConfig = modInstance.getConfigManager().getMainConfig();

            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player == null) return Unit.INSTANCE;

            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();

            if (playerDiffValue.equalsIgnoreCase("none")) return Unit.INSTANCE;

            Difficulty difficulty = modInstance.getDifficulty(playerDiffValue);

            Leveling levelingModule = difficulty.leveling();

            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.config().tiers.get(tierLevel);
            int pokemonLevel = pokemon.getLevel();

            if (pokemonLevel >= maxLevel) {
                event.setNewLevel(pokemonLevel);
                player.sendSystemMessage(Component.literal(modConfig.errorMessages.levelingTier), modConfig.errorMessages.useActionBar);
            }

            return Unit.INSTANCE;
        });
    }
}
