package dev.itsthatnova.fpcameraheight;

import dev.itsthatnova.fpcameraheight.network.OffsetSyncPayload;
import dev.itsthatnova.fpcameraheight.server.PlayerOffsetStore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FirstPersonCameraHeightServer implements ModInitializer {

    public static final String MOD_ID = "fpcameraheight";

    public static void log(String message) {
        // Server-side logging — reads from PlayerOffsetStore's server debug flag
        if (PlayerOffsetStore.isDebugLogging()) {
            System.out.println("[FPCH] " + message);
        }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(OffsetSyncPayload.ID, OffsetSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OffsetSyncPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                PlayerOffsetStore.setOffset(
                    context.player().getUuid(),
                    payload.enabled(),
                    payload.offset(),
                    payload.debugLogging()
                );
                log("Received sync from " + context.player().getName().getString()
                    + " | enabled=" + payload.enabled()
                    + " | offset=" + payload.offset()
                    + " | debug=" + payload.debugLogging());
            });
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            log("Player disconnected, removing offset for " + handler.player.getName().getString());
            PlayerOffsetStore.remove(handler.player.getUuid());
        });
    }
}
