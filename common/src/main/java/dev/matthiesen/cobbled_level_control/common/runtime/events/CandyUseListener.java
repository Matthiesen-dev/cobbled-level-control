package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.interaction.ExperienceCandyUseEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
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
            var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
            var levelingModule = CLCConfig.getLevelingConfig();
            if (levelingModule.doNotRestrictLeveling()) return Unit.INSTANCE;
            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.tiers().get(Integer.toString(tierLevel));
            int pokemonLevel = pokemon.getLevel();
            if (pokemonLevel >= maxLevel) {
                event.setExperienceYield(0);
                player.sendSystemMessage(Component.literal(CLCConfig.SERVER_CONFIG.messages_error_levelingTier.get()).withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.get());
            }
            return Unit.INSTANCE;
        });
    }
}
