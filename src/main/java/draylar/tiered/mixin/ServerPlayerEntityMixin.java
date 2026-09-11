package draylar.tiered.mixin;

import com.mojang.authlib.GameProfile;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        boolean changed = false;

        // Check main inventory
        for (ItemStack stack : this.getInventory().main) {
            if (checkAndApplyTier(stack)) {
                changed = true;
            }
        }

        // Check armor inventory
        for (ItemStack stack : this.getInventory().armor) {
            if (checkAndApplyTier(stack)) {
                changed = true;
            }
        }

        // Check offhand inventory
        for (ItemStack stack : this.getInventory().offHand) {
            if (checkAndApplyTier(stack)) {
                changed = true;
            }
        }

        // Check cursor stack
        ScreenHandler current = this.currentScreenHandler;
        if (current != null) {
            ItemStack cursor = current.getCursorStack();
            if (checkAndApplyTier(cursor)) {
                changed = true;
            }
        }

        if (changed) {
            this.playerScreenHandler.sendContentUpdates();
            if (this.currentScreenHandler != null) {
                this.currentScreenHandler.sendContentUpdates();
            }
        }
    }

    @Unique
    private boolean checkAndApplyTier(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && !ModifierUtils.hasTier(stack)) {
            Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
            if (potentialAttributeID != null) {
                ModifierUtils.setTier(stack, potentialAttributeID);
                return true;
            }
        }
        return false;
    }
}
