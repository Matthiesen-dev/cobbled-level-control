package dev.matthiesen.cobbled_level_control.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.matthiesen.cobbled_level_control.common.config.CLCConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.Set;

/**
 * Handles the {@code /level-control configure} sub-command, allowing operators to adjust
 * {@link dev.matthiesen.cobbled_level_control.common.config.ServerConfig ServerConfig} values
 * at runtime without restarting the server.
 *
 * <h2>Command Syntax</h2>
 * <pre>{@code
 * /level-control configure <module> <property> <value>
 * }</pre>
 *
 * <h2>Modules &amp; Properties</h2>
 * <table border="1">
 *   <caption>Configurable modules and their properties</caption>
 *   <tr><th>Module</th><th>Property</th><th>Type</th><th>Description</th></tr>
 *   <tr><td rowspan="9">{@code battle}</td>
 *       <td>{@code restrictBattles}</td><td>boolean</td><td>Enables/disables battle level restrictions</td></tr>
 *   <tr><td>{@code legendary}</td><td>string</td><td>Permission node required to battle legendary Pokémon</td></tr>
 *   <tr><td>{@code mythical}</td><td>string</td><td>Permission node required to battle mythical Pokémon</td></tr>
 *   <tr><td>{@code ultraBeast}</td><td>string</td><td>Permission node required to battle ultra beast Pokémon</td></tr>
 *   <tr><td>{@code shiny}</td><td>string</td><td>Permission node required to battle shiny Pokémon</td></tr>
 *   <tr><td>{@code evoStages.singleEvo}</td><td>string</td><td>Permission node for single-evolution Pokémon battles</td></tr>
 *   <tr><td>{@code evoStages.firstStageEvo}</td><td>string</td><td>Permission node for first-stage evolution battles</td></tr>
 *   <tr><td>{@code evoStages.secondStageEvo}</td><td>string</td><td>Permission node for second-stage evolution battles</td></tr>
 *   <tr><td>{@code evoStages.finalStageEvo}</td><td>string</td><td>Permission node for final-stage evolution battles</td></tr>
 *
 *   <tr><td rowspan="9">{@code catching}</td>
 *       <td>{@code restrictCatching}</td><td>boolean</td><td>Enables/disables catching level restrictions</td></tr>
 *   <tr><td>{@code legendary}</td><td>string</td><td>Permission node required to catch legendary Pokémon</td></tr>
 *   <tr><td>{@code mythical}</td><td>string</td><td>Permission node required to catch mythical Pokémon</td></tr>
 *   <tr><td>{@code ultraBeast}</td><td>string</td><td>Permission node required to catch ultra beast Pokémon</td></tr>
 *   <tr><td>{@code shiny}</td><td>string</td><td>Permission node required to catch shiny Pokémon</td></tr>
 *   <tr><td>{@code evoStages.singleEvo}</td><td>string</td><td>Permission node for single-evolution Pokémon catching</td></tr>
 *   <tr><td>{@code evoStages.firstStageEvo}</td><td>string</td><td>Permission node for first-stage evolution catching</td></tr>
 *   <tr><td>{@code evoStages.secondStageEvo}</td><td>string</td><td>Permission node for second-stage evolution catching</td></tr>
 *   <tr><td>{@code evoStages.finalStageEvo}</td><td>string</td><td>Permission node for final-stage evolution catching</td></tr>
 *
 *   <tr><td rowspan="5">{@code leveling}</td>
 *       <td>{@code restrictLeveling}</td><td>boolean</td><td>Enables/disables leveling level restrictions</td></tr>
 *   <tr><td>{@code evoStages.singleEvo}</td><td>string</td><td>Permission node for single-evolution Pokémon leveling</td></tr>
 *   <tr><td>{@code evoStages.firstStageEvo}</td><td>string</td><td>Permission node for first-stage evolution leveling</td></tr>
 *   <tr><td>{@code evoStages.secondStageEvo}</td><td>string</td><td>Permission node for second-stage evolution leveling</td></tr>
 *   <tr><td>{@code evoStages.finalStageEvo}</td><td>string</td><td>Permission node for final-stage evolution leveling</td></tr>
 *
 *   <tr><td rowspan="2">{@code scaling}</td>
 *       <td>{@code enableScaling}</td><td>boolean</td><td>Enables/disables wild Pokémon level scaling</td></tr>
 *   <tr><td>{@code scalingMethod}</td><td>string</td><td>Scaling formula, e.g. {@code "+- random7"}, {@code "+ 0"}</td></tr>
 * </table>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * /level-control configure battle restrictBattles false
 * /level-control configure catching legendary cobbled_level_control.catching.legendary
 * /level-control configure scaling enableScaling true
 * /level-control configure scaling scalingMethod "+- random5"
 * /level-control configure leveling evoStages.finalStageEvo cobbled_level_control.leveling.evoStages.final
 * }</pre>
 *
 * <h2>Permissions</h2>
 * Requires the {@code cobbled_level_control.command.level-control.configure} permission node
 * (default level: {@code ALL_COMMANDS}, configurable via {@code permissions.toml}).
 *
 * <h2>Persistence</h2>
 * Each successful change immediately calls {@code .save()} on the specific
 * {@link net.neoforged.neoforge.common.ModConfigSpec.ConfigValue ConfigValue} that was
 * modified, persisting the new value to {@code server.toml} on disk.
 *
 * <p><b>Note:</b> Tier list properties ({@code catchingModule_tiersConfig},
 * {@code levelingModule_tiersConfig}) are intentionally excluded from this command due to
 * their complex TOML list structure; edit them directly in {@code server.toml} instead.</p>
 */
public final class ConfigureCommand {

    public static final ConfigureCommand CMD = new ConfigureCommand();

    /** Valid property keys for the {@code battle} module. */
    private static final Set<String> BATTLE_PROPERTIES = Set.of(
            "restrictBattles",
            "legendary",
            "mythical",
            "ultraBeast",
            "shiny",
            "evoStages.singleEvo",
            "evoStages.firstStageEvo",
            "evoStages.secondStageEvo",
            "evoStages.finalStageEvo"
    );

    /** Valid property keys for the {@code catching} module. */
    private static final Set<String> CATCHING_PROPERTIES = Set.of(
            "restrictCatching",
            "legendary",
            "mythical",
            "ultraBeast",
            "shiny",
            "evoStages.singleEvo",
            "evoStages.firstStageEvo",
            "evoStages.secondStageEvo",
            "evoStages.finalStageEvo"
    );

    /** Valid property keys for the {@code leveling} module. */
    private static final Set<String> LEVELING_PROPERTIES = Set.of(
            "restrictLeveling",
            "evoStages.singleEvo",
            "evoStages.firstStageEvo",
            "evoStages.secondStageEvo",
            "evoStages.finalStageEvo"
    );

    /** Valid property keys for the {@code scaling} module. */
    private static final Set<String> SCALING_PROPERTIES = Set.of(
            "enableScaling",
            "scalingMethod"
    );

    private static final Map<String, Set<String>> MODULE_PROPERTIES = Map.of(
            "battle", BATTLE_PROPERTIES,
            "catching", CATCHING_PROPERTIES,
            "leveling", LEVELING_PROPERTIES,
            "scaling", SCALING_PROPERTIES
    );

    /**
     * Returns a {@link com.mojang.brigadier.suggestion.SuggestionProvider} that suggests
     * the four configurable module names: {@code battle}, {@code catching},
     * {@code leveling}, and {@code scaling}.
     *
     * @return suggestion provider for the {@code <module>} argument
     */
    public SuggestionProvider<CommandSourceStack> modulesProvider() {
        return (_ctx, builder) -> {
            for (String module : MODULE_PROPERTIES.keySet()) {
                builder.suggest(module);
            }
            return builder.buildFuture();
        };
    }

    /**
     * Returns a {@link com.mojang.brigadier.suggestion.SuggestionProvider} that suggests
     * the valid property keys for whichever module has already been typed in the
     * {@code <module>} argument position. If the module argument is not yet present or
     * unrecognised, no suggestions are returned.
     *
     * @return context-aware suggestion provider for the {@code <property>} argument
     */
    public SuggestionProvider<CommandSourceStack> propertiesProvider() {
        return (ctx, builder) -> {
            try {
                String module = StringArgumentType.getString(ctx, "module");
                Set<String> props = MODULE_PROPERTIES.get(module.toLowerCase());
                if (props != null) {
                    for (String prop : props) {
                        builder.suggest(prop);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // module arg not yet typed; no suggestions
            }
            return builder.buildFuture();
        };
    }

    /**
     * Executes the configure command.
     *
     * <p>Reads the {@code <module>}, {@code <property>}, and {@code <value>} arguments
     * from the command context, applies the change to the matching field in
     * {@link dev.matthiesen.cobbled_level_control.common.config.ServerConfig ServerConfig},
     * and immediately persists it by calling {@code .save()} on that specific
     * {@link net.neoforged.neoforge.common.ModConfigSpec.ConfigValue ConfigValue}.</p>
     *
     * <p>Boolean properties ({@code restrictBattles}, {@code restrictCatching},
     * {@code restrictLeveling}, {@code enableScaling}) accept {@code "true"} or
     * {@code "false"} (case-insensitive, via {@link Boolean#parseBoolean}).</p>
     *
     * <p>On success, sends a green {@code [CLC] Set module.property = value} message
     * to the command source. On failure (unknown module or property), sends a red error
     * message listing the valid options and returns {@code 0}.</p>
     *
     * @param context the Brigadier command context
     * @return {@code 1} on success, {@code 0} on failure
     */
    public int configure(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var cfg = CLCConfig.SERVER_CONFIG;

        String module = StringArgumentType.getString(context, "module").toLowerCase();
        String property = StringArgumentType.getString(context, "property");
        String value = StringArgumentType.getString(context, "value");

        switch (module) {
            case "battle" -> {
                switch (property) {
                    case "restrictBattles" -> {
                        cfg.battleModule_restrictBattles.set(Boolean.parseBoolean(value));
                        cfg.battleModule_restrictBattles.save();
                    }
                    case "legendary"       -> {
                        cfg.battleModule_legendary.set(value);
                        cfg.battleModule_legendary.save();
                    }
                    case "mythical"        -> {
                        cfg.battleModule_mythical.set(value);
                        cfg.battleModule_mythical.save();
                    }
                    case "ultraBeast"      -> {
                        cfg.battleModule_ultraBeast.set(value);
                        cfg.battleModule_ultraBeast.save();
                    }
                    case "shiny"           -> {
                        cfg.battleModule_shiny.set(value);
                        cfg.battleModule_shiny.save();
                    }
                    case "evoStages.singleEvo"    -> {
                        cfg.battleModule_evoStages_singleEvo.set(value);
                        cfg.battleModule_evoStages_singleEvo.save();
                    }
                    case "evoStages.firstStageEvo"  -> {
                        cfg.battleModule_evoStages_firstStageEvo.set(value);
                        cfg.battleModule_evoStages_firstStageEvo.save();
                    }
                    case "evoStages.secondStageEvo" -> {
                        cfg.battleModule_evoStages_secondStageEvo.set(value);
                        cfg.battleModule_evoStages_secondStageEvo.save();
                    }
                    case "evoStages.finalStageEvo"  -> {
                        cfg.battleModule_evoStages_finalStageEvo.set(value);
                        cfg.battleModule_evoStages_finalStageEvo.save();
                    }
                    default -> {
                        source.sendFailure(Component.literal(
                                "Unknown battle property '" + property + "'. Valid properties: " +
                                String.join(", ", BATTLE_PROPERTIES)
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                }
            }
            case "catching" -> {
                switch (property) {
                    case "restrictCatching" -> {
                        cfg.catchingModule_restrictCatching.set(Boolean.parseBoolean(value));
                        cfg.catchingModule_restrictCatching.save();
                    }
                    case "legendary"        -> {
                        cfg.catchingModule_legendary.set(value);
                        cfg.catchingModule_legendary.save();
                    }
                    case "mythical"         -> {
                        cfg.catchingModule_mythical.set(value);
                        cfg.catchingModule_mythical.save();
                    }
                    case "ultraBeast"       -> {
                        cfg.catchingModule_ultraBeast.set(value);
                        cfg.catchingModule_ultraBeast.save();
                    }
                    case "shiny"            -> {
                        cfg.catchingModule_shiny.set(value);
                        cfg.catchingModule_shiny.save();
                    }
                    case "evoStages.singleEvo"    -> {
                        cfg.catchingModule_evoStages_singleEvo.set(value);
                        cfg.catchingModule_evoStages_singleEvo.save();
                    }
                    case "evoStages.firstStageEvo"  -> {
                        cfg.catchingModule_evoStages_firstStageEvo.set(value);
                        cfg.catchingModule_evoStages_firstStageEvo.save();
                    }
                    case "evoStages.secondStageEvo" -> {
                        cfg.catchingModule_evoStages_secondStageEvo.set(value);
                        cfg.catchingModule_evoStages_secondStageEvo.save();
                    }
                    case "evoStages.finalStageEvo"  -> {
                        cfg.catchingModule_evoStages_finalStageEvo.set(value);
                        cfg.catchingModule_evoStages_finalStageEvo.save();
                    }
                    default -> {
                        source.sendFailure(Component.literal(
                                "Unknown catching property '" + property + "'. Valid properties: " +
                                String.join(", ", CATCHING_PROPERTIES)
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                }
            }
            case "leveling" -> {
                switch (property) {
                    case "restrictLeveling" -> {
                        cfg.levelingModule_restrictLeveling.set(Boolean.parseBoolean(value));
                        cfg.levelingModule_restrictLeveling.save();
                    }
                    case "evoStages.singleEvo"    -> {
                        cfg.levelingModule_evoStages_singleEvo.set(value);
                        cfg.levelingModule_evoStages_singleEvo.save();
                    }
                    case "evoStages.firstStageEvo"  -> {
                        cfg.levelingModule_evoStages_firstStageEvo.set(value);
                        cfg.levelingModule_evoStages_firstStageEvo.save();
                    }
                    case "evoStages.secondStageEvo" -> {
                        cfg.levelingModule_evoStages_secondStageEvo.set(value);
                        cfg.levelingModule_evoStages_secondStageEvo.save();
                    }
                    case "evoStages.finalStageEvo"  -> {
                        cfg.levelingModule_evoStages_finalStageEvo.set(value);
                        cfg.levelingModule_evoStages_finalStageEvo.save();
                    }
                    default -> {
                        source.sendFailure(Component.literal(
                                "Unknown leveling property '" + property + "'. Valid properties: " +
                                String.join(", ", LEVELING_PROPERTIES)
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                }
            }
            case "scaling" -> {
                switch (property) {
                    case "enableScaling"  -> {
                        cfg.scaling_enableScaling.set(Boolean.parseBoolean(value));
                        cfg.scaling_enableScaling.save();
                    }
                    case "scalingMethod"  -> {
                        cfg.scaling_scalingMethod.set(value);
                        cfg.scaling_scalingMethod.save();
                    }
                    default -> {
                        source.sendFailure(Component.literal(
                                "Unknown scaling property '" + property + "'. Valid properties: " +
                                String.join(", ", SCALING_PROPERTIES)
                        ).withStyle(ChatFormatting.RED));
                        return 0;
                    }
                }
            }
            default -> {
                source.sendFailure(Component.literal(
                        "Unknown module '" + module + "'. Valid modules: " +
                        String.join(", ", MODULE_PROPERTIES.keySet())
                ).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        source.sendSystemMessage(Component.literal(
                "[CLC] Set " + module + "." + property + " = " + value
        ).withStyle(ChatFormatting.GREEN));
        return 1;

    }
}
