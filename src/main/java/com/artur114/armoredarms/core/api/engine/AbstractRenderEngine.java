package com.artur114.armoredarms.core.api.engine;

import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.CoreUtils;
import com.artur114.armoredarms.core.util.IAAModContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRenderEngine<E extends AbstractRenderEngine<?, ?>, P extends IArmRenderPipeline<?>> implements IArmRenderEngine<P> {
    protected Map<Class<? extends IArmRenderLayer<E>>, IArmRenderLayer<E>> layerMap;
    protected List<IArmRenderLayer<E>> sortedLayers;

    @Override
    @SuppressWarnings("unchecked")
    public void init(P context, IAAModContainer mod) {
        this.layerMap = new HashMap<>(this.initLayers());
        this.sortedLayers = CoreUtils.sortPrioritisedList(this.layerMap.values());

        for (IArmRenderLayer<E> layer : this.sortedLayers) layer.init((E) this, mod);
    }

    @Override
    public <L extends IArmRenderLayer<?>> L layer(Class<L> clazz) {
        IArmRenderLayer<E> layer = this.layerMap.get(clazz);
        if (clazz.isInstance(layer)) {
            return clazz.cast(layer);
        }
        return null;
    }

    protected abstract Map<Class<? extends IArmRenderLayer<E>>, IArmRenderLayer<E>> initLayers();
}
