package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.minecraft.client.model.ModelBiped;

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
            Constructor<? extends IArmModelRenderer<ArmModelManagerArmor>> constructor = clazz.getDeclaredConstructor(ModelBiped.class, IMultiTexture.class);
            constructor.setAccessible(true);
            creator = (mb, texture) -> {
                try {
                    return constructor.newInstance(mb, texture);
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
        return this.creator.create(manager.model, manager.texture);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return true;
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
        IArmModelRenderer<ArmModelManagerArmor> create(ModelBiped mb, IMultiTexture texture);
    }
}
