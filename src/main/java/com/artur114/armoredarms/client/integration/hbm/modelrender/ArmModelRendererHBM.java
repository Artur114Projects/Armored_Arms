package com.artur114.armoredarms.client.integration.hbm.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.Minecraft;

public class ArmModelRendererHBM implements IArmModelRenderer<ArmModelManagerArmor> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public final ModelRendererObj[] arms;
    public final IMultiTexture texture;

    public ArmModelRendererHBM(IMultiTexture texture, ModelRendererObj right, ModelRendererObj left) {
        this.arms = new ModelRendererObj[] {left, right};
        this.texture = texture;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            ModelRendererObj arm = this.arms[side.ordinal()];
            Bone bone = context.layer.engine().mainBones().bySide(side);
            arm.rotationPointX = bone.rotationPointX;
            arm.rotationPointY = bone.rotationPointY;
            arm.rotationPointZ = bone.rotationPointZ;
            arm.rotateAngleX = bone.rotateAngleX;
            arm.rotateAngleY = bone.rotateAngleY;
            arm.rotateAngleZ = bone.rotateAngleZ;
            arm.offsetX = bone.offsetX;
            arm.offsetY = bone.offsetY;
            arm.offsetZ = bone.offsetZ;
            arm.render(1.0F / 16.0F);
            iterator.postBind();
        }
    }
}
