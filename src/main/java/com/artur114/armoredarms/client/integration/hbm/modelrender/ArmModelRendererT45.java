package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ArmModelRendererT45 extends ArmModelRendererArmor {
    public ArmModelRendererT45(ModelBiped mb, IMultiTexture texture) {
        super(mb, texture);
    }

    public ArmModelRendererT45(ModelBiped mb, IMultiTexture texture, ModelRenderer right, ModelRenderer left) {
        super(mb, texture, right, left);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.13F, 1.13F, 1.13F);
        super.renderArm(context, side);
        GlStateManager.popMatrix();
    }
}
