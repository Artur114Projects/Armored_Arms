package com.artur114.armoredarms.client.integration.deepresonance.modelrender;

import com.artur114.armoredarms.client.modelrender.IArmModelRendererBase;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import com.artur114.armoredarms.core.util.Reflector;
import mcjty.deepresonance.items.armor.ChestModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ArmModelRendererDeep implements IArmModelRendererBase<ArmModelManagerArmor> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public final IMultiTexture texture;
    public final ModelRenderer[] right;
    public final ModelRenderer[] left;
    public final ChestModel gavno;

    public ArmModelRendererDeep(IMultiTexture texture, ChestModel mb) {
        this.texture = texture;
        this.gavno = mb;

        this.right = new ModelRenderer[] {
                Reflector.getPrivateField(mb, "righthand"),
                Reflector.getPrivateField(mb, "rightarmside1"),
                Reflector.getPrivateField(mb, "rightarmside2"),
                Reflector.getPrivateField(mb, "rightarmfront"),
                Reflector.getPrivateField(mb, "rightarmback"),
                Reflector.getPrivateField(mb, "rightshoulder"),
        };
        this.left = new ModelRenderer[] {
                Reflector.getPrivateField(mb, "lefthand"),
                Reflector.getPrivateField(mb, "leftarmside1"),
                Reflector.getPrivateField(mb, "leftarmside2"),
                Reflector.getPrivateField(mb, "leftarmfront"),
                Reflector.getPrivateField(mb, "leftarmback"),
                Reflector.getPrivateField(mb, "leftshoulder"),
        };
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            if (side == EnumHandSideAA.RIGHT) {
                for (ModelRenderer renderer : this.right) {
                    renderer.render(1.0F / 16.0F);
                }
            } else {
                for (ModelRenderer renderer : this.left) {
                    renderer.render(1.0F / 16.0F);
                }
            }
            iterator.postBind();
        }
    }
}
