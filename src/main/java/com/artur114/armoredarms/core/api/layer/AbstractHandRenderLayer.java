package com.artur114.armoredarms.core.api.layer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.*;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractHandRenderLayer<I extends AbstractHandRenderLayer<?, ?, ?>, S extends IItemStack, E extends IArmRenderEngine<?>> implements IArmRenderLayer<E> {
    public List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> renderContainers;
    public IArmModelRenderContainer<I, IArmModelManager<?, I>> lastContainer;
    public IArmModelRenderer<IArmModelManager<?, I>> model;
    public List<ShapelessLocation> renderArmWearList;
    public S currentChestPlate = this.emptyStack();
    public IArmModelManager<?, I> modelManager;
    public boolean deactivated = false;
    public IAAModContainer mod;
    public E engine;

    @Override
    public void init(E engine, IAAModContainer mod) {
        try {
            this.tryInit(engine, mod);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public void update(E engine) {
        if (this.deactivated) {
            return;
        }

        try {
            this.tryTick(engine);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public void render(E engine, EnumHandSideAA handSide) {
        if (this.deactivated) {
            return;
        }

        try {
            this.tryRender(engine, handSide);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public boolean needRender(E engine, boolean renderEngineState) {
        return renderEngineState;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    public IArmModelRenderContainer<I, IArmModelManager<?, I>> pickUpContainer(IArmModelManager<?, I> modelManager) {
        for (IArmModelRenderContainer<I, IArmModelManager<?, I>> container : this.renderContainers) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz()) && container.needWork(modelManager)) {
                return container;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public IArmModelRenderer<IArmModelManager<?, I>> cacheRenderer(IArmModelManager<?, I> modelManager, IArmModelRenderContainer<I, IArmModelManager<?, I>> container) {
        return (IArmModelRenderer<IArmModelManager<?, I>>) modelManager.cacheRenderer((I) this, (IArmModelRenderContainer) container);
    }

    public abstract S currentChestPlate();
    public abstract S emptyStack();

    public abstract List<ShapelessLocation> initRenderWearList();
    public abstract List<IArmModelManager<?, ?>> initModelManager();
    public abstract List<IArmModelRenderContainer<?, ?>> initRenderContainers();

    @SuppressWarnings("unchecked")
    public IArmModelManager<?, I> castModelManagers(List<IArmModelManager<?, ?>> list) {
        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        for (IArmModelManager<?, ?> manager : CoreUtils.sortPrioritisedList(list)) {
            if (manager.targetLayer().isAssignableFrom(clazz)) {
                loggerCore.info("Registered model manager");
                loggerCore.info("   Layer: {}", this);
                loggerCore.info("   Manager: {}", manager);
                return (IArmModelManager<?, I>) manager;
            } else {
                loggerCore.error("Attempting to register an incompatible model manager!");
                loggerCore.error("  Layer: {}", this);
                loggerCore.error("  Manager: {}", manager);
            }
        }

        loggerCore.warn("Could not find a suitable model manager!");
        loggerCore.warn("   Layer: {}", this);
        loggerCore.warn("   Engine: {}", this.engine);

        return null;
    }

    @SuppressWarnings("unchecked")
    public List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> castRenderContainers(List<IArmModelRenderContainer<?, ?>> list) {
        List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> ret = new ArrayList<>(list.size());

        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        for (IArmModelRenderContainer<?, ?> container : list) {
            if (container.targetLayer().isAssignableFrom(clazz)) {
                ret.add((IArmModelRenderContainer<I,  IArmModelManager<?, I>>) container);
                loggerCore.info("Registered dynamic render container");
                loggerCore.info("   Layer: {}", this);
                loggerCore.info("   Container: {}", container);
            } else {
                loggerCore.error("Attempting to register an incompatible dynamic container!");
                loggerCore.error("  Layer: {}", this);
                loggerCore.error("  Container: {}", container);
            }
        }

        return CoreUtils.sortPrioritisedList(ret);
    }

    public void tryInit(E engine, IAAModContainer mod) {
        this.engine = engine;
        this.mod = mod;

        this.renderArmWearList = this.initRenderWearList();
        this.modelManager = this.castModelManagers(this.initModelManager());
        this.renderContainers = this.castRenderContainers(this.initRenderContainers());
    }

    @SuppressWarnings("unchecked")
    public void tryTick(E engine) {
        this.currentChestPlate = this.currentChestPlate();

        IArmModelRenderContainer<I, IArmModelManager<?, I>> container = this.pickUpContainer(this.modelManager);

        if (this.lastContainer != container && container != null) {
            this.model = this.cacheRenderer(this.modelManager, container);
        } else if (container == null) {
            this.model = null;
        }

        if (this.model == null && this.lastContainer != container) {
            Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
            loggerCore.warn("Could not find a suitable render container!");
            loggerCore.warn("   Layer: {}", this);
            loggerCore.warn("   Engine: {}", engine);
            loggerCore.warn("   Model manager: {}", this.modelManager);
            loggerCore.warn("   Model render container: {}", container);
        }

        this.lastContainer = container;

        if (this.modelManager != null) {
            this.modelManager.update((I) this);
        }
    }

    @SuppressWarnings("unchecked")
    public void tryRender(E engine, EnumHandSideAA handSide) {
        if (this.model != null && this.modelManager != null) {
            this.modelManager.render((I) this, (IArmModelRenderer) this.model, handSide);
        }
    }
}
