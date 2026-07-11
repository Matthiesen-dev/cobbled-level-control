package dev.matthiesen.cobbled_level_control.common.mixin;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import dev.matthiesen.cobbled_level_control.common.CobbledLevelControl;
import dev.matthiesen.cobbled_level_control.common.utils.BattleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BattleActor.class, remap = false)
public abstract class BattleRegistryMixin {
    @Shadow
    public abstract PokemonBattle getBattle();

    @Inject(method = "getPokemonList", at = @At("RETURN"), cancellable = true)
    private void cobbledLevelControl$filterRestrictedPokemon(CallbackInfoReturnable<List<BattlePokemon>> cir) {
        List<BattlePokemon> team = cir.getReturnValue();
        if (team == null || team.isEmpty()) {
            CobbledLevelControl.INSTANCE.createInfoLog("[BattleRegistryMixin] getPokemonList returned no Pokémon; nothing to filter.");
            return;
        }

        PokemonBattle battle = getBattle();
        if (battle == null || !battle.isPvW()) {
            CobbledLevelControl.INSTANCE.createInfoLog("[BattleRegistryMixin] Skipping filter because battle is " + (battle == null ? "null" : "not PvW") + ". Team size=" + team.size());
            return;
        }

        int before = team.size();
        team.removeIf(BattleUtils::isRestricted);
        int removed = before - team.size();
        if (removed > 0) {
            CobbledLevelControl.INSTANCE.createInfoLog("[BattleRegistryMixin] Removed " + removed + " restricted Pokémon from battle " + battle.getBattleId() + ". Remaining team size=" + team.size());
        } else {
            CobbledLevelControl.INSTANCE.createInfoLog("[BattleRegistryMixin] Battle " + battle.getBattleId() + " is PvW, but no restricted Pokémon were removed.");
        }

        cir.setReturnValue(team);
    }
}
