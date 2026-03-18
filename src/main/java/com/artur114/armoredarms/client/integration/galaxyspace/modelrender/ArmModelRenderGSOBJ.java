package com.artur114.armoredarms.client.integration.galaxyspace.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ArmModelRenderGSOBJ implements IArmModelRenderer<ArmModelManagerArmor> {
    public final ModelRenderer[] playerArms = AAUtils.playerArms();
    public final IMultiTexture texture;
    public final float[] color;
    public final int[] arms;

    public ArmModelRenderGSOBJ(ItemSpaceSuitModel model, IMultiTexture texture) {
        this.arms = new int[] {ItemSpaceSuitModel.leftArmList, ItemSpaceSuitModel.rightArmList};
        this.color = model.color;
        this.texture = texture;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            GlStateManager.pushMatrix();
            int glList = this.arms[side.ordinal()];
            ModelRenderer pArm = this.playerArms[side.ordinal()];
            float x = pArm.rotateAngleX;
            float y = pArm.rotateAngleY;
            float z = pArm.rotateAngleZ;

            GlStateManager.rotate(z * 57.295776F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(y * 57.295776F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(x * 57.295776F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(side == EnumHandSideAA.RIGHT ? (1.0F / 16.0F) : -(1.0F / 16.0F), -1.7F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.color(this.color[0], this.color[1], this.color[2]);
            GlStateManager.callList(glList);
            GlStateManager.popMatrix();

            iterator.postBind();
        }
    }
}
