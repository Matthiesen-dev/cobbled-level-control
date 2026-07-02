package dev.matthiesen.cobbled_level_control.common.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.runtime.RuntimeDifficulty;
import kotlin.Unit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ExperienceGainedListener {
    public static ObservableSubscription<ExperienceGainedEvent.Pre> register() {
        return CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            var modInstance = CobbledLevelControl.INSTANCE;
            var modConfig = modInstance.getConfigManager().getMessagesConfig();
            Pokemon pokemon = event.getPokemon();
            if (!pokemon.isPlayerOwned()) return Unit.INSTANCE;
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player == null) {
                modInstance.createInfoLog("An error was detected trying to get a player owner from a Pokemon. Printing debug info.");
                modInstance.createInfoLog(pokemon.getPersistentData().toString());
                modInstance.createInfoLog("Player owned? " + pokemon.isPlayerOwned());
                return Unit.INSTANCE;
            }
            var playerData = modInstance.getConfigManager().getPlayerAccountRecord(player.getUUID());
            String playerDiffValue = playerData.getDifficulty();
            if (playerDiffValue.equalsIgnoreCase(RuntimeDifficulty.emptyDifficulty)) return Unit.INSTANCE;
            RuntimeDifficulty difficulty = modInstance.getDifficulty(playerDiffValue);
            var levelingModule = difficulty.getLevelingModule();
            if (levelingModule.doRestrictLeveling()) return Unit.INSTANCE;
            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.getConfig().tiers.get(tierLevel);
            int pokemonLevel = pokemon.getLevel();
            int experience = event.getExperience();
            int experienceRequired = pokemon.getExperienceToLevel(maxLevel + 1);
            if (pokemonLevel >= maxLevel || experience >= experienceRequired) {
                event.cancel();
                event.setExperience(Math.max(experienceRequired - 1, 0));
                player.sendSystemMessage(Component.literal(modConfig.errors.levelingTier), modConfig.errors.useActionBar);
            }
            return Unit.INSTANCE;
        });
    }
}
