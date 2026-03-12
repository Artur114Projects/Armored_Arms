package com.artur114.armoredarms.core.api.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.layer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;

import java.util.function.Function;

public interface IArmModelManager<I extends IArmModelManager<?, L>, L extends IArmRenderLayer<?>> extends IPrioritised, IArmRenderComponent {
    void update(L layer);
    void render(L layer, IArmModelRenderer<I> renderer, EnumHandSideAA side);
    IArmModelRenderer<I> cacheRenderer(L layer, IArmModelRenderContainer<L, I> container);
    Class<L> targetLayer();
    Class<I> clazz();

    @Override
    default String type() {
        return "model-manager";
    }
}
