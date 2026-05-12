package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import top.ribs.scguns.client.render.gun.animated.AnimatedGunRenderer;
import top.ribs.scguns.item.animated.AnimatedGunItem;

@Mixin(value = AnimatedGunRenderer.class, remap = false)
public class AnimatedGunRendererMixin {
    @Shadow
    private void checkAndHandleAnimations(AnimationController<GeoAnimatable> animationController) {}
    @Shadow
    private void setupArmTransforms(PoseStack poseStack, GeoBone bone) {}
    @Shadow
    private boolean isRightArm(GeoBone bone) {return false;}
    @Shadow
    private MultiBufferSource bufferSource;

    @Inject(method = "renderPlayerArms", at = @At("HEAD"), cancellable = true)
    private void renderPlayerArms(Minecraft client, PoseStack poseStack, GeoBone bone, AnimatedGunItem animatable, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (client.player == null) {
            return;
        }

        try {
            this.setupArmTransforms(poseStack, bone);
            PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(client.player);
            long id = GeoItem.getId(client.player.getMainHandItem());
            AnimationController<GeoAnimatable> animationController = animatable.getAnimatableInstanceCache().getManagerForId(id).getAnimationControllers().get("controller");
            this.checkAndHandleAnimations(animationController);
            IArmModelManager<?, ArmRenderLayerHand> manager = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class).modelManager;
            if (manager instanceof ArmModelManagerPlayer mp) mp.prepareModel(playerRenderer.getModel());

            if (this.isRightArm(bone)) {
                poseStack.scale(0.66F, 0.78F, 0.66F);
                poseStack.translate(0.25F, -0.1, 0.1625);
                ModelPart arm = playerRenderer.getModel().rightArm;
                poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
                poseStack.translate(-(arm.x / 16.0F), -(arm.y / 16.0F), -(arm.z / 16.0F));
                playerRenderer.renderRightHand(poseStack, this.bufferSource, packedLight, client.player);
            } else {
                poseStack.scale(0.66F, 0.79F, 0.66F);
                poseStack.translate(-0.25F, -0.1, 0.1625);
                ModelPart arm = playerRenderer.getModel().leftArm;
                poseStack.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
                poseStack.translate(-(arm.x / 16.0F), -(arm.y / 16.0F), -(arm.z / 16.0F));
                playerRenderer.renderLeftHand(poseStack, this.bufferSource, packedLight, client.player);
            }
        } catch (Exception e) {
            return;
        }

        ci.cancel();
    }
}
