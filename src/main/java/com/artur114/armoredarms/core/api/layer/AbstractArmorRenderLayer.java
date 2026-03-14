package com.artur114.armoredarms.core.api.layer;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.*;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class AbstractArmorRenderLayer<I extends AbstractArmorRenderLayer<?, ?, ?>, S extends IItemStack, E extends IArmRenderEngine<?>> implements IArmRenderLayer<E> {
    public ShapelessLocationMap<IArmModelRenderContainer<I, IArmModelManager<?, I>>> renderContainers;
    public List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> dynRenderContainers;
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
        } catch (RenderException rm) {
            throw rm;
        } catch (Throwable t) {
            throw new RenderException(t).setComponent(this);
        }
    }

    @Override
    public void update(E engine) {
        if (this.deactivated) {
            return;
        }

        try {
            this.tryTick(engine);
        } catch (RenderException rm) {
            throw rm;
        } catch (Throwable t) {
            this.processException(t);
            throw new RenderException(t).setComponent(this).setMessageForPlayer("armoredarms.error.layer.armor.tryTick").setType(EnumExceptionType.WARN);
        }
    }

    @Override
    public void render(E engine, EnumHandSideAA handSide) {
        if (this.deactivated) {
            return;
        }

        try {
            this.tryRender(engine, handSide);
        } catch (RenderException rm) {
            throw rm;
        } catch (Throwable t) {
            this.processException(t);
            throw new RenderException(t).setComponent(this).setMessageForPlayer("armoredarms.error.layer.armor.tryRender").setType(EnumExceptionType.WARN);
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

    public void processException(Throwable tr) {
        this.killingArmor.add(this.chestPlate);
        this.render = false;
    }

    public IArmModelManager<?, I> pickUpModelManager(ShapelessLocation location) {
        return this.modelManagers.get(location);
    }

    @SuppressWarnings("unchecked")
    public IArmModelRenderer<IArmModelManager<?, I>> cacheRenderer(IArmModelManager<?, I> modelManager, ShapelessLocation location) {
        List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> containers = this.renderContainers.getAll(location);

        for (IArmModelRenderContainer<I, IArmModelManager<?, I>> container : CoreUtils.sortPrioritisedList(containers)) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz())) {
                return (IArmModelRenderer<IArmModelManager<?, I>>) modelManager.cacheRenderer((I) this, (IArmModelRenderContainer) container);
            }
        }

        for (IArmModelRenderContainer<I, IArmModelManager<?, I>> container : this.dynRenderContainers) {
            if (container.targetManager().isAssignableFrom(modelManager.clazz()) && container.needWork(modelManager)) {
                return (IArmModelRenderer<IArmModelManager<?, I>>) modelManager.cacheRenderer((I) this, (IArmModelRenderContainer) container);
            }
        }

        return null;
    }

    public abstract S currentChestPlate();
    public abstract S emptyStack();

    public abstract List<ShapelessLocation> initBlackList();
    public abstract List<SLContainer<IArmModelManager<?, ?>>> initModelManagers();
    public abstract List<SLContainer<IArmModelRenderContainer<?, ?>>> initRenderContainers();

    @SuppressWarnings("unchecked")
    public List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> castDynamicContainers(List<SLContainer<IArmModelRenderContainer<?, ?>>> list) {
        List<IArmModelRenderContainer<I, IArmModelManager<?, I>>> ret = new ArrayList<>(list.size());

        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        for (SLContainer<IArmModelRenderContainer<?, ?>> entry : list) {
            if (!entry.location.isAbsoluteShapeless()) {
                continue;
            }
            if (entry.value.targetLayer().isAssignableFrom(clazz)) {
                ret.add((IArmModelRenderContainer<I,  IArmModelManager<?, I>>) entry.value);
                loggerCore.info("Registered dynamic render container");
                loggerCore.info("   Layer: {}", this);
                loggerCore.info("   Manager: {}", entry.value);
                loggerCore.info("   Location: {}", entry.location);
            } else {
                loggerCore.error("Attempting to register an incompatible dynamic container!");
                loggerCore.error("  Layer: {}", this);
                loggerCore.error("  Manager: {}", entry.value);
                loggerCore.error("  Location: {}", entry.location);
            }
        }

        return CoreUtils.sortPrioritisedList(ret);
    }

    @SuppressWarnings("unchecked")
    public ShapelessLocationMap<IArmModelRenderContainer<I, IArmModelManager<?, I>>> castContainers(List<SLContainer<IArmModelRenderContainer<?, ?>>> list) {
        ShapelessLocationMap<IArmModelRenderContainer<I, IArmModelManager<?, I>>> ret = new ShapelessLocationMap<>();
        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        for (SLContainer<IArmModelRenderContainer<?, ?>> entry : CoreUtils.sortPrioritisedList(list, (v) -> v.value.priority())) {
            if (entry.location.isAbsoluteShapeless()) {
                continue;
            }
            if (entry.value.targetLayer().isAssignableFrom(clazz)) {
                ret.put(entry.location, (IArmModelRenderContainer<I, IArmModelManager<?, I>>) entry.value);
                loggerCore.info("Registered render container");
                loggerCore.info("   Layer: {}", this);
                loggerCore.info("   Manager: {}", entry.value);
                loggerCore.info("   Location: {}", entry.location);
            } else {
                loggerCore.error("Attempting to register an incompatible container!");
                loggerCore.error("  Layer: {}", this);
                loggerCore.error("  Manager: {}", entry.value);
                loggerCore.error("  Location: {}", entry.location);
            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public ShapelessLocationMap<IArmModelManager<?, I>> castModelManagers(List<SLContainer<IArmModelManager<?, ?>>> list) {
        ShapelessLocationMap<IArmModelManager<?, I>> ret = new ShapelessLocationMap<>();
        Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
        Class<?> clazz = this.getClass();

        SLContainer<IArmModelManager<?, ?>> absoluteEntry = null;

        for (SLContainer<IArmModelManager<?, ?>> entry : CoreUtils.sortPrioritisedList(list, (v) -> v.value.priority())) {
            if (entry.value.targetLayer().isAssignableFrom(clazz)) {
                if (entry.location.isAbsoluteShapeless()) {
                    if (absoluteEntry == null || absoluteEntry.value.priority().toInt() < entry.value.priority().toInt()) {
                        absoluteEntry = entry;
                    }
                    continue;
                }
                ret.put(entry.location, (IArmModelManager<?, I>) entry.value);
                loggerCore.info("Registered model manager");
                loggerCore.info("   Layer: {}", this);
                loggerCore.info("   Manager: {}", entry.value);
                loggerCore.info("   Location: {}", entry.location);
            } else {
                loggerCore.error("Attempting to register an incompatible model manager!");
                loggerCore.error("  Layer: {}", this);
                loggerCore.error("  Manager: {}", entry.value);
                loggerCore.error("  Location: {}", entry.location);
            }
        }

        if (absoluteEntry != null) {
            ret.put(absoluteEntry.location, (IArmModelManager<?, I>) absoluteEntry.value);
            loggerCore.info("Registered absolute model manager");
            loggerCore.info("   Layer: {}", this);
            loggerCore.info("   Manager: {}", absoluteEntry.value);
            loggerCore.info("   Location: {}", absoluteEntry.location);
        }

        return ret;
    }

    public void tryInit(E engine, IAAModContainer mod) {
        this.engine = engine;
        this.mod = mod;
        this.modelManagers = this.castModelManagers(this.initModelManagers());
        List<SLContainer<IArmModelRenderContainer<?, ?>>> containers = this.initRenderContainers();
        this.renderContainers = this.castContainers(containers);
        this.dynRenderContainers = this.castDynamicContainers(containers);
        this.blackList = this.initBlackList();
        this.killingArmor = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public void tryTick(E engine) {
        S chestPlate = this.currentChestPlate();

        if (chestPlate.isEmpty() || this.killingArmor.contains(chestPlate)) {
            this.chestPlate = this.emptyStack();
            this.render = false;
            return;
        }

        if (!this.chestPlate.isNew(chestPlate)) {
            if (this.modelManager != null) {
                this.modelManager.update((I) this);
            }
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
            Logger loggerCore = this.mod.logger("ARMOREDARMS-CORE");
            loggerCore.warn("Could not find a suitable render components!");
            loggerCore.warn("   Layer: {}", this);
            loggerCore.warn("   Engine: {}", engine);
            loggerCore.warn("   Chest plate location: {}", chestPlate.location());
            loggerCore.warn("   Model manager: {}", this.modelManager);
            loggerCore.warn("   Model renderer: {}", this.model);
        }

        if (this.modelManager != null) {
            this.modelManager.update((I) this);
        }
    }

    @SuppressWarnings("unchecked")
    public void tryRender(E engine, EnumHandSideAA handSide) {
        if (this.modelManager != null && this.model != null) {
            this.modelManager.render((I) this, (IArmModelRenderer) this.model, handSide);
        }
    }
}
