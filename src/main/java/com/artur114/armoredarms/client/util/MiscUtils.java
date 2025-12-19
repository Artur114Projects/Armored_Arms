package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.client.core.ArmRenderLayerVanilla;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;

public class MiscUtils {
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
        ModelPlayer player = ArmoredArms.RENDER_ARM_MANAGER.getLayer(ArmRenderLayerVanilla.class).renderPlayer.getMainModel();
        return new ModelRenderer[] {player.bipedLeftArm, player.bipedRightArm};
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
