package draylar.tiered.mixin;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "createPlayerAttributes",
            at = @At("RETURN")
    )
    private static void initAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> ci) {
        ci.getReturnValue().add(CustomEntityAttributes.CRIT_CHANCE);
        ci.getReturnValue().add(CustomEntityAttributes.DIG_SPEED);
    }

    @Inject(
            method = "getBlockBreakingSpeed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        EntityAttributeInstance instance = this.getAttributeInstance(CustomEntityAttributes.DIG_SPEED);

        if (instance != null && instance.getValue() > 0.0) {
            float f = cir.getReturnValueF();
            cir.setReturnValue((float) (f + instance.getValue()));
        }
    }
}