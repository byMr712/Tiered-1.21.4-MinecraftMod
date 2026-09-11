package draylar.tiered.api;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemVerifier {

    private final String id;
    private final String tag;

    public ItemVerifier(String id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public boolean isValid(Identifier itemID) {
        if (itemID == null) return false;
        Item item = Registries.ITEM.get(itemID);
        return isValid(item);
    }

    public boolean isValid(Item item) {
        if (item == null) return false;
        Identifier itemID = Registries.ITEM.getId(item);

        if (id != null) {
            return itemID.toString().equals(id) || itemID.getPath().equals(id);
        } else if (tag != null) {
            TagKey<Item> tagKey = resolveTagKey(tag);
            if (tagKey != null) {
                RegistryEntry<Item> entry = Registries.ITEM.getEntry(item);
                if (entry.isIn(tagKey)) {
                    return true;
                }
            }
            return checkLegacyTagFallbacks(tag, item);
        }

        return false;
    }

    private static TagKey<Item> resolveTagKey(String tagString) {
        Identifier tagId = Identifier.tryParse(tagString);
        if (tagId == null) return null;
        return TagKey.of(RegistryKeys.ITEM, tagId);
    }

    private static boolean checkLegacyTagFallbacks(String tag, Item item) {
        RegistryEntry<Item> entry = Registries.ITEM.getEntry(item);
        String name = tag;
        if (name.contains(":")) {
            name = name.substring(name.indexOf(':') + 1);
        }

        switch (name) {
            case "swords":
                return entry.isIn(ItemTags.SWORDS) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "swords")));
            case "pickaxes":
                return entry.isIn(ItemTags.PICKAXES) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "pickaxes")));
            case "axes":
                return entry.isIn(ItemTags.AXES) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "axes")));
            case "shovels":
                return entry.isIn(ItemTags.SHOVELS) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "shovels")));
            case "hoes":
                return entry.isIn(ItemTags.HOES) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "hoes")));
            case "helmets":
            case "head_armor":
                return entry.isIn(ItemTags.HEAD_ARMOR) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "helmets")));
            case "chestplates":
            case "chest_armor":
                return entry.isIn(ItemTags.CHEST_ARMOR) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "chestplates")));
            case "leggings":
            case "leg_armor":
                return entry.isIn(ItemTags.LEG_ARMOR) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "leggings")));
            case "boots":
            case "foot_armor":
                return entry.isIn(ItemTags.FOOT_ARMOR) || entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "boots")));
            case "shields":
                return entry.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "shields")));
            default:
                return false;
        }
    }
}
