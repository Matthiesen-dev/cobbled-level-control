package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
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
            if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
            RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
            var levelingModule = difficulty.getLevelingModule();
            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.tiers.get(tierLevel);
            int pokemonLevel = pokemon.getLevel();
            if (pokemonLevel >= maxLevel) {
                event.setExperienceYield(0);
                var config = modInstance.getConfigManager().getMessagesConfig();
                player.sendSystemMessage(Component.literal(config.errors.levelingTier).withStyle(ChatFormatting.RED), config.errors.useActionBar);
            }
            return Unit.INSTANCE;
        });
    }
}
