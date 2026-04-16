package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.ModelBiped;
import org.lwjgl.opengl.GL11;

public class ArmModelRendererPlayer implements IArmModelRenderer<ArmModelManagerPlayer> {
    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        context.mc.getTextureManager().bindTexture(context.playerSkin);
        ModelBiped mb = context.renderPlayer.modelBipedMain;
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        context.prepareModel(mb);
        AAUtils.handFromModelBiped(mb, side).render(1.0F / 16.0F);
    }
}
