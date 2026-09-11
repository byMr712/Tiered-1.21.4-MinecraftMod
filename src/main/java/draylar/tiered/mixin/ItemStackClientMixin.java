package draylar.tiered.mixin;

import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

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
