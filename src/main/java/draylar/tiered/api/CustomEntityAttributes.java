package draylar.tiered.api;

import draylar.tiered.Tiered;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class CustomEntityAttributes {

    public static final RegistryEntry<EntityAttribute> DIG_SPEED = register("dig_speed",
            new ClampedEntityAttribute("attribute.name.tiered.dig_speed", 0.0D, 0.0D, 2048.0D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> CRIT_CHANCE = register("crit_chance",
            new ClampedEntityAttribute("attribute.name.tiered.crit_chance", 0.0D, 0.0D, 1.0D).setTracked(true));

    public static void init() {
        // NO-OP
    }

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Tiered.id(id), attribute);
    }
}
