package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArmModelContainerPlayer implements IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> {
    private final IConstructor creator;
    private final IPriority priority;


    public ArmModelContainerPlayer(Class<? extends IArmModelRenderer<ArmModelManagerPlayer>> clazz) {
        this(clazz, Priority.NORMAL);
    }

    public ArmModelContainerPlayer(Class<? extends IArmModelRenderer<ArmModelManagerPlayer>> clazz, IPriority priority) {
        this.priority = priority;
        try {
            Constructor<? extends IArmModelRenderer<ArmModelManagerPlayer>> constructor = clazz.getDeclaredConstructor(RenderPlayer.class);
            constructor.setAccessible(true);
            creator = (renderPlayer) -> {
                try {
                    return constructor.newInstance(renderPlayer);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IArmModelRenderer<ArmModelManagerPlayer> create(ArmModelManagerPlayer manager) {
        return this.creator.create(manager.renderPlayer);
    }

    @Override
    public boolean needWork(ArmModelManagerPlayer manager) {
        return true;
    }

    @Override
    public Class<ArmModelManagerPlayer> targetManager() {
        return ArmModelManagerPlayer.class;
    }

    @Override
    public Class<ArmRenderLayerHand> targetLayer() {
        return ArmRenderLayerHand.class;
    }

    @Override
    public IPriority priority() {
        return this.priority;
    }

    private interface IConstructor {
        IArmModelRenderer<ArmModelManagerPlayer> create(RenderPlayer renderPlayer);
    }
}
