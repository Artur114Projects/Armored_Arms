package com.artur114.armoredarms.core.api.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderComponent;

public interface IArmModelRenderer<M extends IArmModelManager<?, ?>> extends IArmRenderComponent {
    void renderArm(M context, EnumHandSideAA side);

    @Override
    default String type() {
        return "model-renderer";
    }
}
