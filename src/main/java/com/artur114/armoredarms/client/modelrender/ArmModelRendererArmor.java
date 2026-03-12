package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import com.artur114.armoredarms.core.util.Immutable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

@Immutable
public class ArmModelRendererArmor implements IArmModelRendererBase<ArmModelManagerArmor> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public final ModelRenderer[] playerArms = AAUtils.playerArms();
    public final IMultiTexture texture;
    public final ModelRenderer[] arms;
    public final ModelBiped mb;

    public ArmModelRendererArmor(ModelBiped mb, IMultiTexture texture) {
        this.arms = new ModelRenderer[] {mb.bipedLeftArm, mb.bipedRightArm};
        this.texture = texture;
        this.mb = mb;
    }

    public ArmModelRendererArmor(ModelBiped mb, IMultiTexture texture, ModelRenderer right, ModelRenderer left) {
        this.arms = new ModelRenderer[] {left, right};
        this.texture = texture;
        this.mb = mb;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            this.defaultRenderModel(this.arms[side.ordinal()], this.playerArms[side.ordinal()], side);
            iterator.postBind();
        }
    }
}
