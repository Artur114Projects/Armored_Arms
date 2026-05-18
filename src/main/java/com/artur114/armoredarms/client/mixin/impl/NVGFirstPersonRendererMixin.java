package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.tkg.ModernMayhem.client.item.NVGFirstPersonFakeItem;
import net.tkg.ModernMayhem.client.renderer.custom.NVGFirstPersonRenderer;
import net.tkg.ModernMayhem.server.util.AnimUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

@Mixin(value = NVGFirstPersonRenderer.class, remap = false)
public abstract class NVGFirstPersonRendererMixin extends GeoItemRenderer<NVGFirstPersonFakeItem> {
    @Shadow
    protected MultiBufferSource currentBuffer;

    public <I extends NVGFirstPersonFakeItem> NVGFirstPersonRendererMixin(I item) {
        super(item);
    }

    @Inject(method = "renderRecursively*", at = @At("HEAD"), cancellable = true)
    public void renderRecursively(PoseStack poseStack, NVGFirstPersonFakeItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        try {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player != null) {
                String boneName = bone.getName();
                boolean renderingArms = false;
                if (boneName.equals("left_arm") || boneName.equals("right_arm")) {
                    bone.setHidden(true);
                    renderingArms = true;
                }

                if (renderingArms) {
                    PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
                    IArmModelManager<?, ArmRenderLayerHand> manager = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class).modelManager;
                    if (manager instanceof ArmModelManagerPlayer mp) mp.prepareModel(playerRenderer.getModel());
                    poseStack.pushPose();
                    RenderUtils.translateMatrixToBone(poseStack, bone);
                    RenderUtils.translateToPivotPoint(poseStack, bone);
                    RenderUtils.rotateMatrixAroundBone(poseStack, bone);
                    RenderUtils.scaleMatrixForBone(poseStack, bone);
                    RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
                    poseStack.translate(0.0F, -0.8F, 0.0F);
                    if (boneName.equals("left_arm")) {
                        poseStack.translate(-0.0625F, 0.125F, 0.0F);
                        ModelPart arm = playerRenderer.getModel().leftArm;
                        poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
                        poseStack.translate(-(arm.x / 16.0F), -(arm.y / 16.0F), -(arm.z / 16.0F));
                        AAUtils.renderArmPlayerRenderer(EnumHandSideAA.LEFT, poseStack, this.currentBuffer, packedLight, player);
                    } else {
                        poseStack.translate(0.0625F, 0.125F, 0.0F);
                        ModelPart arm = playerRenderer.getModel().rightArm;
                        poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
                        poseStack.translate(-(arm.x / 16.0F), -(arm.y / 16.0F), -(arm.z / 16.0F));
                        AAUtils.renderArmPlayerRenderer(EnumHandSideAA.RIGHT, poseStack, this.currentBuffer, packedLight, player);
                    }

                    this.currentBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(this.animatable)));
                    poseStack.popPose();
                }
            }

            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        } catch (Exception e) {
            return;
        }

        ci.cancel();
    }
}
