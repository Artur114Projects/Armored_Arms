package com.artur114.armoredarms.client.integration.alfheim.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.ArmsBone;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITextureIterator;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class ArmModelRendererElvorium implements IArmModelRenderer<ArmModelManagerArmor> {
    private final IModelCustom modelCustom;
    private final IMultiTexture texture;

    public ArmModelRendererElvorium(IMultiTexture texture, IModelCustom modelCustom) {
        this.modelCustom = modelCustom;
        this.texture = texture;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        ITextureIterator iterator = this.texture.textureIterator();

        while (iterator.hasNext()) {
            iterator.bindNext();
            float parTicks = 1.0F / 16.0F;
            Bone arm = context.layer.engine().mainBones().bySide(side);
            GL11.glPushMatrix();
            GL11.glTranslatef(arm.rotationPointX * parTicks, arm.rotationPointY * parTicks, arm.rotationPointZ * parTicks);
            GL11.glRotatef((float) (arm.rotateAngleZ * (180.0F / Math.PI)), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef((float) (arm.rotateAngleY * (180.0F / Math.PI)), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float) (arm.rotateAngleX * (180.0F / Math.PI)), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            double s = 0.01;
            if (side == EnumHandSideAA.RIGHT) {
                GL11.glTranslated(0.31, -0.55, 0.0);
                GL11.glScaled(s, s, s);
                this.modelCustom.renderPart("ArmO");
            } else {
                GL11.glTranslated(-0.31, -0.55, 0.0);
                GL11.glScaled(s, s, s);
                this.modelCustom.renderPart("ArmT");
            }
            GL11.glPopMatrix();
            iterator.postBind();
        }
    }
}
