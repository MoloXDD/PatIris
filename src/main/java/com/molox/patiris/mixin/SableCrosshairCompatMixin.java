package com.molox.patiris.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(targets = "com.xtracr.realcamera.EventHandler", remap = false)
public class SableCrosshairCompatMixin {

    private static boolean patiris$sableInitDone = false;
    private static Object patiris$sableHelper = null;
    private static Method patiris$projectOutMethod = null;
    private static Field patiris$hitResultLocationField = null;

    @WrapOperation(
            method = "onRenderLevelStage",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/xtracr/realcamera/util/CrosshairUtil;update(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/phys/Vec3;[Lorg/joml/Matrix4fc;)V"
            ),
            require = 0
    )
    private static void patiris$fixSableCrosshair(
            Minecraft client, Vec3 cameraPos, Matrix4fc[] projectionMatrices,
            Operation<Void> original) {

        patiris$ensureSableReflection();

        HitResult hitResult = client.hitResult;
        if (hitResult == null
                || patiris$sableHelper == null
                || patiris$projectOutMethod == null
                || patiris$hitResultLocationField == null) {
            original.call(client, cameraPos, projectionMatrices);
            return;
        }

        Vec3 originalLocation = hitResult.getLocation();
        Vec3 projected = patiris$projectOut(client, originalLocation);

        if (projected == null || projected.equals(originalLocation)) {
            original.call(client, cameraPos, projectionMatrices);
            return;
        }

        try {
            patiris$hitResultLocationField.set(hitResult, projected);
            original.call(client, cameraPos, projectionMatrices);
        } catch (Throwable t) {
            original.call(client, cameraPos, projectionMatrices);
        } finally {
            try {
                patiris$hitResultLocationField.set(hitResult, originalLocation);
            } catch (Throwable ignored) {}
        }
    }

    private static Vec3 patiris$projectOut(Minecraft client, Vec3 location) {
        try {
            Object result = patiris$projectOutMethod.invoke(
                    patiris$sableHelper, client.level, location);
            if (result instanceof Vec3 vec) return vec;
        } catch (Throwable ignored) {}
        return null;
    }

    private static void patiris$ensureSableReflection() {
        if (patiris$sableInitDone) return;
        patiris$sableInitDone = true;
        try {
            Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
            Field helperField = sableClass.getDeclaredField("HELPER");
            helperField.setAccessible(true);
            patiris$sableHelper = helperField.get(null);
            if (patiris$sableHelper == null) return;

            patiris$projectOutMethod = patiris$sableHelper.getClass().getMethod(
                    "projectOutOfSubLevel",
                    net.minecraft.world.level.Level.class,
                    net.minecraft.core.Position.class);

            Field locField = HitResult.class.getDeclaredField("location");
            locField.setAccessible(true);
            patiris$hitResultLocationField = locField;
        } catch (Throwable t) {
            patiris$sableHelper = null;
            patiris$projectOutMethod = null;
            patiris$hitResultLocationField = null;
        }
    }
}