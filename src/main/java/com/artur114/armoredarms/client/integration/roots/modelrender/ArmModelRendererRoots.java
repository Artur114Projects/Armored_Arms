package com.artur114.armoredarms.client.integration.roots.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import com.artur114.armoredarms.core.util.Reflector;
import epicsquid.mysticallib.client.model.ModelArmorBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class ArmModelRendererRoots implements IArmModelRenderer<ArmModelManagerArmor> {
    private final ModelRenderer[] armsB;
    private final ModelRenderer[] arms;
    private final IMultiTexture texture;
    private final ModelArmorBase mb;

    public ArmModelRendererRoots(ModelArmorBase mb, IMultiTexture texture) {
        this.arms = new ModelRenderer[] {Reflector.getPrivateField(ModelArmorBase.class, mb, "armL"), Reflector.getPrivateField(ModelArmorBase.class, mb, "armR")};
        this.armsB = new ModelRenderer[] {mb.bipedLeftArm, mb.bipedRightArm};
        this.texture = texture;
        this.mb = mb;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            ModelRenderer armB = this.armsB[side.ordinal()];
            ModelRenderer arm = this.arms[side.ordinal()];
            armB.rotationPointX = -5.0F * side.delta();
            armB.rotationPointY = 2.0F;
            armB.rotationPointZ = 0.0F;
            context.bone(side).injectTo(armB);
            this.mb.setChestRotation(context.player);
            boolean h = arm.isHidden;
            boolean s = arm.showModel;
            arm.isHidden = false;
            arm.showModel = true;
            arm.render((1.0F / 16.0F) * 1.05F);
            arm.isHidden = h;
            arm.showModel = s;
            iterator.postBind();
        }
    }
}
