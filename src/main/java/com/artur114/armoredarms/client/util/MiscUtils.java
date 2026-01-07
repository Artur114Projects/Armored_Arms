package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.client.core.ArmRenderLayerVanilla;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class MiscUtils {
    public static ModelRenderer playerArms() {
        ModelBiped player = ArmoredArms.RENDER_ARM_MANAGER.getLayer(ArmRenderLayerVanilla.class).renderPlayer.modelBipedMain;
        return player.bipedRightArm;
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
