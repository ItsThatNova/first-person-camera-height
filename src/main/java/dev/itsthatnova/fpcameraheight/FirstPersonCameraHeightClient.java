package dev.itsthatnova.fpcameraheight;

import dev.itsthatnova.fpcameraheight.config.FPCameraHeightConfig;
import dev.itsthatnova.fpcameraheight.network.OffsetSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class FirstPersonCameraHeightClient implements ClientModInitializer {

    // Ticks to wait after join before sending the packet (20 ticks = 1 second)
    private static int ticksUntilSync = -1;

    public static void log(String message) {
        if (FPCameraHeightConfig.isDebugLogging()) {
            System.out.println("[FPCH] " + message);
        }
    }

    @Override
    public void onInitializeClient() {
        FPCameraHeightConfig.load();

        // On join, schedule a sync after a short delay to ensure play phase is ready
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ticksUntilSync = 20;
            log("Joined world, scheduling sync in 20 ticks");
        });

        // Tick down and fire once ready
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ticksUntilSync > 0) {
                ticksUntilSync--;
            } else if (ticksUntilSync == 0) {
                ticksUntilSync = -1;
                log("Sending initial sync packet");
                sendSyncPacket();
            }
        });
    }

    public static void sendSyncPacket() {
        try {
            ClientPlayNetworking.send(new OffsetSyncPayload(
                FPCameraHeightConfig.isEnabled(),
                FPCameraHeightConfig.getOffset(),
                FPCameraHeightConfig.isDebugLogging()
            ));
            log("Sync packet sent | enabled=" + FPCameraHeightConfig.isEnabled()
                + " | offset=" + FPCameraHeightConfig.getOffset());
        } catch (Exception e) {
            // Silently ignore if not connected
        }
    }
}
