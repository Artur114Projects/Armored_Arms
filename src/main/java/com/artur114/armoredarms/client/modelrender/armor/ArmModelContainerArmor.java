package com.artur114.armoredarms.client.modelrender.armor;


import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.HumanoidModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArmModelContainerArmor implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    private final IConstructor creator;
    private final IPriority priority;

    public ArmModelContainerArmor(Class<? extends IArmModelRenderer<ArmModelManagerArmor>> clazz) {
        this(clazz, Priority.NORMAL);
    }

    public ArmModelContainerArmor(Class<? extends IArmModelRenderer<ArmModelManagerArmor>> clazz, IPriority priority) {
        this.priority = priority;
        try {
            Constructor<? extends IArmModelRenderer<ArmModelManagerArmor>> constructor = clazz.getDeclaredConstructor(MultiModelRenderContext.class, HumanoidModel.class);
            constructor.setAccessible(true);
            this.creator = (context, model) -> {
                try {
                    return constructor.newInstance(context, model);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        return this.creator.create(manager.context, (HumanoidModel<?>) manager.model);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return manager.model instanceof HumanoidModel<?>;
    }

    @Override
    public Class<ArmModelManagerArmor> targetManager() {
        return ArmModelManagerArmor.class;
    }

    @Override
    public Class<ArmRenderLayerArmor> targetLayer() {
        return ArmRenderLayerArmor.class;
    }

    @Override
    public IPriority priority() {
        return this.priority;
    }

    private interface IConstructor {
        IArmModelRenderer<ArmModelManagerArmor> create(MultiModelRenderContext context, HumanoidModel<?> model);
    }
}