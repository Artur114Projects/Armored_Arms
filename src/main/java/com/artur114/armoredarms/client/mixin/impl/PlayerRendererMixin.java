package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.client.mixin.RenderArmMixinEvent;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerRenderer.class})
public class PlayerRendererMixin { // Now I define reality myself

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
}
