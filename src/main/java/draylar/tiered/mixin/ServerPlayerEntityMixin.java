package draylar.tiered.mixin;

import com.mojang.authlib.GameProfile;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private DefaultedList<ItemStack> mainCopy = null;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        DefaultedList<ItemStack> main = this.getInventory().main;
        if (mainCopy == null) {
            mainCopy = copyDefaultedList(main);
            runCheck(main);
        } else if (!main.equals(mainCopy)) {
            mainCopy = copyDefaultedList(main);
            runCheck(main);
        }
    }

    @Unique
    private DefaultedList<ItemStack> copyDefaultedList(DefaultedList<ItemStack> list) {
        DefaultedList<ItemStack> newList = DefaultedList.ofSize(list.size(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            newList.set(i, list.get(i).copy());
        }
        return newList;
    }

    @Unique
    private void runCheck(DefaultedList<ItemStack> list) {
        list.forEach(itemStack -> {
            if (!itemStack.isEmpty() && !ModifierUtils.hasTier(itemStack)) {
                Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(itemStack.getItem());
                if (potentialAttributeID != null) {
                    ModifierUtils.setTier(itemStack, potentialAttributeID);
                }
            }
        });
    }
}
