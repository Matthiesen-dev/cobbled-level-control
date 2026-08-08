package dev.matthiesen.cobbled_level_control.common.runtime.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import kotlin.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class LevelUpListener {
    public static ObservableSubscription<LevelUpEvent> register() {
        return CobblemonEvents.LEVEL_UP_EVENT.subscribe(Priority.NORMAL, event -> {
            var modInstance = CobbledLevelControl.INSTANCE;
            Pokemon pokemon = event.getPokemon();
            ServerPlayer player = pokemon.getOwnerPlayer();
            if (player == null) return Unit.INSTANCE;
            var playerData = modInstance.getStoredPlayerAccountRecords().getPlayerAccountRecord(player.getUUID());
            var levelingModule = CLCConfig.getLevelingConfig();
            if (levelingModule.doNotRestrictLeveling()) return Unit.INSTANCE;
            int tierLevel = playerData.getLeveling();
            int maxLevel = levelingModule.tiers().get(Integer.toString(tierLevel));
            int pokemonLevel = pokemon.getLevel();
            if (pokemonLevel >= maxLevel) {
                event.setNewLevel(pokemonLevel);
                player.sendSystemMessage(Component.literal(CLCConfig.SERVER_CONFIG.messages_error_levelingTier.get())
                        .withStyle(ChatFormatting.RED), CLCConfig.SERVER_CONFIG.messages_error_useActionBar.getAsBoolean());
            }
            return Unit.INSTANCE;
        });
    }
}
