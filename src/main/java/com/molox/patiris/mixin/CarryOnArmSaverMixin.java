package com.molox.patiris.mixin;

import com.molox.patiris.CarryOnArmState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidModel.class, priority = 2000)
public class CarryOnArmSaverMixin {

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void patiris$saveCarryArmState(LivingEntity entity, float limbSwing,
                                           float limbSwingAmount, float ageInTicks,
                                           float netHeadYaw, float headPitch,
                                           CallbackInfo ci) {
        if (!(entity instanceof Player player)) {
            CarryOnArmState.saved = false;
            return;
        }

        boolean carrying;
        try {
            carrying = tschipp.carryon.common.carry.CarryOnDataManager.getCarryData(player).isCarrying();
        } catch (Throwable t) {
            CarryOnArmState.saved = false;
            return;
        }

        if (!carrying) {
            CarryOnArmState.saved = false;
            return;
        }

        HumanoidModel<?> self = (HumanoidModel<?>) (Object) this;
        CarryOnArmState.rightXRot = self.rightArm.xRot;
        CarryOnArmState.rightYRot = self.rightArm.yRot;
        CarryOnArmState.rightZRot = self.rightArm.zRot;
        CarryOnArmState.rightX = self.rightArm.x;
        CarryOnArmState.rightY = self.rightArm.y;
        CarryOnArmState.rightZ = self.rightArm.z;
        CarryOnArmState.leftXRot = self.leftArm.xRot;
        CarryOnArmState.leftYRot = self.leftArm.yRot;
        CarryOnArmState.leftZRot = self.leftArm.zRot;
        CarryOnArmState.leftX = self.leftArm.x;
        CarryOnArmState.leftY = self.leftArm.y;
        CarryOnArmState.leftZ = self.leftArm.z;
        CarryOnArmState.saved = true;
    }
}