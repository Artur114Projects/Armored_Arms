package com.artur114.armoredarms.client.integration.dragonblockc.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Reflector;

import java.util.Random;

public class ArmModelContainerDragonShitC implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        return new ArmModelRendererDragonShitC(manager.model, manager.texture);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return false;
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
        return Priority.NORMAL;
    }
}
