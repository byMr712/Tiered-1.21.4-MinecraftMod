package draylar.tiered.api;

import draylar.tiered.Tiered;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModifierUtils {

    private static final Random RANDOM = new Random();

    public static Identifier getRandomAttributeIDFor(Item item) {
        if (item == null) return null;
        List<Identifier> potentialAttributes = new ArrayList<>();

        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if (attribute.isValid(item)) {
                potentialAttributes.add(id);
            }
        });

        if (!potentialAttributes.isEmpty()) {
            return potentialAttributes.get(RANDOM.nextInt(potentialAttributes.size()));
        } else {
            return null;
        }
    }

    public static Identifier getTier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            NbtCompound nbt = customData.copyNbt();
            if (nbt.contains(Tiered.NBT_SUBTAG_KEY, NbtElement.COMPOUND_TYPE)) {
                NbtCompound sub = nbt.getCompound(Tiered.NBT_SUBTAG_KEY);
                if (sub.contains(Tiered.NBT_SUBTAG_DATA_KEY, NbtElement.STRING_TYPE)) {
                    return Identifier.tryParse(sub.getString(Tiered.NBT_SUBTAG_DATA_KEY));
                }
            }
        }
        return null;
    }

    public static void setTier(ItemStack stack, Identifier tierId) {
        if (stack == null || stack.isEmpty() || tierId == null) return;
        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = customData.copyNbt();
        NbtCompound sub = nbt.getCompound(Tiered.NBT_SUBTAG_KEY);
        sub.putString(Tiered.NBT_SUBTAG_DATA_KEY, tierId.toString());
        nbt.put(Tiered.NBT_SUBTAG_KEY, sub);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static boolean hasTier(ItemStack stack) {
        return getTier(stack) != null;
    }

    private ModifierUtils() {
        // no-op
    }
}