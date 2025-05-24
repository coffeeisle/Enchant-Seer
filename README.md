# Enchantment Preview

A Minecraft Fabric mod that reveals all enchantments in enchantment table tooltips, removing the guesswork from enchanting!

## Features

- **Complete Enchantment Preview**: See exactly what enchantments you'll receive before spending your XP and lapis lazuli
- **Detailed Tooltips**: Hover over any enchantment option to see all enchantments that will be applied
- **Cost Information**: Shows the exact XP and lapis lazuli cost for each enchantment option
- **Client-Side Only**: Works on both single-player and multiplayer servers (no server-side installation required)

## Compatibility

- **Minecraft Versions**: 1.21.0 - 1.21.5
- **Mod Loader**: Fabric
- **Required Dependencies**: Fabric API

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the Enchantment Preview mod from the [releases page](https://github.com/coffeeisle/enchant-seer/releases)
4. Place the mod file in your `mods` folder
5. Launch Minecraft with the Fabric profile

## Usage

1. Set up an enchantment table with bookshelves as usual
2. Place an item to enchant and lapis lazuli in the enchantment table
3. Hover over any of the three enchantment options
4. A tooltip will appear showing:
   - All enchantments that will be applied
   - The level of each enchantment
   - The XP cost
   - The lapis lazuli requirement

No more gambling with enchantments - you'll know exactly what you're getting!

## Building from Source

1. Clone this repository
2. Run `./gradlew build` (or `gradlew.bat build` on Windows)
3. The built mod file will be in `build/libs/`

## Development Setup

1. Clone this repository
2. Run `./gradlew genSources` to generate Minecraft sources
3. Import the project into your IDE (IntelliJ IDEA recommended)
4. Run `./gradlew runClient` to test the mod

## Technical Details

This mod uses Fabric's client-side capabilities to:
- Hook into the enchantment screen rendering
- Simulate the enchantment generation process using the same seed and algorithms as vanilla Minecraft
- Display accurate previews without affecting gameplay balance
- Work seamlessly with other mods

The mod uses mixins to cleanly integrate with the vanilla enchantment screen without breaking compatibility.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## Changelog

### Version 1.0.0
- Initial release
- Complete enchantment preview functionality
- Support for Minecraft 1.21.0 - 1.21.5
- Client-side only implementation
