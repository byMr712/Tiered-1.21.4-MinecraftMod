package draylar.tiered;

import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.data.AttributeDataLoader;
import draylar.tiered.network.AttributeSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TieredClient implements ClientModInitializer {

    public static final Map<Identifier, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        registerAttributeSyncHandler();
    }

    public static void registerAttributeSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(AttributeSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                CACHED_ATTRIBUTES.putAll(Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes());
                Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().clear();

                payload.attributes().forEach((id, json) -> {
                    try {
                        PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(json, PotentialAttribute.class);
                        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().put(id, pa);
                    } catch (Exception e) {
                        Tiered.LOGGER.error("Failed to parse synced tiered attribute: " + id, e);
                    }
                });
            });
        });
    }
}
