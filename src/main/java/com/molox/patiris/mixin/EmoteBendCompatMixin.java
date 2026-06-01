package com.molox.patiris.mixin;

import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import io.github.kosmx.bendylib.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.kosmx.playerAnim.impl.animation.AnimationApplier", remap = false)
public class EmoteBendCompatMixin {

    @Inject(
            method = "updatePart(Ljava/lang/String;Lnet/minecraft/client/model/geom/ModelPart;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void patiris$ensureBendMutator(String partName, ModelPart part, CallbackInfo ci) {
        if (partName == null || part == null || "head".equals(partName)) {
            return;
        }
        ModelPartAccessor.optionalGetCuboid(part, 0).ifPresent(cube -> {
            if (!cube.hasMutator("bend")) {
                IBendHelper.INSTANCE.initBend(part, patiris$directionFor(partName));
            }
        });
    }

    private static Direction patiris$directionFor(String partName) {
        // 参考 emotecraft PlayerModelMixin.initBendableStuff 的方向约定：
        // 四肢(sleeve/pants)用 UP，躯干(jacket)用 DOWN
        switch (partName) {
            case "torso":
                return Direction.DOWN;
            case "leftArm":
            case "rightArm":
            case "leftLeg":
            case "rightLeg":
            default:
                return Direction.UP;
        }
    }
}