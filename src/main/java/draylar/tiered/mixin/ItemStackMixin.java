package draylar.tiered.mixin;

import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "applyAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At("TAIL")
    )
    private void applyTieredModifiers(EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        Identifier tier = ModifierUtils.getTier(self);

        if (tier != null) {
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if (potentialAttribute != null && potentialAttribute.getAttributes() != null) {
                potentialAttribute.getAttributes().forEach(template -> {
                    // required equipment slots
                    if (template.getRequiredEquipmentSlots() != null) {
                        if (Arrays.asList(template.getRequiredEquipmentSlots()).contains(slot)) {
                            template.realize(attributeModifierConsumer, slot);
                        }
                    }

                    // optional equipment slots
                    if (template.getOptionalEquipmentSlots() != null) {
                        if (Arrays.asList(template.getOptionalEquipmentSlots()).contains(slot) && Tiered.isPreferredEquipmentSlot(self, slot)) {
                            template.realize(attributeModifierConsumer, slot);
                        }
                    }
                });
            }
        }
    }
}
