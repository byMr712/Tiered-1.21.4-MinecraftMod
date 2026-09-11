package draylar.tiered.api;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TieredItemTags {

    public static final TagKey<Item> HELMETS = registerC("helmets");
    public static final TagKey<Item> CHESTPLATES = registerC("chestplates");
    public static final TagKey<Item> LEGGINGS = registerC("leggings");
    public static final TagKey<Item> BOOTS = registerC("boots");
    public static final TagKey<Item> SHIELDS = registerC("shields");
    public static final TagKey<Item> SWORDS = registerC("swords");
    public static final TagKey<Item> AXES = registerC("axes");
    public static final TagKey<Item> PICKAXES = registerC("pickaxes");
    public static final TagKey<Item> SHOVELS = registerC("shovels");
    public static final TagKey<Item> HOES = registerC("hoes");

    private TieredItemTags() { }

    public static void init() {
    }

    private static TagKey<Item> registerC(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", id));
    }
}
