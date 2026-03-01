package dev.itsthatnova.fpcameraheight.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OffsetSyncPayload(boolean enabled, float offset, boolean debugLogging) implements CustomPayload {

    public static final CustomPayload.Id<OffsetSyncPayload> ID = new CustomPayload.Id<>(
        Identifier.of("fpcameraheight", "offset_sync")
    );

    public static final PacketCodec<PacketByteBuf, OffsetSyncPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {
            buf.writeBoolean(payload.enabled());
            buf.writeFloat(payload.offset());
            buf.writeBoolean(payload.debugLogging());
        },
        buf -> new OffsetSyncPayload(buf.readBoolean(), buf.readFloat(), buf.readBoolean())
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
