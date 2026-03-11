package com.artur114.armoredarms.core.api.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.armorlayer.AbstractArmorRenderLayer;

public interface IArmModelManager<I extends IArmModelManager<?, L>, L extends AbstractArmorRenderLayer<?, ?, ?>> {
    void render(L layer, IArmModelRenderer<I> renderer, EnumHandSideAA side);
    IArmModelRenderer<I> cacheRenderer(L layer, IArmModelRenderContainer<L, I> container);
    Class<L> targetLayer();
    Class<I> clazz();
}
