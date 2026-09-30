package draylar.tiered.mixin;

import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Unique
    private boolean tiered$initialized = false;

    @Inject(method = "setStack", at = @At("HEAD"))
    private void onSetStack(ItemStack stack, CallbackInfo ci) {
        tiered$processStack(stack);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!tiered$initialized) {
            tiered$initialized = true;
            ItemEntity entity = (ItemEntity) (Object) this;
            if (!entity.getWorld().isClient()) {
                tiered$processStack(entity.getStack());
            }
        }
    }

    @Unique
    private void tiered$processStack(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            if (ModifierUtils.isItemTierable(stack.getItem()) && !ModifierUtils.hasTier(stack)) {
                Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
                if (potentialAttributeID != null) {
                    ModifierUtils.setTier(stack, potentialAttributeID);
                }
            }
        }
    }
}
