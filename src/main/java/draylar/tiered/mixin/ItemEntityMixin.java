package draylar.tiered.mixin;

import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (!entity.getWorld().isClient()) {
            ItemStack stack = entity.getStack();
            if (stack != null && !stack.isEmpty() && !ModifierUtils.hasTier(stack)) {
                Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
                if (potentialAttributeID != null) {
                    ModifierUtils.setTier(stack, potentialAttributeID);
                }
            }
        }
    }
}
