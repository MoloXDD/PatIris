package com.molox.patiris.mixin;

import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.ModelPartAccessor;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class EmoteOuterLayerBendMixin {

    @Inject(method = "copyFrom", at = @At("HEAD"), require = 0)
    private void patiris$ensureOuterBendMutator(ModelPart source, CallbackInfo ci) {
        ModelPart self = (ModelPart) (Object) this;

        // 仅当源 part 的 cube0 有激活/可激活的 bend mutator 时，才需要让目标也具备
        MutableCuboid srcCube = ModelPartAccessor.optionalGetCuboid(source, 0).orElse(null);
        if (srcCube == null || !srcCube.hasMutator("bend")) {
            return;
        }

        MutableCuboid selfCube = ModelPartAccessor.optionalGetCuboid(self, 0).orElse(null);
        if (selfCube == null || selfCube.hasMutator("bend")) {
            return;
        }

        // 外层与源同肢体，弯曲方向取源 part 的方向。
        // 但我们拿不到源的注册方向，按 emotecraft 约定：sleeve/pants 用 UP。
        IBendHelper.INSTANCE.initBend(self, Direction.UP);
    }
}