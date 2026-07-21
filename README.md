# Cobbled Level Control

<div>
  <img src="https://mods.matthiesen.dev/badges/matthiesenLibAPI.svg" alt="Matthiesen Lib API">
  <img src="https://mods.matthiesen.dev/badges/cobblemon.svg" alt="Cobblemon">
</div>

This server-side mod gives the server-owner full control over level caps on pokemons using permission nodes for absolute control. Perfect for a tailored player experience!

Are you a server owner looking to customize the level caps of pokemon on your server? Look no further! With this mod, you can easily set level caps for different pokemon species, 
set catch rules, and even control the level of wild pokemon spawns. All of this is done through permission nodes, giving you complete control over the player experience on your server.

## Features

- Difficulty system:
  - Battle Control: Ability to restrict the level of pokemon in battles, ensuring fair and balanced gameplay.
  - Catch Control: Control over Evolution stages, types of pokemon (e.g., Legendary, Mythical, etc.), shiny status, and level caps for catching pokemon.
  - Level Control: Set level caps for wild pokemon spawns, allowing you to create a unique and challenging experience for your players.
- Permission nodes for absolute control: Use permission nodes to set level caps for different pokemon species, catch rules, and wild pokemon spawns.
- Server-side only: This mod is designed to be used on the server-side, ensuring that all players have a consistent experience regardless of their client setup.
- Easy to use: The mod is easy to install and configure, making it accessible to server owners of all skill levels.

### Currently Supported Cobblemon Events

- `BattleStarEvent` - Triggered when a battle starts.
- `CandyUseEvent` - Triggered when a player uses candy on a Pokemon.
- `CatchEvent` - Triggered when a player catches a Pokemon.
- `ExperienceGainedEvent` - Triggered when a Pokemon gains experience.
- `LevelUpEvent` - Triggered when a Pokemon levels up.
- `PokemonSpawnEvent` - Triggered when a Pokemon spawns in the world.
- `EvolutionEvent` - Triggered when a Pokemon evolves.

## Requirements
- [Matthiesen Lib API](https://modrinth.com/mod/matthiesen-lib-api)
- [Cobblemon](https://modrinth.com/mod/cobblemon)

## Docs

Documentation for this mod can be found at [mods.matthiesen.dev](https://mods.matthiesen.dev/cobbled-level-control/)

## Version Compatibility

| Minecraft Version | Mod Version |
|-------------------|-------------|
| 1.21.1            | 1.x.x       |

## FastStats Metrics

This library uses [FastStats](https://faststats.dev) to collect anonymous usage statistics. This helps the developer understand
how the library and the mods built using the library are being used and improve it over time. You can learn more about the data
collected and how it is used by visiting [FastStats: Information](https://faststats.dev/info).

You can also view the data collected by this library on the [FastStats: Cobbled Level Control](https://faststats.dev/project/cobbled-level-control) page.

To opt out of this data collection, set the `enabled` property to `false` in the `<game_directory>/config/matthiesen_lib_api/metrics.properties` file.

## License

MIT - see `LICENSE`.
