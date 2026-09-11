package draylar.tiered.mixin;

import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onCraftTake(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (stack != null && !stack.isEmpty() && !ModifierUtils.hasTier(stack)) {
            Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
            if (potentialAttributeID != null) {
                ModifierUtils.setTier(stack, potentialAttributeID);
            }
        }
    }
}
