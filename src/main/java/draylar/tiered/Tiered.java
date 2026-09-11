package draylar.tiered;

import draylar.tiered.api.CustomEntityAttributes;
import draylar.tiered.api.TieredItemTags;
import draylar.tiered.data.AttributeDataLoader;
import draylar.tiered.network.AttributeSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Tiered implements ModInitializer {

    public static final AttributeDataLoader ATTRIBUTE_DATA_LOADER = new AttributeDataLoader();
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String NBT_SUBTAG_KEY = "Tiered";
    public static final String NBT_SUBTAG_DATA_KEY = "Tier";

    @Override
    public void onInitialize() {
        TieredItemTags.init();
        CustomEntityAttributes.init();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ATTRIBUTE_DATA_LOADER);

        PayloadTypeRegistry.playS2C().register(AttributeSyncPayload.ID, AttributeSyncPayload.CODEC);
        registerAttributeSyncer();
    }

    public static Identifier id(String path) {
        return Identifier.of("tiered", path);
    }

    public static boolean isPreferredEquipmentSlot(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return false;
        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable != null) {
            return equippable.slot().equals(slot);
        }
        return slot == EquipmentSlot.MAINHAND;
    }

    public static void registerAttributeSyncer() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Map<Identifier, String> map = new HashMap<>();
            ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
                map.put(id, AttributeDataLoader.GSON.toJson(attribute));
            });
            ServerPlayNetworking.send(handler.player, new AttributeSyncPayload(map));
        });
    }
}
