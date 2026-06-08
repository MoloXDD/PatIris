package com.molox.patiris.mixin;

import com.misterd.agritechtwo.blockentity.custom.PlanterBlockEntity;
import com.misterd.agritechtwo.client.ber.PlanterBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlanterBlockEntityRenderer.class)
public class PlanterShadowMixin {

    @Inject(method = "render(Lcom/misterd/agritechtwo/blockentity/custom/PlanterBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void patiris$skipShadowRender(PlanterBlockEntity blockEntity, float partialTick,
                                          PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, CallbackInfo ci) {
        boolean isShadow = ShadowRenderingState.areShadowsCurrentlyBeingRendered();
        LoggerFactory.getLogger("PatIris").info("[PlanterShadow] called, isShadow={}", isShadow);
        if (isShadow) {
            ci.cancel();
        }
    }
}