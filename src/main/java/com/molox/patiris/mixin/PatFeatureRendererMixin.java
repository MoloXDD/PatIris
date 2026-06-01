package com.molox.patiris.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.patpat.client.render.feature.PatFeatureRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(value = PatFeatureRenderer.class, remap = false)
public class PatFeatureRendererMixin {

    private static boolean irisChecked = false;
    private static Method shadowPassMethod = null;

    @Inject(
            method = "request",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipDuringShadowPass(ResourceLocation texture, PoseStack.Pose poseStack,
                                      float x1, float y1, float x2, float y2, float z,
                                      float u1, float v1, float u2, float v2, int light,
                                      CallbackInfo ci) {
        if (patiris$isShadowPass()) {
            ci.cancel();
        }
    }

    private static boolean patiris$isShadowPass() {
        if (!irisChecked) {
            irisChecked = true;
            try {
                Class<?> clazz = Class.forName("net.irisshaders.iris.shadows.ShadowRenderingState");
                shadowPassMethod = clazz.getMethod("areShadowsCurrentlyBeingRendered");
            } catch (Throwable t) {
                shadowPassMethod = null;
            }
        }
        if (shadowPassMethod == null) {
            return false;
        }
        try {
            return (boolean) shadowPassMethod.invoke(null);
        } catch (Throwable t) {
            return false;
        }
    }
}