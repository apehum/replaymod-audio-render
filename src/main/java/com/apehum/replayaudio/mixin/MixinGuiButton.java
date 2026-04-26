package com.apehum.replayaudio.mixin;

import com.apehum.replayaudio.ReplayModAudioRender;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiButton;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractGuiButton.class)
public class MixinGuiButton {
    @Inject(method = "playClickSound(Lnet/minecraft/client/Minecraft;)V", at = @At("HEAD"), cancellable = true)
    private static void playClickSound(Minecraft mc, CallbackInfo ci) {
        if (ReplayModAudioRender.isRendering()) {
            ci.cancel();
        }
    }
}
