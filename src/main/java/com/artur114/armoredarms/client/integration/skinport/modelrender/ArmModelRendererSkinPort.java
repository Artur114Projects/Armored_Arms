package com.artur114.armoredarms.client.integration.skinport.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ArmModelRendererSkinPort implements IArmModelRenderer<ArmModelManagerPlayer> {
    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        context.mc.getTextureManager().bindTexture(context.playerSkin);
        ModelBiped mb = context.renderPlayer.modelBipedMain;
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        context.prepareModel(mb);
        ModelRenderer renderer = AAUtils.handFromModelBiped(mb, side);
        if (context.shouldRenderWear) {
            renderer.render(0.0625F);
        } else {
            List child = renderer.childModels;
            renderer.childModels = null;
            renderer.render(0.0625F);
            renderer.childModels = child;
        }
    }
}
