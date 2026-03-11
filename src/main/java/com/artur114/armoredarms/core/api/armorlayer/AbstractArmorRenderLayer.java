package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IArmRenderLayer;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.CoreUtils;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.core.util.ShapelessLocationMap;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class AbstractArmorRenderLayer<I extends AbstractArmorRenderLayer<?, ?, ?>, S extends IItemStack, E extends IArmRenderEngine<?>> implements IArmRenderLayer<E> {
    public ShapelessLocationMap<IArmModelRenderContainer<I, ? extends IArmModelManager<?, I>>> renderContainers;
    public ShapelessLocationMap<IArmModelManager<?, I>> modelManagers;
    public List<ShapelessLocation> blackList;
    public Set<IItemStack> killingArmor;
    public boolean deactivated = false;
    public boolean render = false;
    public IAAModContainer mod;
    public E engine;
    public S chestPlate = this.emptyStack();

    public IArmModelManager<?, I> modelManager = null;
    public IArmModelRenderer<IArmModelManager<?, I>> model = null;

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

    public IArmModelManager<?, I> pickUpModelManager(ShapelessLocation location) {
        return this.modelManagers.get(location);
    }

    @SuppressWarnings("unchecked")
    public IArmModelRenderer<IArmModelManager<?, I>> cacheRenderer(IArmModelManager<?, I> modelManager, ShapelessLocation location) {
        List<IArmModelRenderContainer<I, ? extends IArmModelManager<?, I>>> containers = this.renderContainers.getAll(location);

        for (IArmModelRenderContainer<?, ?> container : CoreUtils.sortPrioritisedList(containers)) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz())) {
                return (IArmModelRenderer<IArmModelManager<?, I>>) modelManager.cacheRenderer((I) this, (IArmModelRenderContainer) container);
            }
        }

        this.mod.logger("ARMOREDARMS-CORE").warn("Could not find a suitable render container, layer: {}, model manager: {}", this, modelManager);
        return null;
    }

    public abstract S currentChestPlate();
    public abstract S emptyStack();

    public abstract List<ShapelessLocation> initBlackList();
    public abstract ShapelessLocationMap<IArmModelManager<?, ?>> initModelManagers();
    public abstract ShapelessLocationMap<IArmModelRenderContainer<?, ?>> initRenderContainers();

    @SuppressWarnings("unchecked")
    public ShapelessLocationMap<IArmModelRenderContainer<I, ? extends IArmModelManager<?, I>>> castContainers(ShapelessLocationMap<IArmModelRenderContainer<?, ?>> map) {
        ShapelessLocationMap<IArmModelRenderContainer<I, ? extends IArmModelManager<?, I>>> ret = new ShapelessLocationMap<>();
        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        map.forEach((location, container) -> {
            if (container.targetLayer().isAssignableFrom(clazz)) {
                ret.put(location, (IArmModelRenderContainer<I, ? extends IArmModelManager<?, I>>) container);
                loggerCore.info("Registered render container: {}, for layer: {}", container.getClass(), clazz);
            } else {
                loggerCore.error("Attempting to register an incompatible container: {}, for layer: {}", container.getClass(), clazz);
            }
        });

        return ret;
    }

    @SuppressWarnings("unchecked")
    public ShapelessLocationMap<IArmModelManager<?, I>> castModelManagers(ShapelessLocationMap<IArmModelManager<?, ?>> map) {
        ShapelessLocationMap<IArmModelManager<?, I>> ret = new ShapelessLocationMap<>();
        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        map.forEach((location, modelManager) -> {
            if (modelManager.targetLayer().isAssignableFrom(clazz)) {
                ret.put(location, (IArmModelManager<?, I>) modelManager);
                loggerCore.info("Registered model manager : {}, for layer: {}", modelManager.getClass(), clazz);
            } else {
                loggerCore.error("Attempting to register an incompatible model manager: {}, for layer: {}", modelManager.getClass(), clazz);
            }
        });

        return ret;
    }

    public void tryInit(E engine, IAAModContainer mod) {
        this.engine = engine;
        this.mod = mod;
        this.modelManagers = this.castModelManagers(this.initModelManagers());
        this.renderContainers = this.castContainers(this.initRenderContainers());
        this.blackList = new ArrayList<>(this.initBlackList());
        this.killingArmor = new HashSet<>();
    }

    public void tryTick(E engine) {
        S chestPlate = this.currentChestPlate();

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
            this.mod.logger("ARMOREDARMS-CORE").warn("Could not find a suitable render components for: {}, layer: {}", chestPlate.location(), this);
        }
    }

    @SuppressWarnings("unchecked")
    public void tryRender(E engine, EnumHandSideAA handSide) {
        if (this.modelManager != null && this.model != null) {
            this.modelManager.render((I) this, (IArmModelRenderer) this.model, handSide);
        }
    }
}
