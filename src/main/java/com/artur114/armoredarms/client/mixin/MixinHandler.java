package com.artur114.armoredarms.client.mixin;

import com.artur114.armoredarms.client.util.AAUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

public class MixinHandler {
    public static void renderPlayerAnim(LocalPlayer player, LivingEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (FirstPersonMode.isFirstPersonPass()) {
            if (pEntity instanceof IAnimatedPlayer animPlayer) {
                AnimationApplier animationApplier = animPlayer.playerAnimator_getAnimation();
                FirstPersonConfiguration config = animationApplier.getFirstPersonConfiguration();
                PlayerRenderer renderer = AAUtils.playerRenderer(player);

                if (config.isShowRightArm()) {
                    renderer.renderRightHand(pPoseStack, pBuffer, pPackedLight, player);
                }
                if (config.isShowLeftArm()) {
                    renderer.renderLeftHand(pPoseStack, pBuffer, pPackedLight, player);
                }
            }
        }
    }

    public static void cancelPlayerAnimRender(AbstractClientPlayer entity, PlayerModel<?> thisModel) {
        if (FirstPersonMode.isFirstPersonPass()) {
            if (entity == Minecraft.getInstance().getCameraEntity()) {
                thisModel.rightArm.visible = false;
                thisModel.rightSleeve.visible = false;
                thisModel.leftArm.visible = false;
                thisModel.leftSleeve.visible = false;
            }
        }
    }
}
