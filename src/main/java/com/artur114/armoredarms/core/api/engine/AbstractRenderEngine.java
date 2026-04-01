package com.artur114.armoredarms.core.api.engine;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.CoreUtils;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRenderEngine<E extends AbstractRenderEngine<?, ?>, P extends IArmRenderPipeline<?>> implements IArmRenderEngine<P> {
    protected Map<Class<? extends IArmRenderLayer<E>>, IArmRenderLayer<E>> layerMap;
    protected List<IArmRenderLayer<E>> sortedLayers;
    public IAAModContainer mod = null;
    public boolean deactivated = false;
    public boolean render = false;
    public P pipeline = null;

    @Override
    @SuppressWarnings("unchecked")
    public void init(P context, IAAModContainer mod) {
        this.pipeline = context;
        this.mod = mod;

        Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> rawLayers = this.initLayers();
        Logger loggerCore = mod.logger("ARMOREDARMS-CORE");
        this.layerMap = new HashMap<>();
        Class<?> clazz = this.getClass();

        rawLayers.forEach(((aClass, iArmRenderLayer) -> {
            if (iArmRenderLayer != null && iArmRenderLayer.targetEngine().isAssignableFrom(clazz)) {
                loggerCore.info("Registered render layer");
                loggerCore.info("   Layer: {}", iArmRenderLayer);
                loggerCore.info("   Engine: {}", this);
                loggerCore.info("   Priority: {}", iArmRenderLayer.priority());
                this.layerMap.put((Class<? extends IArmRenderLayer<E>>) aClass, (IArmRenderLayer<E>) iArmRenderLayer);
            } else {
                loggerCore.error("Attempting to initialize an incompatible layer!");
                loggerCore.error("   Layer: {}", iArmRenderLayer);
                loggerCore.error("   Engine: {}", this);
            }
        }));

        this.sortedLayers = CoreUtils.sortPrioritisedList(this.layerMap.values());

        loggerCore.info("Render queue");
        loggerCore.info("   -start:");
        for (IArmRenderLayer<E> layer : this.sortedLayers) {
            loggerCore.info("       -> {}", layer);
        }
        loggerCore.info("   -end|");

        for (IArmRenderLayer<E> layer : this.sortedLayers) {
            try {
                layer.init((E) this, mod);
            } catch (RenderException rm) {
                throw rm;
            } catch (Throwable t) {
                throw new RenderException(t).setComponent(layer);
            }
        }
    }

    @Override
    public <L extends IArmRenderLayer<?>> L layer(Class<L> clazz) {
        IArmRenderLayer<E> layer = this.layerMap.get(clazz);
        if (clazz.isInstance(layer)) {
            return clazz.cast(layer);
        }
        return null;
    }

    @Override
    public void tryRender(P context) {
        if (this.deactivated || !this.render) {
            return;
        }

        this.render(context);
    }

    @Override
    public void tryTick(P context) {
        if (this.deactivated) {
            return;
        }

        this.tick(context);
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    public abstract void render(P context);
    public abstract void tick(P context);

    public boolean onLayerRendering(IArmRenderLayer<E> layer, EnumHandSideAA side) {
        return true;
    }

    public void cleanUpLayers() {
        if (CoreUtils.removeDeactivated(this.layerMap.values())) {
            this.sortedLayers = CoreUtils.sortPrioritisedList(this.layerMap.values());
        }
    }

    @SuppressWarnings("unchecked")
    public void renderAllLayers(EnumHandSideAA side) {
        for (IArmRenderLayer<E> layer : this.sortedLayers) {
            if (layer.needRender((E) this, this.render)) {
                try {
                    if (this.onLayerRendering(layer, side)) {
                        layer.render((E) this, side);
                    }
                } catch (RenderException rm) {
                    throw rm;
                } catch (Throwable tr) {
                    throw new RenderException(tr).setComponent(layer);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean updateAllLayers() {
        boolean render = false;

        for (IArmRenderLayer<E> layer : this.sortedLayers) {

            try {
                layer.update((E) this);
            } catch (RenderException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RenderException(tr).setComponent(layer);
            }

            render |= layer.needRender((E) this, render);
        }

        return render;
    }

    protected abstract Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers();
}
