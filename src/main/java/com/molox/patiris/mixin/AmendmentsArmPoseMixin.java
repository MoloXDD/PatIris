package com.molox.patiris.mixin;

import com.molox.patiris.CarryOnArmState;
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

        boolean carrying = patiris$isCarrying(player);

        switch (partName) {
            case "right_arm" -> {
                if (carrying) {
                    if (CarryOnArmState.saved) {
                        model.rightArm.xRot = CarryOnArmState.rightXRot;
                        model.rightArm.yRot = CarryOnArmState.rightYRot;
                        model.rightArm.zRot = CarryOnArmState.rightZRot;
                        model.rightArm.x = CarryOnArmState.rightX;
                        model.rightArm.y = CarryOnArmState.rightY;
                        model.rightArm.z = CarryOnArmState.rightZ;
                    }
                    return;
                }
                ItemStack stack = patiris$getArmStack(player, true);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                patiris$poseArm(player, stack, player.getMainArm(), model, true);
            }
            case "right_sleeve" -> {
                if (!(model instanceof PlayerModel<?> pm)) return;
                if (carrying) {
                    float xRot = CarryOnArmState.saved ? CarryOnArmState.rightXRot : model.rightArm.xRot;
                    float yRot = CarryOnArmState.saved ? CarryOnArmState.rightYRot : model.rightArm.yRot;
                    float zRot = CarryOnArmState.saved ? CarryOnArmState.rightZRot : model.rightArm.zRot;
                    float x = CarryOnArmState.saved ? CarryOnArmState.rightX : model.rightArm.x;
                    float y = CarryOnArmState.saved ? CarryOnArmState.rightY : model.rightArm.y;
                    float z = CarryOnArmState.saved ? CarryOnArmState.rightZ : model.rightArm.z;
                    pm.rightSleeve.xRot = xRot;
                    pm.rightSleeve.yRot = yRot;
                    pm.rightSleeve.zRot = zRot;
                    pm.rightSleeve.x = x;
                    pm.rightSleeve.y = y;
                    pm.rightSleeve.z = z;
                    return;
                }
                ItemStack stack = patiris$getArmStack(player, true);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                pm.rightSleeve.xRot = model.rightArm.xRot;
                pm.rightSleeve.yRot = model.rightArm.yRot;
                pm.rightSleeve.zRot = model.rightArm.zRot;
                pm.rightSleeve.x = model.rightArm.x;
                pm.rightSleeve.y = model.rightArm.y;
                pm.rightSleeve.z = model.rightArm.z;
            }
            case "left_arm" -> {
                if (carrying) {
                    if (CarryOnArmState.saved) {
                        model.leftArm.xRot = CarryOnArmState.leftXRot;
                        model.leftArm.yRot = CarryOnArmState.leftYRot;
                        model.leftArm.zRot = CarryOnArmState.leftZRot;
                        model.leftArm.x = CarryOnArmState.leftX;
                        model.leftArm.y = CarryOnArmState.leftY;
                        model.leftArm.z = CarryOnArmState.leftZ;
                    }
                    return;
                }
                ItemStack stack = patiris$getArmStack(player, false);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                HumanoidArm offArm = player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
                patiris$poseArm(player, stack, offArm, model, false);
            }
            case "left_sleeve" -> {
                if (!(model instanceof PlayerModel<?> pm)) return;
                if (carrying) {
                    float xRot = CarryOnArmState.saved ? CarryOnArmState.leftXRot : model.leftArm.xRot;
                    float yRot = CarryOnArmState.saved ? CarryOnArmState.leftYRot : model.leftArm.yRot;
                    float zRot = CarryOnArmState.saved ? CarryOnArmState.leftZRot : model.leftArm.zRot;
                    float x = CarryOnArmState.saved ? CarryOnArmState.leftX : model.leftArm.x;
                    float y = CarryOnArmState.saved ? CarryOnArmState.leftY : model.leftArm.y;
                    float z = CarryOnArmState.saved ? CarryOnArmState.leftZ : model.leftArm.z;
                    pm.leftSleeve.xRot = xRot;
                    pm.leftSleeve.yRot = yRot;
                    pm.leftSleeve.zRot = zRot;
                    pm.leftSleeve.x = x;
                    pm.leftSleeve.y = y;
                    pm.leftSleeve.z = z;
                    return;
                }
                ItemStack stack = patiris$getArmStack(player, false);
                if (stack.isEmpty()) return;
                if (IThirdPersonAnimationProvider.get(stack.getItem()) == null) return;
                pm.leftSleeve.xRot = model.leftArm.xRot;
                pm.leftSleeve.yRot = model.leftArm.yRot;
                pm.leftSleeve.zRot = model.leftArm.zRot;
                pm.leftSleeve.x = model.leftArm.x;
                pm.leftSleeve.y = model.leftArm.y;
                pm.leftSleeve.z = model.leftArm.z;
            }
        }
    }

    private static boolean patiris$isCarrying(Player player) {
        try {
            return tschipp.carryon.common.carry.CarryOnDataManager.getCarryData(player).isCarrying();
        } catch (Throwable t) {
            return false;
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