package com.molox.patiris.mixin;

import com.molox.patiris.PatIris;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonAnimationProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartWithState", remap = false)
public class AmendmentsArmPoseMixin {

    private static long patiris$lastLogTime = 0;
    private static Field patiris$nameField = null;
    private static boolean patiris$nameFieldSearched = false;

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
            at = @At(value = "INVOKE",
                    target = "Ltraben/entity_model_features/models/parts/EMFModelPart$Animator;run()V"),
            require = 0
    )
    private void patiris$beforeDAAnimation(
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            com.mojang.blaze3d.vertex.VertexConsumer buffer,
            int packedLight, int packedOverlay, int color,
            CallbackInfo ci) {

        String partName = patiris$getName();
        if (partName == null) return;

        LivingEntity entity = patiris$getCurrentEntity();
        if (!(entity instanceof Player player)) return;

        HumanoidModel<?> model = patiris$getModel(player);
        if (model == null) return;

        switch (partName) {
            case "right_arm" -> {
                ItemStack stack = patiris$getArmStack(player, true);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                long now = System.currentTimeMillis();
                float before = model.rightArm.xRot;
                patiris$poseArm(player, stack, player.getMainArm(), model, true);
                if (now - patiris$lastLogTime >= 1000) {
                    patiris$lastLogTime = now;
                    PatIris.LOGGER.info("[BeforeDA] right_arm before={} after={}",
                            String.format("%.4f", before),
                            String.format("%.4f", model.rightArm.xRot));
                }
            }
            case "right_sleeve" -> {
                ItemStack stack = patiris$getArmStack(player, true);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                if (!(model instanceof PlayerModel<?> playerModel)) return;
                playerModel.rightSleeve.xRot = model.rightArm.xRot;
                playerModel.rightSleeve.yRot = model.rightArm.yRot;
                playerModel.rightSleeve.zRot = model.rightArm.zRot;
                playerModel.rightSleeve.x = model.rightArm.x;
                playerModel.rightSleeve.y = model.rightArm.y;
                playerModel.rightSleeve.z = model.rightArm.z;
            }
            case "left_arm" -> {
                ItemStack stack = patiris$getArmStack(player, false);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                HumanoidArm offArm = player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
                patiris$poseArm(player, stack, offArm, model, false);
            }
            case "left_sleeve" -> {
                ItemStack stack = patiris$getArmStack(player, false);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                if (!(model instanceof PlayerModel<?> playerModel)) return;
                playerModel.leftSleeve.xRot = model.leftArm.xRot;
                playerModel.leftSleeve.yRot = model.leftArm.yRot;
                playerModel.leftSleeve.zRot = model.leftArm.zRot;
                playerModel.leftSleeve.x = model.leftArm.x;
                playerModel.leftSleeve.y = model.leftArm.y;
                playerModel.leftSleeve.z = model.leftArm.z;
            }
        }
    }

    private static ItemStack patiris$getArmStack(Player player, boolean isRightArm) {
        boolean mainIsRight = player.getMainArm() == HumanoidArm.RIGHT;
        if (isRightArm == mainIsRight) return player.getMainHandItem();
        return player.getOffhandItem();
    }

    private String patiris$getName() {
        if (!patiris$nameFieldSearched) {
            patiris$nameFieldSearched = true;
            for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getName().equals("name") && f.getType() == String.class) {
                        f.setAccessible(true);
                        patiris$nameField = f;
                        break;
                    }
                }
                if (patiris$nameField != null) break;
            }
        }
        if (patiris$nameField == null) return null;
        try {
            return (String) patiris$nameField.get(this);
        } catch (Throwable t) {
            return null;
        }
    }

    private static LivingEntity patiris$getCurrentEntity() {
        try {
            var state = traben.entity_model_features.models.animation.EMFAnimationEntityContext.getEmfState();
            if (state == null) return null;
            Object entity = state.emfEntity();
            if (entity instanceof LivingEntity le) return le;
        } catch (Throwable t) {}
        return null;
    }

    private static HumanoidModel<?> patiris$getModel(Player player) {
        try {
            var mc = net.minecraft.client.Minecraft.getInstance();
            var renderer = mc.getEntityRenderDispatcher().getRenderer(player);
            if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer<?,?> lr) {
                if (lr.getModel() instanceof HumanoidModel<?> hm) return hm;
            }
        } catch (Throwable t) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> void patiris$poseArm(
            T entity, ItemStack stack, HumanoidArm arm,
            HumanoidModel<?> model, boolean isRight) {
        IThirdPersonAnimationProvider provider = IThirdPersonAnimationProvider.get(stack.getItem());
        if (provider == null) return;
        HumanoidModel<T> typedModel = (HumanoidModel<T>) model;
        if (isRight) provider.poseRightArm(stack, typedModel, entity, arm);
        else provider.poseLeftArm(stack, typedModel, entity, arm);
    }
}