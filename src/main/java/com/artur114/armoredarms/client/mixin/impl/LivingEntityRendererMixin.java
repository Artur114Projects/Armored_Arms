package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.client.mixin.MixinHandler;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.EnumMods;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntityRenderer.class}, priority = 1001)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(
        method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
            shift = At.Shift.AFTER
        )}
    )
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        try {
            if (EnumMods.PLAYER_ANIMATOR.isLoaded() && !pEntity.isSpectator() && pEntity instanceof LocalPlayer player) {
                MixinHandler.renderPlayerAnim(player, pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
            }
        } catch (Exception ignored) {}
    }
}
