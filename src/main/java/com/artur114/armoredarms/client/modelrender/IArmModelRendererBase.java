package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import net.minecraft.client.model.geom.ModelPart;

public interface IArmModelRendererBase<M extends IArmModelManager<?, ?>> extends IArmModelRenderer<M> {
    default void renderDefault(IModelRenderContext context, Bone bone, ModelPart arm) {
        bone.injectTo(arm);
//        arm.xRot = 0.0F;
        boolean s = arm.skipDraw;
        boolean v = arm.visible;
        arm.skipDraw = false;
        arm.visible = true;
        context.renderPart(arm);
        arm.skipDraw = s;
        arm.visible = v;
    }
    default void renderDefault(MultiModelRenderContext context, Bone bone, ModelPart arm) {
        for (IModelRenderContext part : context) this.renderDefault(part, bone, arm);
    }
}
