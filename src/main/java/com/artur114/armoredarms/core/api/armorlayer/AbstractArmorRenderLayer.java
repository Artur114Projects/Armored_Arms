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
    protected static final Logger LOGGER = LogManager.getLogger();

    protected ShapelessLocationMap<IArmModelRenderContainer<IArmModelManager<?, T>>> renderContainers;
    protected ShapelessLocationMap<IArmModelManager<?, T>> modelManagers;
    protected List<ShapelessLocation> blackList;
    protected Set<IItemStack> killingArmor;
    protected boolean render = false;
    protected I chestPlate;

    protected IArmModelManager<?, T> modelManager = null;
    protected IArmModelRenderer<IArmModelManager<?, T>> model = null;

    @Override
    public void init(E engine, IAAModContainer mod) {
        try {
            this.tryInit(engine, mod);
        } catch (Throwable t) {

        }
    }

    @Override
    public void update(E engine) {
        try {
            this.tryTick(engine);
        } catch (Throwable t) {

        }
    }

    @Override
    public void render(E engine, EnumHandSideAA handSide) {
        try {
            this.tryRender(engine, handSide);
        } catch (Throwable t) {

        }
    }

    protected IArmModelManager<?, T> pickUpModelManager(ShapelessLocation location) {
        return this.modelManagers.get(location);
    }

    @SuppressWarnings("unchecked")
    protected IArmModelRenderer<IArmModelManager<?, T>> cacheRenderer(IArmModelManager<?, T> modelManager, ShapelessLocation location) {
        List<IArmModelRenderContainer<IArmModelManager<?, T>>> containers = this.renderContainers.getAll(location);

        for (IArmModelRenderContainer<IArmModelManager<?, T>> container : CoreUtils.sortPrioritisedList(containers)) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz())) {
                return (IArmModelRenderer<IArmModelManager<?, T>>) modelManager.cacheRenderer((T) this, (IArmModelRenderContainer) container);
            }
        }

        LOGGER.warn("Could not find a suitable render container, armor layer: {}, model manager: {}", this, modelManager);
        return null;
    }

    protected abstract I currentChestPlate();
    protected abstract I emptyStack();

    protected abstract List<ShapelessLocation> initBlackList();
    protected abstract ShapelessLocationMap<IArmModelManager<?, T>> initModelManagers();
    protected abstract ShapelessLocationMap<IArmModelRenderContainer<IArmModelManager<?, T>>> initRenderContainers();

    protected void tryInit(E engine, IAAModContainer mod) {
        this.renderContainers = this.initRenderContainers();
        this.modelManagers = this.initModelManagers();
        this.blackList = new ArrayList<>(this.initBlackList());
    }

    protected void tryTick(E engine) {
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
    protected void tryRender(E engine, EnumHandSideAA handSide) {
        if (this.modelManager != null && this.model != null) {
            this.modelManager.render((T) this, (IArmModelRenderer) this.model, handSide);
        }
    }
}
