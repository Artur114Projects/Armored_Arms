package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ArmModelRendererHBM implements IArmModelRenderer<ArmModelManagerArmor> {
    public final ModelRenderer[] playerArms = AAUtils.playerArms();
    public final ModelRendererObj[] arms;
    public final IMultiTexture texture;
    public final ModelBiped mb;

    public ArmModelRendererHBM(ModelBiped mb, IMultiTexture texture, ModelRendererObj right, ModelRendererObj left) {
        this.arms = new ModelRendererObj[] {left, right};
        this.texture = texture;
        this.mb = mb;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            ModelRendererObj arm = this.arms[side.ordinal()];
            ModelRenderer pArm = this.playerArms[side.ordinal()];
            arm.rotationPointX = -5.0F * side.delta();
            arm.rotationPointY = 2.0F;
            arm.rotationPointZ = 0.0F;
            arm.rotateAngleX = pArm.rotateAngleX;
            arm.rotateAngleY = pArm.rotateAngleY;
            arm.rotateAngleZ = pArm.rotateAngleZ;
            arm.offsetX = pArm.offsetX;
            arm.offsetY = pArm.offsetY;
            arm.offsetZ = pArm.offsetZ;
            arm.render(1.0F / 16.0F);
            iterator.postBind();
        }
    }
}
