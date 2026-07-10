package dev.matthiesen.cobbled_level_control.common.mixin;

import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import dev.matthiesen.cobbled_level_control.common.utils.BattleUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = BattleRegistry.class, remap = false)
public abstract class BattleRegistryMixin {
    @Redirect(
            method = "startShowdown",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/battles/BattleRegistry;packTeam(Ljava/util/List;)Ljava/lang/String;"
            )
    )
    public String cobbledLevelControl$packTeam(BattleRegistry instance, List<? extends BattlePokemon> team) {
        team.removeIf(BattleUtils::isRestricted);
        return BattleRegistry.INSTANCE.packTeam(team);
    }
}
