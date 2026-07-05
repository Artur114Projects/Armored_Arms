package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.ArmoredArms;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class AAUtils {
    public static EnumHandSideAA fromMc(HumanoidArm arm) {
        return EnumHandSideAA.values()[arm.getId()];
    }

    public static ShapelessLocation fromMc(ResourceLocation location) {
        if (location == null) {
            return ShapelessLocation.EMPTY;
        }
        return ShapelessLocation.location(location.getNamespace(), location.getPath());
    }

    public static MultiBufferSource buffer() {
        return Minecraft.getInstance().renderBuffers().bufferSource();
    }

    public static PlayerRenderer playerRenderer(AbstractClientPlayer player) {
        return (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
    }

    public static void renderArmPlayerRenderer(HumanoidArm side, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer) {
        renderArmPlayerRenderer(fromMc(side), pPoseStack, pBuffer, pCombinedLight, pPlayer);
    }

    public static void renderArmPlayerRenderer(EnumHandSideAA side, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer) {
        if (side == EnumHandSideAA.RIGHT) {
            playerRenderer(pPlayer).renderRightHand(pPoseStack, pBuffer, pCombinedLight, pPlayer);
        } else {
            playerRenderer(pPlayer).renderLeftHand(pPoseStack, pBuffer, pCombinedLight, pPlayer);
        }
    }
}
