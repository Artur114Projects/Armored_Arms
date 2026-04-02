package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ArmModelRendererPlayer implements IArmModelRenderer<ArmModelManagerPlayer> {
    protected final Minecraft mc = Minecraft.getInstance();
    protected final MultiModelRenderContext context;

    public ArmModelRendererPlayer(MultiModelRenderContext context) {
        this.context = context;
    }

    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        if (this.mc.player == null) return;
        ModelPart armWear = context.armWear(side);
        ModelPart arm = context.arm(side);

        int i = 0;

        for (IModelRenderContext part : this.context) {
            ModelPart modelPart;

            if (i % 2 == 0) {
                modelPart = arm;
            } else {
                modelPart = armWear;
            }

            i++;

            if (modelPart == armWear && !context.shouldRenderWear) {
                continue;
            }

            modelPart.xRot = 0.0F;
            part.renderPart(modelPart);
        }
    }
}
