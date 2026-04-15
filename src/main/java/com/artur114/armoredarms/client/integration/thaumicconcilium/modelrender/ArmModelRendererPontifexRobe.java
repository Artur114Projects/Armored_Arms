package com.artur114.armoredarms.client.integration.thaumicconcilium.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ArmModelRendererPontifexRobe extends ArmModelRendererArmor {
    public ArmModelRendererPontifexRobe(ModelBiped mb, IMultiTexture texture) {
        super(mb, texture);
    }

    public ArmModelRendererPontifexRobe(ModelBiped mb, IMultiTexture texture, ModelRenderer right, ModelRenderer left) {
        super(mb, texture, right, left);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        GL11.glPushMatrix();
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        super.renderArm(context, side);
        GL11.glPopMatrix();
    }
}
