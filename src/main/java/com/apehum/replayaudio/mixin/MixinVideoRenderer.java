package com.apehum.replayaudio.mixin;

import com.apehum.replayaudio.AudioRenderSettings;
import com.apehum.replayaudio.ReplayModAudioRender;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.replaymod.render.rendering.VideoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//?if >=1.19.3 {
import net.minecraft.client.OptionInstance;
//?} else {
/*import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;
*///?}

@Mixin(VideoRenderer.class)
public class MixinVideoRenderer {
    //?if >=1.19.3 {
    @WrapOperation(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;set(Ljava/lang/Object;)V")
    )
    private void setSoundVolume(OptionInstance<?> instance, Object object, Operation<Void> original) {
        if (AudioRenderSettings.get().enabled) return;
        original.call(instance, object);
    }
    //?} else {
    /*@WrapOperation(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setSoundCategoryVolume(Lnet/minecraft/sounds/SoundSource;F)V")
    )
    private void setSoundVolume(Options instance, SoundSource soundSource, float v, Operation<Void> original) {
        if (AudioRenderSettings.get().enabled) return;
        original.call(instance, soundSource, v);
    }*///?}

    @Inject(
            method = "finish",
            at = @At(value = "HEAD")
    )
    private void onFinish(CallbackInfo ci) {
        ReplayModAudioRender.stopRender();
    }
}
