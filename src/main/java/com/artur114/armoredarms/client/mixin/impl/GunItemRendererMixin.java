package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vicmatskiv.pointblank.client.controller.GlowAnimationController;
import com.vicmatskiv.pointblank.client.render.GunItemRenderer;
import com.vicmatskiv.pointblank.client.render.RenderApprover;
import com.vicmatskiv.pointblank.client.render.RenderPassGeoRenderer;
import com.vicmatskiv.pointblank.item.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;
import top.ribs.scguns.client.render.gun.animated.AnimatedGunRenderer;

import java.util.List;

@Mixin(value = GunItemRenderer.class, remap = false)
public abstract class GunItemRendererMixin {
    @Shadow(remap = false)
    private BakedGeoModel getLeftHandModel() {return null;}

    @Shadow(remap = false)
    private BakedGeoModel getRightHandModel() {return null;}

    @Shadow(remap = false)
    private void applyArmRefTransforms(PoseStack poseStack, GeoBone refBone, GeoBone leftArmBone) {}

    @Inject(method = "renderLeftArm", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/renderer/GeoItemRenderer;renderCubesOfBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), cancellable = true)
    private void renderLeftArm(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        PlayerRenderer renderer = AAUtils.playerRenderer(player);
        ModelPart arm = renderer.getModel().leftArm;
        float delta = -5.0F;
        poseStack.translate(-delta, 2, 0);
        renderer.renderLeftHand(poseStack, AAUtils.buffer(), packedLight, player);
        ci.cancel();
    }

    @Inject(method = "renderRightArm", at = @At("HEAD"))
    private void renderRightArm(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        BakedGeoModel handsBakedGeoModel = this.getRightHandModel();
        if (handsBakedGeoModel == null) {
            return;
        }
        GeoBone rightArmBone = handsBakedGeoModel.getBone("rightarm").orElse(null);
        if (rightArmBone != null) {
            poseStack.pushPose();
            Minecraft mc = Minecraft.getInstance();
            AbstractClientPlayer player = mc.player;
            if (player == null) return;
            PlayerRenderer renderer = AAUtils.playerRenderer(player);
            ModelPart arm = renderer.getModel().leftArm;
            this.applyArmRefTransforms(poseStack, bone, rightArmBone);
            PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
            IArmModelManager<?, ArmRenderLayerHand> manager = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class).modelManager;
            if (manager instanceof ArmModelManagerPlayer mp) mp.prepareModel(playerRenderer.getModel());
            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);
            RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            RenderUtils.scaleMatrixForBone(poseStack, bone);
            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
            poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
            poseStack.translate(-(arm.x / 16.0F), -(arm.y / 16.0F), -(arm.z / 16.0F));
            renderer.renderRightHand(poseStack, AAUtils.buffer(), packedLight, player);
            poseStack.popPose();
        }
    }
}
