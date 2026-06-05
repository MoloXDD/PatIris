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

    /**
     * HEAD: 如果源 part 有 bend mutator，确保外层 self 也拥有它。
     * 这一步保持原有逻辑不变，用于首次播放表情时给护甲初始化 mutator。
     */
    @Inject(method = "copyFrom", at = @At("HEAD"), require = 0)
    private void patiris$ensureOuterBendMutator(ModelPart source, CallbackInfo ci) {
        ModelPart self = (ModelPart) (Object) this;

        MutableCuboid srcCube = ModelPartAccessor.optionalGetCuboid(source, 0).orElse(null);
        if (srcCube == null || !srcCube.hasMutator("bend")) {
            return;
        }

        MutableCuboid selfCube = ModelPartAccessor.optionalGetCuboid(self, 0).orElse(null);
        if (selfCube == null || selfCube.hasMutator("bend")) {
            return;
        }

        // 外层与内层同肢体，sleeve/pants 用 UP 方向
        IBendHelper.INSTANCE.initBend(self, Direction.UP);
    }

    /**
     * TAIL: copyFrom 执行完毕后，把外层的 bend 状态同步为与源 part 完全一致。
     *
     * 这是修复护甲卡住的核心：
     *   - 表情播放中：source 的 bend 有值 → self 跟着弯曲（正确显示）
     *   - 表情结束后：emotecraft 调用 copyFrom 把原始姿势写回 source，
     *     source 的 bend 被清零 → 这里同步后 self 的 bend 也变为零 → 护甲恢复直立
     *   - 没穿护甲时：selfCube 为 null 或没有 mutator，直接跳过，无副作用
     */
    @Inject(method = "copyFrom", at = @At("TAIL"), require = 0)
    private void patiris$syncOuterBendState(ModelPart source, CallbackInfo ci) {
        ModelPart self = (ModelPart) (Object) this;

        // 外层必须已经有 bend mutator 才需要同步
        MutableCuboid selfCube = ModelPartAccessor.optionalGetCuboid(self, 0).orElse(null);
        if (selfCube == null || !selfCube.hasMutator("bend")) {
            return;
        }

        // 源 part 必须也有 bend mutator，否则无法同步（不应发生，但防御性检查）
        MutableCuboid srcCube = ModelPartAccessor.optionalGetCuboid(source, 0).orElse(null);
        if (srcCube == null || !srcCube.hasMutator("bend")) {
            // 源没有 bend，说明不是弯曲肢体的 copyFrom，停用外层的 bend 以防卡住
            selfCube.getAndActivateMutator(null);
            return;
        }

        // 把源的 bend 状态（包括弯曲量和激活/停用状态）完整复制给外层
        selfCube.copyStateFrom(srcCube);
    }
}