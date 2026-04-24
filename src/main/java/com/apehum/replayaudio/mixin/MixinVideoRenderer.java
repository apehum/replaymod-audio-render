package com.apehum.replayaudio.mixin;

import com.apehum.replayaudio.AudioRenderSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.replaymod.render.rendering.VideoRenderer;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VideoRenderer.class)
public class MixinVideoRenderer {
    @WrapOperation(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;set(Ljava/lang/Object;)V")
    )
    private void setSoundVolume(OptionInstance<?> instance, Object object, Operation<Void> original) {
        if (AudioRenderSettings.get().enabled) return;
        original.call(instance, object);
    }
}
