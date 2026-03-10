package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.CoreUtils;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.core.util.ShapelessLocationMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class AbstractArmorRenderLayer<T extends AbstractArmorRenderLayer<?, ?, ?>, I extends IItemStack, E extends IArmRenderEngine<?>> implements IArmRenderLayer<E> {
    public static final Logger LOGGER = LogManager.getLogger();

    public ShapelessLocationMap<IArmModelRenderContainer<? extends IArmModelManager<?, T>>> renderContainers;
    public ShapelessLocationMap<IArmModelManager<?, T>> modelManagers;
    public List<ShapelessLocation> blackList;
    public Set<IItemStack> killingArmor;
    public boolean deactivated = false;
    public boolean render = false;
    public I chestPlate;

    public IArmModelManager<?, T> modelManager = null;
    public IArmModelRenderer<IArmModelManager<?, T>> model = null;

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
        return this.render;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    public IArmModelManager<?, T> pickUpModelManager(ShapelessLocation location) {
        return this.modelManagers.get(location);
    }

    @SuppressWarnings("unchecked")
    public IArmModelRenderer<IArmModelManager<?, T>> cacheRenderer(IArmModelManager<?, T> modelManager, ShapelessLocation location) {
        List<IArmModelRenderContainer<? extends IArmModelManager<?, T>>> containers = this.renderContainers.getAll(location);

        for (IArmModelRenderContainer<?> container : CoreUtils.sortPrioritisedList(containers)) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz())) {
                return (IArmModelRenderer<IArmModelManager<?, T>>) modelManager.cacheRenderer((T) this, (IArmModelRenderContainer) container);
            }
        }

        LOGGER.warn("Could not find a suitable render container, armor layer: {}, model manager: {}", this, modelManager);
        return null;
    }

    public abstract I currentChestPlate();
    public abstract I emptyStack();

    public abstract List<ShapelessLocation> initBlackList();
    public abstract ShapelessLocationMap<IArmModelManager<?, T>> initModelManagers();
    public abstract ShapelessLocationMap<IArmModelRenderContainer<? extends IArmModelManager<?, T>>> initRenderContainers();

    public void tryInit(E engine, IAAModContainer mod) {
        this.renderContainers = this.initRenderContainers();
        this.modelManagers = this.initModelManagers();
        this.blackList = new ArrayList<>(this.initBlackList());
    }

    public void tryTick(E engine) {
        I chestPlate = this.currentChestPlate();

        if (chestPlate.isEmpty() || this.killingArmor.contains(chestPlate)) {
            this.chestPlate = this.emptyStack();
            this.render = false;
            return;
        }


        if (!this.chestPlate.isNew(chestPlate)) {
            return;
        }

        if (this.blackList.contains(chestPlate.location())) {
            this.chestPlate = chestPlate;
            this.render = false;
            return;
        }

        this.render = true;
        this.chestPlate = chestPlate;

        this.modelManager = this.pickUpModelManager(chestPlate.location());
        this.model = this.cacheRenderer(this.modelManager, chestPlate.location());

        if (this.modelManager == null || this.model == null) {
            LOGGER.warn("Could not find a suitable render components for: {}, armor layer: {}", chestPlate.location(), this);
        }
    }

    @SuppressWarnings("unchecked")
    public void tryRender(E engine, EnumHandSideAA handSide) {
        if (this.modelManager != null && this.model != null) {
            this.modelManager.render((T) this, (IArmModelRenderer) this.model, handSide);
        }
    }
}
