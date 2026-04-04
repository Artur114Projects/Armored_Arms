package com.artur114.armoredarms.aalegacy.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class MiscUtils {
    public static ModelRenderer[] playerArms() {
        ModelBiped player = ((RenderPlayer) RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer)).modelBipedMain;
        return new ModelRenderer[] {player.bipedRightArm, player.bipedLeftArm};
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
