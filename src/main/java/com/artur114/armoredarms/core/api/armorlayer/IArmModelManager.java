package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;

public interface IArmModelManager<T extends IArmModelManager<?, ?>, L extends AbstractArmorRenderLayer<?, ?, ?>> {
    void render(L layer, IArmModelRenderer<T> renderer, EnumHandSideAA side);
    IArmModelRenderer<T> cacheRenderer(L layer, IArmModelRenderContainer<T> container);
    Class<L> targetLayer();
    Class<T> clazz();
}
