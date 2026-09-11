package draylar.tiered.mixin;

import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import draylar.tiered.api.PotentialAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow
    public abstract void applyAttributeModifier(AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer);

    @Unique
    private boolean isTiered;
    @Unique
    private boolean slotInfo;
    @Unique
    private final Map<RegistryEntry<EntityAttribute>, List<EntityAttributeModifier>> tieredMap = new HashMap<>();

    /**
     * Pre-scan: collect all modifiers (base + tiered) for each attribute into tieredMap.
     * Only runs for tiered items. Stops at the first slot that contains modifiers
     * to avoid pulling in any secondary slot modifiers.
     */
    @Inject(method = "appendAttributeModifiersTooltip", at = @At("HEAD"))
    private void appendAttributeModifiersTooltipMixin(Consumer<Text> textConsumer, @Nullable PlayerEntity player, CallbackInfo info) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier tier = ModifierUtils.getTier(itemStack);
        if (tier != null) {
            this.isTiered = true;
            this.tieredMap.clear();

            for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values()) {
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                this.applyAttributeModifier(attributeModifierSlot, (attribute, modifier) -> {
                    if (modifier.value() > 0.0001D || modifier.value() < -0.0001D) {
                        List<EntityAttributeModifier> modifiers = this.tieredMap.computeIfAbsent(attribute, k -> new ArrayList<>());
                        modifiers.add(modifier);
                        mutableBoolean.setValue(true);
                    }
                });
                if (mutableBoolean.getValue()) {
                    break;
                }
            }
        } else {
            this.isTiered = false;
        }
        this.slotInfo = true;
    }

    /**
     * Suppresses duplicate modifier sections (e.g. "When Worn:" after "When on Legs:").
     * Once the primary slot header has been accepted, slotInfo becomes false,
     * so any subsequent slot iteration is immediately cancelled.
     */
    @Inject(method = "appendAttributeModifiersTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;applyAttributeModifier(Lnet/minecraft/component/type/AttributeModifierSlot;Ljava/util/function/BiConsumer;)V"), cancellable = true)
    private void appendAttributeModifiersTooltipTwoMixin(Consumer<Text> textConsumer, @Nullable PlayerEntity player, CallbackInfo info) {
        if (this.isTiered && !this.slotInfo) {
            info.cancel();
        }
    }

    /**
     * When the first slot header is output in method_57370, mark slotInfo = false
     * so that no further slot sections will be processed.
     */
    @Inject(method = "method_57370", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    private void method_57370Mixin(MutableBoolean mutableBoolean, Consumer<Text> consumer, AttributeModifierSlot attributeModifierSlot, PlayerEntity playerEntity, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo info) {
        if (this.isTiered) {
            this.slotInfo = false;
        }
    }

    /**
     * Replaces the entire appendAttributeModifierTooltip method for tiered items.
     * This avoids CAPTURE_FAILSOFT issues — in 1.21.4 the StackMapTable marks 'bl'
     * (slot 7) as 'top' after the if(bl) branch, causing LocalCapture to silently
     * fail for ordinals 1 and 2. By injecting at HEAD and cancelling, we bypass
     * the entire vanilla method and compute the tooltip ourselves.
     */
    @Inject(
            method = "appendAttributeModifierTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void appendAttributeModifierTooltipMixin(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo info) {
        if (!this.isTiered || !this.tieredMap.containsKey(attribute)) {
            return;
        }

        List<EntityAttributeModifier> list = this.tieredMap.get(attribute);

        // Only the first modifier for this attribute renders the combined line.
        // All subsequent modifiers are suppressed (their values are folded into the first line).
        if (!list.isEmpty() && !list.get(0).idMatches(modifier.id())) {
            info.cancel();
            return;
        }

        // --- Reproduce vanilla appendAttributeModifierTooltip logic ---
        double d = modifier.value();
        boolean bl = false;

        if (player != null) {
            if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
                bl = true;
            } else if (modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED);
                bl = true;
            }
        }

        double e;
        if (modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                || modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
            e = d * 100.0;
        } else if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE)) {
            e = d * 10.0;
        } else {
            e = d;
        }

        // --- Build tiered tooltip text ---
        MutableText text;
        Formatting baseColor;

        if (bl) {
            // "equals" format — absolute value with space prefix (attack damage / attack speed)
            String translationKey = "tiered.attribute.modifier.equals." + modifier.operation().getId();
            text = ScreenTexts.space();
            text.append(Text.translatable(translationKey,
                            AttributeModifiersComponent.DECIMAL_FORMAT.format(e))
                    .formatted(Formatting.DARK_GREEN));
            baseColor = Formatting.DARK_GREEN;
        } else if (d > 0) {
            // "plus" format — positive modifier
            String translationKey = "tiered.attribute.modifier.plus." + modifier.operation().getId();
            text = Text.translatable(translationKey,
                            AttributeModifiersComponent.DECIMAL_FORMAT.format(e))
                    .formatted(attribute.value().getFormatting(true));
            baseColor = attribute.value().getFormatting(true);
        } else {
            // "take" format — negative modifier
            String translationKey = "tiered.attribute.modifier.take." + modifier.operation().getId();
            text = Text.translatable(translationKey,
                            AttributeModifiersComponent.DECIMAL_FORMAT.format(-e))
                    .formatted(attribute.value().getFormatting(false));
            baseColor = attribute.value().getFormatting(false);
        }

        // Append tiered bonus values in parentheses
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).idMatches(modifier.id())) {
                continue; // skip the base modifier itself
            }
            EntityAttributeModifier tieredModifier = list.get(i);
            double tieredValue;
            if (tieredModifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    || tieredModifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                tieredValue = tieredModifier.value() * 100.0;
            } else if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE)) {
                tieredValue = tieredModifier.value() * 10.0;
            } else {
                tieredValue = tieredModifier.value();
            }
            boolean addition = tieredValue > 0;
            String bonusStr = "(" + (addition ? "+" : "")
                    + AttributeModifiersComponent.DECIMAL_FORMAT.format(tieredValue)
                    + (tieredModifier.operation().getId() > 0 ? "%" : "")
                    + ")";
            text.append(ScreenTexts.space());
            text.append(Text.translatable("tiered.attribute.modifier", bonusStr)
                    .formatted(addition ? Formatting.BLUE : Formatting.RED));
        }

        // Append attribute name
        text.append(ScreenTexts.space());
        text.append(Text.translatable(attribute.value().getTranslationKey()).formatted(baseColor));

        textConsumer.accept(text);
        info.cancel();
    }

    @Inject(
            method = "getName",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyName(CallbackInfoReturnable<Text> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (!self.contains(DataComponentTypes.CUSTOM_NAME)) {
            Identifier tier = ModifierUtils.getTier(self);
            if (tier != null) {
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
                if (potentialAttribute != null) {
                    Text prefix = Text.translatable(potentialAttribute.getID() + ".label").setStyle(potentialAttribute.getStyle());
                    Text name = prefix.copy().append(" ").append(cir.getReturnValue()).setStyle(potentialAttribute.getStyle());
                    cir.setReturnValue(name);
                }
            }
        }
    }
}
