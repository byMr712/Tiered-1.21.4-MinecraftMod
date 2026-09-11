package draylar.tiered.network;

import draylar.tiered.Tiered;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record AttributeSyncPayload(Map<Identifier, String> attributes) implements CustomPayload {
    public static final CustomPayload.Id<AttributeSyncPayload> ID = new CustomPayload.Id<>(Tiered.id("attribute_sync"));

    public static final PacketCodec<PacketByteBuf, AttributeSyncPayload> CODEC = CustomPayload.codecOf(
            AttributeSyncPayload::write,
            AttributeSyncPayload::new
    );

    public AttributeSyncPayload(PacketByteBuf buf) {
        this(readMap(buf));
    }

    private static Map<Identifier, String> readMap(PacketByteBuf buf) {
        int size = buf.readVarInt();
        Map<Identifier, String> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(buf.readIdentifier(), buf.readString());
        }
        return map;
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(attributes.size());
        attributes.forEach((id, json) -> {
            buf.writeIdentifier(id);
            buf.writeString(json);
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
