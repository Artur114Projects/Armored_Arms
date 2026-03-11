package com.artur114.armoredarms.client.armorlayer;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.ModelRenderer;

public interface IArmModelRendererBase<M extends IArmModelManager<?, ?>> extends IArmModelRenderer<M> {
    default void defaultRenderModel(ModelRenderer arm, ModelRenderer playerArm, EnumHandSideAA side) {
        arm.rotationPointX = -5.0F * side.delta();
        arm.rotationPointY = 2.0F;
        arm.rotationPointZ = 0.0F;
        AAUtils.setPlayerArmDataToArm(arm, playerArm);
        arm.rotateAngleX = 0.0F;
        boolean h = arm.isHidden;
        boolean s = arm.showModel;
        arm.isHidden = false;
        arm.showModel = true;
        arm.render(1.0F / 16.0F);
        arm.isHidden = h;
        arm.showModel = s;
    }
}
