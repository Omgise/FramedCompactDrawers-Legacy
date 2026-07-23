# FramedCompactDrawers-Legacy

A backport of **Framed Compact Drawers** (by eutros, originally for Minecraft 1.12.2) to **Minecraft 1.7.10**, providing custom-material framed variants of three Storage Drawers blocks.

## Features

Three framed blocks that inherit their **side, trim, and front materials** from any block you apply in the Framing Table:

- **Framed Compacting Drawer** — A 3-slot compacting drawer (gold ingot ↔ nugget ↔ block) with fully customizable material textures.
- **Framed Drawer Controller** — A drawer controller that forwards lock/shroud/quantify/personal-key interactions, with customizable materials.
- **Framed Controller Slave** — A slave block that proxies key interactions to its bound controller, with customizable materials.

All blocks preserve their material data as NBT on the dropped item, so breaking a framed block returns the exact item with materials intact.

## Dependencies

| Mod                                                                       | Type                | Version       |
|---------------------------------------------------------------------------|---------------------|---------------|
| [Storage Drawers (GTNH)](https://github.com/GTNewHorizons/StorageDrawers) | Required            | 2.2.26-GTNH+  |
| [UniMixins](https://github.com/LegacyModdingMC/UniMixins)                 | Required (embedded) | 0.8.5-GTNH+   |
| Minecraft Forge                                                           | Required            | 10.13.4.1614+ |

## Installation

1. Install Minecraft Forge 10.13.4.1614 for 1.7.10.
2. Place the mod jar in your `mods/` folder alongside Storage Drawers and UniMixins.
3. Launch the game.

## Building from Source

```bash
git clone https://github.com/MrFuzzihead/FramedCompactDrawers-Legacy.git
cd FramedCompactDrawers-Legacy
./gradlew build
```

The compiled jar will be at `build/libs/FramedCompactDrawers-Legacy-*.jar`.

## Development

This is a standard GTNH-based Forge 1.7.10 project. Import into IntelliJ IDEA:

```bash
./gradlew setupDecompWorkspace
./gradlew idea
```

Run the client:

```bash
./gradlew runClient
```

Run checkstyle/spotless formatting:

```bash
./gradlew spotlessApply check
```

## Credits

- **eutros** — Original Framed Compact Drawers for 1.12.2.
- **MrFuzzihead** — Backport to 1.7.10.
- **jaquadro** — Storage Drawers, the parent mod this extends.
- **GTNewHorizons** — Maintained 1.7.10 builds of Storage Drawers and build tooling.

## License

MIT License — see [LICENSE](LICENSE).

