package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.Difficulty;
import dev.matthiesen.cobbled_level_control.common.runtime.Leveling;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CandyUseListener {
    public static ObservableSubscription<ExperienceCandyUseEvent.Pre> register() {
        return CobblemonEvents.EXPERIENCE_CANDY_USE_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = event.getPlayer();

            var modInstance = CobbledLevelControl.INSTANCE;

            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();

            if (playerDiffValue.equalsIgnoreCase("none")) return Unit.INSTANCE;

            Difficulty difficulty = modInstance.getDifficulty(playerDiffValue);
            Leveling levelingModule = difficulty.leveling();

            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.config().tiers.get(tierLevel);
            int pokemonLevel = pokemon.getLevel();

            if (pokemonLevel >= maxLevel) {
                event.setExperienceYield(0);
                var config = modInstance.getConfigManager().getMainConfig();
                player.sendSystemMessage(Component.literal(config.errorMessages.levelingTier).withStyle(ChatFormatting.RED), config.errorMessages.useActionBar);
            }

            return Unit.INSTANCE;
        });
    }
}
