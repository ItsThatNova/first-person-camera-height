package dev.itsthatnova.fpcameraheight.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FPCameraHeightConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("firstpersoncameraheight.json");

    private static FPCameraHeightConfig instance = new FPCameraHeightConfig();

    public boolean enabled = true;
    public float offset = 0.0f;
    public boolean debugLogging = false;

    public static final float MIN_OFFSET = -0.5f;
    public static final float MAX_OFFSET = 1.0f;
    public static final float DEFAULT_OFFSET = 0.0f;

    public static FPCameraHeightConfig getInstance() {
        return instance;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                FPCameraHeightConfig loaded = GSON.fromJson(reader, FPCameraHeightConfig.class);
                if (loaded != null) {
                    loaded.offset = Math.max(MIN_OFFSET, Math.min(MAX_OFFSET, loaded.offset));
                    instance = loaded;
                }
            } catch (IOException e) {
                System.err.println("[FirstPersonCameraHeight] Failed to load config: " + e.getMessage());
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            System.err.println("[FirstPersonCameraHeight] Failed to save config: " + e.getMessage());
        }
    }

    public static boolean isEnabled() {
        return instance.enabled;
    }

    public static float getOffset() {
        return instance.offset;
    }

    public static boolean isDebugLogging() {
        return instance.debugLogging;
    }

    public static void setEnabled(boolean enabled) {
        instance.enabled = enabled;
        save();
        dev.itsthatnova.fpcameraheight.FirstPersonCameraHeightClient.sendSyncPacket();
    }

    public static void setOffset(float offset) {
        instance.offset = Math.max(MIN_OFFSET, Math.min(MAX_OFFSET, offset));
        save();
        dev.itsthatnova.fpcameraheight.FirstPersonCameraHeightClient.sendSyncPacket();
    }

    public static void setDebugLogging(boolean debugLogging) {
        instance.debugLogging = debugLogging;
        save();
    }
}
