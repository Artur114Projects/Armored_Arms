package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.client.mixin.RenderArmMixinEvent;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {PlayerRenderer.class}, priority = 2000)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context pContext, PlayerModel<AbstractClientPlayer> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void mixinRenderRightHand(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new RenderArmMixinEvent(pPoseStack, pBuffer,pCombinedLight, pPlayer, EnumHandSideAA.RIGHT))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void mixinRenderLeftHand(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new RenderArmMixinEvent(pPoseStack, pBuffer,pCombinedLight, pPlayer, EnumHandSideAA.LEFT))) {
            ci.cancel();
        }
    }

    @Inject(
            method = {"render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )}
    )
    private void hideBonesInFirstPerson(AbstractClientPlayer entity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        if (EnumMods.PLAYER_ANIMATOR.isLoaded() && FirstPersonMode.isFirstPersonPass()) {
            if (entity == Minecraft.getInstance().getCameraEntity()) {
                this.model.rightArm.visible = false;
                this.model.rightSleeve.visible = false;
                this.model.leftArm.visible = false;
                this.model.leftSleeve.visible = false;
            }
        }
    }
}
