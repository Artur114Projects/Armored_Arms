package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class AAUtils {
    public static ShapelessLocation fromMc(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        return ShapelessLocation.location(location.getResourceDomain(), location.getResourcePath());
    }

    public static EnumHandSideAA fromMc(EnumHandSide side) {
        return EnumHandSideAA.values()[side.ordinal()];
    }

    public static int handSideDelta(EnumHandSide handSide) {
        switch (handSide) {
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                return 0;
        }
    }
    public static ModelRenderer handFromModelBiped(ModelBiped mb, EnumHandSideAA handSide) {
        switch (handSide) {
            case RIGHT:
                return mb.bipedRightArm;
            case LEFT:
                return mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }

    public static ModelRenderer handFromModelBiped(ModelBiped mb, EnumHandSide handSide) {
        switch (handSide) {
            case RIGHT:
                return mb.bipedRightArm;
            case LEFT:
                return mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }

    public static ModelRenderer handFromModelPlayer(ModelPlayer mb, EnumHandSideAA handSide, boolean wear) {
        switch (handSide) {
            case RIGHT:
                return wear ? mb.bipedRightArmwear : mb.bipedRightArm;
            case LEFT:
                return wear ? mb.bipedLeftArmwear : mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }

    public static ModelRenderer handFromModelPlayer(ModelPlayer mb, EnumHandSide handSide, boolean wear) {
        switch (handSide) {
            case RIGHT:
                return wear ? mb.bipedRightArmwear : mb.bipedRightArm;
            case LEFT:
                return wear ? mb.bipedLeftArmwear : mb.bipedLeftArm;
            default:
                throw new NullPointerException();
        }
    }

    public static ModelRenderer[] playerArms() {
        if (Minecraft.getMinecraft().player == null) {
            throw new IllegalStateException("Unable to get playerArms before the player loads!");
        }
        ArmRenderLayerHand layer = null;

        try {
            layer = ArmoredArmsApi.currentPipeline().engine().layer(ArmRenderLayerHand.class);
        } catch (Exception ignored) {}

        if (layer != null) {
            return layer.actualPlayerHands();
        } else {
            ArmoredArms.LOGGER.AA_LOG.warn("ArmRenderLayerHand is null! Can't get safe playerArms!");
            ModelPlayer player = ((RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(Minecraft.getMinecraft().player)).getMainModel();
            return new ModelRenderer[] {player.bipedLeftArm, player.bipedRightArm};
        }
    }

    public static void setPlayerArmDataToArm(ModelRenderer arm, ModelRenderer playerArm) {
        arm.rotateAngleX = playerArm.rotateAngleX;
        arm.rotateAngleY = playerArm.rotateAngleY;
        arm.rotateAngleZ = playerArm.rotateAngleZ;
        arm.offsetX = playerArm.offsetX;
        arm.offsetY = playerArm.offsetY;
        arm.offsetZ = playerArm.offsetZ;
    }
}
