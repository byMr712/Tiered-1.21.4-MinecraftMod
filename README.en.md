> **Language:** [Русский](README.md) · English

# Tiered (Minecraft 1.21.4 Fabric Port)

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue.svg)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue.svg)
![ModMenu](https://img.shields.io/badge/ModMenu-Supported-blue.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Port, optimization, and update of the **Tiered** mod for **Minecraft 1.21.4 (Fabric)**.

Original Developer: [Draylar/tiered](https://github.com/Draylar/tiered).

---

## About

**Tiered** adds RPG-style quality tiers (prefixes and modifiers) to armor, tools, and weapons in Minecraft. Crafted or looted equipment is imbued with unique attributes and special perks.

---

## Features

- **Equipment Tier System**: Armor, tools, and weapons receive randomized prefixes (from common to legendary) affecting stats (damage, attack speed, armor, mining speed, health, reach).
- **Reforging**: Reroll equipment modifiers at anvils or reforging stations for higher-tier attributes.
- **Convention & Tag Support**: Compatible with `c:*` and vanilla `minecraft:*` item tags.

---

## Changes in 1.21.4 Port (byMr712)

- **Ported to Minecraft 1.21.4 & Data Components**:
  - Full migration from legacy NBT to 1.21.4 Data Components (`NbtComponent`, `AttributeModifiersComponent`, `EQUIPPABLE`).
  - Network synchronization updated via `PayloadTypeRegistry` and `CustomPayload`.
  - Replaced external `reach-entity-attributes` dependency with native 1.21.4 reach attributes (`player.block_interaction_range`, `player.entity_interaction_range`).
- **Deep Performance Optimizations**:
  - **Attribute Caching**: Valid attributes and tiers are cached in `ConcurrentHashMap`, eliminating redundant registry scans and memory allocations per lookup.
  - **`ItemEntity` Optimization**: Removed per-tick checks (`tick()`) on dropped world items; tier roll occurs once upon stack creation, and non-equippable items are skipped immediately.
  - **Fast Component Checks**: Eliminated unnecessary NBT compound cloning (`copyNbt()`).
  - **Zero-Allocation Slot Iterations**: Replaced `Arrays.asList` calls with direct array iterations over equipment slots.
- **Full Localization**:
  - Added Russian (`ru_ru.json`) and English (`en_us.json`) translations.

---

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/byMr712/Tiered-1.21.4-MinecraftMod/releases).
2. Requires:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
3. Place the `.jar` file into your `mods` folder.
4. Launch the game.

---

## Building

1. Requires Java 21 and Fabric Loader for Minecraft 1.21.4.
2. To build the project, run:
   ```bash
   ./gradlew build
   ```
3. The built jar file will be located at `build/libs/Tiered-1.21.4-byMr712.jar`.

---

## Credits & License

- Original Author: [Draylar](https://github.com/Draylar).
- Ported and optimized for 1.21.4 by: [Mr712](https://github.com/byMr712).
- Distributed under the [MIT License](LICENSE).
