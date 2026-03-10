package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;

public interface IArmModelRenderer<M extends IArmModelManager<?, ?>> {
    void renderArm(M context, EnumHandSideAA side);
}
