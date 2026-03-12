package com.artur114.armoredarms.core.api.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderComponent;

public interface IArmModelRenderer<M extends IArmModelManager<?, ?>> {
    void renderArm(M context, EnumHandSideAA side);
}
