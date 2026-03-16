package com.artur114.armoredarms.client.integration.cqrepoured.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;

public class ArmModelRendererSlime extends ArmModelRendererArmor {
    public ArmModelRendererSlime(ModelBiped mb, IMultiTexture texture) {
        super(mb, texture);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        super.renderArm(context, side);
        GlStateManager.disableBlend();
    }
}
