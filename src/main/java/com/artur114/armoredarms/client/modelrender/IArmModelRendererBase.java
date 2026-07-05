package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import net.minecraft.client.model.ModelRenderer;

public interface IArmModelRendererBase<M extends IArmModelManager<?, ?>> extends IArmModelRenderer<M> {
    default void defaultRenderModel(Bone bone, ModelRenderer arm) {
        bone.injectTo(arm);
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
