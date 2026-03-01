package dev.itsthatnova.fpcameraheight.server;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOffsetStore {

    private static final ConcurrentHashMap<UUID, Float> offsets = new ConcurrentHashMap<>();
    private static volatile boolean serverDebugLogging = false;

    public static void setOffset(UUID playerId, boolean enabled, float offset, boolean debugLogging) {
        serverDebugLogging = debugLogging;
        if (enabled && offset != 0.0f) {
            offsets.put(playerId, offset);
        } else {
            offsets.remove(playerId);
        }
    }

    public static float getOffset(UUID playerId) {
        return offsets.getOrDefault(playerId, 0.0f);
    }

    public static boolean hasOffset(UUID playerId) {
        return offsets.containsKey(playerId);
    }

    public static void remove(UUID playerId) {
        offsets.remove(playerId);
    }

    public static boolean isDebugLogging() {
        return serverDebugLogging;
    }
}
