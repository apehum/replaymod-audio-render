package com.apehum.replayaudio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioRenderSettings {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("replaymodaudiorender.json");

    private static AudioRenderSettings INSTANCE;

    public boolean enabled = true;
    public AudioCodec codec = AudioCodec.AAC;
    public boolean stereo = true;
    public transient File outputFile = null;

    public static AudioRenderSettings get() {
        if (INSTANCE == null) INSTANCE = load();
        return INSTANCE;
    }

    private static AudioRenderSettings load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                AudioRenderSettings loaded = GSON.fromJson(
                        Files.readString(CONFIG_PATH),
                        AudioRenderSettings.class
                );
                if (loaded != null) {
                    if (loaded.codec == null) loaded.codec = AudioCodec.AAC;
                    return loaded;
                }
            }
        } catch (IOException e) {
            ReplayModAudioRender.LOGGER.warn("Failed to load audio render settings", e);
        }
        return new AudioRenderSettings();
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            ReplayModAudioRender.LOGGER.warn("Failed to save audio render settings", e);
        }
    }
}
