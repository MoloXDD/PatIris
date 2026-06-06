package com.molox.patiris.mixin;

import com.molox.patiris.RealCameraRenderState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelHeadRotationMixin {

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void patiris$restoreHeadRotForRealCamera(LivingEntity entity, float limbSwing,
                                                     float limbSwingAmount, float ageInTicks,
                                                     float netHeadYaw, float headPitch,
                                                     CallbackInfo ci) {
        if (!RealCameraRenderState.isInCameraEntityRender) return;
        PlayerModel<?> self = (PlayerModel<?>) (Object) this;
        self.head.xRot = headPitch * ((float) Math.PI / 180f);
        self.head.yRot = netHeadYaw * ((float) Math.PI / 180f);
        self.head.zRot = 0f;
    }
}