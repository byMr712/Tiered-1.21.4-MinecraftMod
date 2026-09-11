package draylar.tiered.api;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
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
                RegistryEntry<Item> entry = item.getRegistryEntry();
                if (entry != null && entry.isIn(tagKey)) {
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
        RegistryEntry<Item> entry = item.getRegistryEntry();
        String name = tag;
        if (name.contains(":")) {
            name = name.substring(name.indexOf(':') + 1);
        }

        EquippableComponent equippable = item.getDefaultStack().get(DataComponentTypes.EQUIPPABLE);
        String path = Registries.ITEM.getId(item).getPath();

        switch (name) {
            case "swords":
                return (entry != null && entry.isIn(ItemTags.SWORDS))
                        || item instanceof SwordItem
                        || path.endsWith("_sword");
            case "pickaxes":
                return (entry != null && entry.isIn(ItemTags.PICKAXES))
                        || item instanceof PickaxeItem
                        || path.endsWith("_pickaxe");
            case "axes":
                return (entry != null && entry.isIn(ItemTags.AXES))
                        || item instanceof AxeItem
                        || path.endsWith("_axe");
            case "shovels":
                return (entry != null && entry.isIn(ItemTags.SHOVELS))
                        || item instanceof ShovelItem
                        || path.endsWith("_shovel");
            case "hoes":
                return (entry != null && entry.isIn(ItemTags.HOES))
                        || item instanceof HoeItem
                        || path.endsWith("_hoe");
            case "helmets":
            case "head_armor":
                return (entry != null && entry.isIn(ItemTags.HEAD_ARMOR))
                        || (equippable != null && equippable.slot() == EquipmentSlot.HEAD)
                        || path.endsWith("_helmet") || path.endsWith("_cap");
            case "chestplates":
            case "chest_armor":
                return (entry != null && entry.isIn(ItemTags.CHEST_ARMOR))
                        || (equippable != null && equippable.slot() == EquipmentSlot.CHEST)
                        || path.endsWith("_chestplate") || path.endsWith("_tunic");
            case "leggings":
            case "leg_armor":
                return (entry != null && entry.isIn(ItemTags.LEG_ARMOR))
                        || (equippable != null && equippable.slot() == EquipmentSlot.LEGS)
                        || path.endsWith("_leggings") || path.endsWith("_pants");
            case "boots":
            case "foot_armor":
                return (entry != null && entry.isIn(ItemTags.FOOT_ARMOR))
                        || (equippable != null && equippable.slot() == EquipmentSlot.FEET)
                        || path.endsWith("_boots");
            case "shields":
                return item instanceof ShieldItem
                        || (equippable != null && equippable.slot() == EquipmentSlot.OFFHAND)
                        || path.contains("shield");
            case "fishing_rods":
                return item instanceof FishingRodItem
                        || path.contains("fishing_rod");
            default:
                return false;
        }
    }
}
