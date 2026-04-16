package com.artur114.armoredarms.client.integration.clfcsl.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.modelrender.player.ArmModelRendererPlayer;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ArmModelRendererModelPlayer implements IArmModelRenderer<ArmModelManagerPlayer> {
    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        context.mc.getTextureManager().bindTexture(context.playerSkin);
        ModelBiped mb = context.renderPlayer.modelBipedMain;
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        context.prepareModel(mb);
        ModelRenderer renderer = AAUtils.handFromModelBiped(mb, side);
        ModelRenderer rendererWear;
        if (side == EnumHandSideAA.RIGHT) {
            rendererWear = ((ModelPlayer) mb).field_178732_b;
        } else {
            rendererWear = ((ModelPlayer) mb).field_178734_a;
        }
        if (context.shouldRenderWear) {
            this.renderWithoutChild(renderer);

            float rotationPointX = rendererWear.rotationPointX;
            float rotationPointY = rendererWear.rotationPointY;
            float rotationPointZ = rendererWear.rotationPointZ;
            float rotateAngleX = rendererWear.rotateAngleX;
            float rotateAngleY = rendererWear.rotateAngleY;
            float rotateAngleZ = rendererWear.rotateAngleZ;
            float offsetX = rendererWear.offsetX;
            float offsetY = rendererWear.offsetY;
            float offsetZ = rendererWear.offsetZ;

            rendererWear.rotationPointX = renderer.rotationPointX;
            rendererWear.rotationPointY = renderer.rotationPointY;
            rendererWear.rotationPointZ = renderer.rotationPointZ;
            rendererWear.rotateAngleX = renderer.rotateAngleX;
            rendererWear.rotateAngleY = renderer.rotateAngleY;
            rendererWear.rotateAngleZ = renderer.rotateAngleZ;
            rendererWear.offsetX = renderer.offsetX;
            rendererWear.offsetY = renderer.offsetY;
            rendererWear.offsetZ = renderer.offsetZ;

            rendererWear.render(0.0625F);

            rendererWear.rotationPointX = rotationPointX;
            rendererWear.rotationPointY = rotationPointY;
            rendererWear.rotationPointZ = rotationPointZ;
            rendererWear.rotateAngleX = rotateAngleX;
            rendererWear.rotateAngleY = rotateAngleY;
            rendererWear.rotateAngleZ = rotateAngleZ;
            rendererWear.offsetX = offsetX;
            rendererWear.offsetY = offsetY;
            rendererWear.offsetZ = offsetZ;
        } else {
            this.renderWithoutChild(renderer);
        }
    }


    private void renderWithoutChild(ModelRenderer renderer) {
        List child = renderer.childModels;
        renderer.childModels = null;
        renderer.render(0.0625F);
        renderer.childModels = child;
    }

    /*
        context.mc.getTextureManager().bindTexture(context.playerSkin);
        ModelBiped mb = context.renderPlayer.modelBipedMain;
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        context.prepareModel(mb);
        ModelRenderer renderer = AAUtils.handFromModelBiped(mb, side);
        ModelRenderer rendererWear;
        if (side == EnumHandSideAA.RIGHT) {
            rendererWear = ((ModelPlayer) mb).field_178732_b;
        } else {
            rendererWear = ((ModelPlayer) mb).field_178734_a;
        }
        if (context.shouldRenderWear) {
            renderer.render(0.0625F);
            rendererWear.render(0.0625F);
        } else {
            renderer.render(0.0625F);
        }
     */
}
