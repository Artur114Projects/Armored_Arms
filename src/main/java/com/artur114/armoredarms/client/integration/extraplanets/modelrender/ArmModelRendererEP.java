package com.artur114.armoredarms.client.integration.extraplanets.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.mjr.extraplanets.client.model.ArmorCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

public class ArmModelRendererEP extends ArmModelRendererArmor {
    public ArmModelRendererEP(ArmorCustomModel mb, IMultiTexture texture) {
        super(mb, texture);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ArmorCustomModel model = (ArmorCustomModel) this.mb;
        ModelRenderer arm = this.arms[side.ordinal()];
        arm.rotationPointX = -5.0F * side.delta();
        arm.rotationPointY = 2.0F;
        arm.rotationPointZ = 0.0F;
        AAUtils.setPlayerArmDataToArm(arm, this.playerArms[side.ordinal()]);
        arm.rotateAngleX = 0.0F;
        model.isSneak = false;
        model.isChild = false;

        GL11.glPushMatrix();
        GL11.glTranslatef(arm.rotationPointX * (1.0F / 16.0F), arm.rotationPointY * (1.0F / 16.0F), arm.rotationPointZ * (1.0F / 16.0F));
        GL11.glRotatef(arm.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(arm.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(arm.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

        if (side == EnumHandSideAA.RIGHT) {
            model.partRightArm();
        } else {
            model.partLeftArm();
        }

        GL11.glPopMatrix();
    }
}
