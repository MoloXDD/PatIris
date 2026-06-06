package com.molox.patiris.mixin;

import com.molox.patiris.PatIris;
import com.molox.patiris.RealCameraRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(targets = "com.xtracr.realcamera.RealCameraCore", remap = false)
public class RealCameraHeadRotationMixin {

    private static Method patiris$isPlayingEmoteMethod = null;
    private static boolean patiris$emoteLookupDone = false;

    @Inject(method = "computeCamera", at = @At("HEAD"), require = 0)
    private static void patiris$computeBegin(Minecraft client, float partialTick,
                                             CallbackInfo ci) {
        if (!patiris$isPlayerEmoting(client)) return;
        RealCameraRenderState.isInCameraEntityRender = true;
    }

    @Inject(method = "computeCamera", at = @At("RETURN"), require = 0)
    private static void patiris$computeEnd(Minecraft client, float partialTick,
                                           CallbackInfo ci) {
        RealCameraRenderState.isInCameraEntityRender = false;
    }

    @Inject(method = "renderCameraEntity", at = @At("HEAD"), require = 0)
    private static void patiris$renderBegin(Minecraft client, float partialTick,
                                            MultiBufferSource bufferSource, Matrix4f modelView,
                                            CallbackInfo ci) {
        if (!patiris$isPlayerEmoting(client)) return;
        RealCameraRenderState.isInCameraEntityRender = true;
    }

    @Inject(method = "renderCameraEntity", at = @At("RETURN"), require = 0)
    private static void patiris$renderEnd(Minecraft client, float partialTick,
                                          MultiBufferSource bufferSource, Matrix4f modelView,
                                          CallbackInfo ci) {
        RealCameraRenderState.isInCameraEntityRender = false;
    }

    private static boolean patiris$isPlayerEmoting(Minecraft client) {
        if (client.player == null) return false;
        if (!patiris$emoteLookupDone) {
            patiris$emoteLookupDone = true;
            try {
                Class<?> clazz = Class.forName("io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity");
                patiris$isPlayingEmoteMethod = clazz.getMethod("isPlayingEmote");
            } catch (Throwable t) {
                patiris$isPlayingEmoteMethod = null;
            }
        }
        if (patiris$isPlayingEmoteMethod == null) return false;
        try {
            return (boolean) patiris$isPlayingEmoteMethod.invoke(client.player);
        } catch (Throwable t) {
            return false;
        }
    }
}