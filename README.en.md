# Tiered (Fabric 1.21.4)
> **Language:** English · [Russian](README.md)

## Fork Description
All rights belong to the original developer — [Draylar](https://github.com/Draylar/tiered).
Fork and port to Minecraft **1.21.4** (Fabric / Java 21):
- Ported to Minecraft 1.21.4 Data Components system (`NbtComponent`, `AttributeModifiersComponent`, `EQUIPPABLE`).
- Updated networking system to `PayloadTypeRegistry` and `CustomPayload`.
- Replaced outdated `reach-entity-attributes` dependency with native vanilla 1.21.4 attributes (`player.block_interaction_range`, `player.entity_interaction_range`).
- Support for modern `c:*` and `minecraft:*` item tags.
- Full English and Russian localization.

## Performance Optimizations
- **Attribute Caching:** Valid tiers and attributes for every item are cached in a `ConcurrentHashMap`, eliminating repeated registry lookups, tag evaluations, and memory allocations on every modifier query.
- **Optimized Item Entities (`ItemEntity`):** Eliminated continuous per-tick checks (`tick()`) on all dropped items in the world. Tiers are assigned once upon item spawn/assignment (`setStack`), and non-tierable items (dirt, seeds, stone, etc.) are filtered instantly.
- **Fast NBT Component Lookups:** Eliminated unnecessary NBT cloning (`copyNbt()`) on items without tiers.
- **Zero-Allocation Equipment Slot Iterations:** Replaced `Arrays.asList` calls with direct array loops, avoiding garbage creation in the Java heap during combat and movement.

Support: Fabric only!
