package draylar.tiered.api;

import com.google.gson.annotations.SerializedName;
import draylar.tiered.Tiered;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class AttributeTemplate {

    @SerializedName("type")
    private final String attributeTypeID;

    @SerializedName("modifier")
    private final RawModifier modifier;

    @SerializedName("required_equipment_slots")
    private final EquipmentSlot[] requiredEquipmentSlots;

    @SerializedName("optional_equipment_slots")
    private final EquipmentSlot[] optionalEquipmentSlots;

    public AttributeTemplate(String attributeTypeID, RawModifier modifier, EquipmentSlot[] requiredEquipmentSlots, EquipmentSlot[] optionalEquipmentSlots) {
        this.attributeTypeID = attributeTypeID;
        this.modifier = modifier;
        this.requiredEquipmentSlots = requiredEquipmentSlots;
        this.optionalEquipmentSlots = optionalEquipmentSlots;
    }

    public EquipmentSlot[] getRequiredEquipmentSlots() {
        return requiredEquipmentSlots;
    }

    public EquipmentSlot[] getOptionalEquipmentSlots() {
        return optionalEquipmentSlots;
    }

    public String getAttributeTypeID() {
        return attributeTypeID;
    }

    public RawModifier getModifier() {
        return modifier;
    }

    public void realize(BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer, EquipmentSlot slot) {
        RegistryEntry<EntityAttribute> key = resolveAttribute(attributeTypeID);
        if (key == null) {
            Tiered.LOGGER.warn("Unknown attribute type '{}' in Tiered data files.", attributeTypeID);
            return;
        }

        String rawName = modifier != null ? modifier.getName() : "tiered:modifier";
        Identifier modId = Tiered.id(rawName.replace("tiered:", "") + "_" + slot.asString());
        double amount = modifier != null ? modifier.getAmount() : 0.0;
        EntityAttributeModifier.Operation op = modifier != null ? modifier.getOperation() : EntityAttributeModifier.Operation.ADD_VALUE;

        EntityAttributeModifier entityModifier = new EntityAttributeModifier(
                modId,
                amount,
                op
        );
        consumer.accept(key, entityModifier);
    }

    public static RegistryEntry<EntityAttribute> resolveAttribute(String id) {
        if (id == null) return null;
        String cleanId = id.trim();
        if (cleanId.equals("reach-entity-attributes:reach") || cleanId.equals("player.block_interaction_range") || cleanId.equals("block_interaction_range") || cleanId.equals("generic.block_interaction_range") || cleanId.equals("minecraft:block_interaction_range") || cleanId.equals("minecraft:player.block_interaction_range")) {
            return net.minecraft.entity.attribute.EntityAttributes.BLOCK_INTERACTION_RANGE;
        }
        if (cleanId.equals("reach-entity-attributes:attack_range") || cleanId.equals("player.entity_interaction_range") || cleanId.equals("entity_interaction_range") || cleanId.equals("generic.entity_interaction_range") || cleanId.equals("minecraft:entity_interaction_range") || cleanId.equals("minecraft:player.entity_interaction_range")) {
            return net.minecraft.entity.attribute.EntityAttributes.ENTITY_INTERACTION_RANGE;
        }
        if (cleanId.equals("generic.dig_speed") || cleanId.equals("tiered:dig_speed") || cleanId.equals("tiered:generic.dig_speed") || cleanId.equals("dig_speed")) {
            return CustomEntityAttributes.DIG_SPEED;
        }
        if (cleanId.equals("generic.crit_chance") || cleanId.equals("tiered:crit_chance") || cleanId.equals("tiered:generic.crit_chance") || cleanId.equals("crit_chance")) {
            return CustomEntityAttributes.CRIT_CHANCE;
        }
        if (cleanId.startsWith("generic.")) {
            Identifier vanillaId = Identifier.of("minecraft", cleanId.substring("generic.".length()));
            var entry = Registries.ATTRIBUTE.getEntry(vanillaId);
            if (entry.isPresent()) return entry.get();
        }
        if (cleanId.startsWith("player.")) {
            Identifier vanillaId = Identifier.of("minecraft", cleanId.substring("player.".length()));
            var entry = Registries.ATTRIBUTE.getEntry(vanillaId);
            if (entry.isPresent()) return entry.get();
        }
        Identifier parsed = Identifier.tryParse(cleanId);
        if (parsed != null) {
            var entry = Registries.ATTRIBUTE.getEntry(parsed);
            if (entry.isPresent()) return entry.get();
        }
        return null;
    }

    public static class RawModifier {
        private String name;
        private double amount;
        private EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.ADD_VALUE;

        public RawModifier(String name, double amount, EntityAttributeModifier.Operation operation) {
            this.name = name;
            this.amount = amount;
            this.operation = operation;
        }

        public String getName() {
            return name != null ? name : "tiered:modifier";
        }

        public double getAmount() {
            return amount;
        }

        public EntityAttributeModifier.Operation getOperation() {
            return operation != null ? operation : EntityAttributeModifier.Operation.ADD_VALUE;
        }
    }
}
