package com.artur114.armoredarms.client.modelrender.player;


import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

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
            Constructor<? extends IArmModelRenderer<ArmModelManagerPlayer>> constructor = clazz.getDeclaredConstructor(MultiModelRenderContext.class);
            constructor.setAccessible(true);
            this.creator = (context) -> {
                try {
                    return constructor.newInstance(context);
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
        return this.creator.create(manager.context);
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
        IArmModelRenderer<ArmModelManagerPlayer> create(MultiModelRenderContext context);
    }
}