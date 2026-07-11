package dev.matthiesen.cobbled_level_control.common.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleRegistry;
import dev.matthiesen.cobbled_level_control.common.utils.BattleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BattleRegistry.class, remap = false)
public abstract class BattleRegistryMixin {
    @Inject(method = "startShowdown", at = @At("HEAD"))
    private void cobbledLevelControl$filterRestrictedPokemon(PokemonBattle battle, CallbackInfo ci) {
        for (var actor : battle.getActors()) {
            actor.getPokemonList().removeIf(BattleUtils::isRestricted);
        }
    }
}
