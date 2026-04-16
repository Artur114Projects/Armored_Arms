package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.ModelRenderer;

public interface IArmModelRendererBase<M extends IArmModelManager<?, ?>> extends IArmModelRenderer<M> {
    default void defaultRenderModel(IArmRenderEngine<?> engine, ModelRenderer arm, EnumHandSideAA side) {
        arm.rotationPointX = -5.0F * side.delta();
        arm.rotationPointY = 2.0F;
        arm.rotationPointZ = 0.0F;
        engine.mainBones().bySide(side).injectTo(arm);
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