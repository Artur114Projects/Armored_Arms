package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vicmatskiv.pointblank.client.render.GunItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.util.RenderUtils;

@Mixin(value = GunItemRenderer.class, remap = false)
public abstract class GunItemRendererMixin {
    @Shadow(remap = false)
    private BakedGeoModel getLeftHandModel() {return null;}

    @Shadow(remap = false)
    private BakedGeoModel getRightHandModel() {return null;}

    @Shadow(remap = false)
    private void applyArmRefTransforms(PoseStack poseStack, GeoBone refBone, GeoBone leftArmBone) {}

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    private void aa$renderLeftArm(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        try {
            this.armoredarms$renderArm(poseStack, bone, packedLight, EnumHandSideAA.LEFT);
        } catch (Exception e) {
            return;
        }
        ci.cancel();
    }

    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    private void aa$renderRightArm(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        try {
            this.armoredarms$renderArm(poseStack, bone, packedLight, EnumHandSideAA.RIGHT);
        } catch (Exception e) {
            return;
        }
        ci.cancel();
    }

    @Unique
    private void armoredarms$renderArm(PoseStack poseStack, GeoBone refBone, int packedLight, EnumHandSideAA side) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) {
            return;
        }
        BakedGeoModel handModel = side.sided(this.getRightHandModel(), this.getLeftHandModel());
        if (handModel == null) {
            return;
        }
        GeoBone armBone = handModel.getBone(side.sided("rightarm", "leftarm")).orElse(null);
        if (armBone == null || armBone.getCubes().isEmpty()) {
            return;
        }

        GeoCube cube = armBone.getCubes().get(0);
        PlayerRenderer renderer = AAUtils.playerRenderer(player);
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        IArmModelManager<?, ArmRenderLayerHand> mgr = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class).modelManager;
        if (mgr instanceof ArmModelManagerPlayer mp) mp.prepareModel(model);
        ModelPart arm = side.sided(model.rightArm, model.leftArm);

        // Dark magic values
        float cgx = side.sided(0.83124995F, -0.85625005F);
        float cgy = 0.83124995F;
        float cgz = -0.037499994F;

        float cvx = ((side.sided(-3.0F, -1.0F) + side.sided(1.0F, 3.0F)) * 0.5F) / 16.0F;
        float cvy = ((-2.0F + 10.0F) * 0.5F) / 16.0F;
        float cvz = ((2.0F + 2.0F) * 0.5F) / 16.0F;

        poseStack.pushPose();
        this.applyArmRefTransforms(poseStack, refBone, armBone);
        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
        poseStack.translate(cgx, cgy, cgz);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-cvx, -cvy, -cvz);
        poseStack.mulPose(new Quaternionf().rotationZYX(arm.zRot, arm.yRot, arm.xRot).invert());
        poseStack.translate(-arm.x / 16.0F, -arm.y / 16.0F, -arm.z / 16.0F);
        AAUtils.renderArmPlayerRenderer(side, poseStack, AAUtils.buffer(), packedLight, player);
        poseStack.popPose();
    }
}
