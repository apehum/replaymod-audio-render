package com.apehum.replayaudio;

import com.apehum.replayaudio.mixin.MixinSoundManagerAccessor;
import com.replaymod.render.rendering.VideoRenderer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ReplayModAudioRender implements ModInitializer {
	public static final String MOD_ID = "replaymodaudiorender";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
	}

	private static AudioRender AUDIO_RENDER;

	public static boolean isRendering() {
		return AUDIO_RENDER != null;
	}

	public static void submit() {
		if (AUDIO_RENDER == null) return;
		AUDIO_RENDER.submit();
	}

	public static void startRender(@NotNull VideoRenderer renderer) {
		if (AUDIO_RENDER != null) return;
		if (!AudioRenderSettings.get().enabled) return;

		AUDIO_RENDER = new AudioRender(renderer);
		Minecraft.getInstance().executeBlocking(ReplayModAudioRender::reloadDevice);
	}

	public static void stopRender() {
		if (AUDIO_RENDER == null) return;

		AUDIO_RENDER.flush();
		AUDIO_RENDER = null;

		Minecraft.getInstance().executeBlocking(ReplayModAudioRender::reloadDevice);
	}

	private static void reloadDevice() {
		((MixinSoundManagerAccessor) Minecraft.getInstance().getSoundManager())
				.getSoundEngine()
				.reload();
	}
}
