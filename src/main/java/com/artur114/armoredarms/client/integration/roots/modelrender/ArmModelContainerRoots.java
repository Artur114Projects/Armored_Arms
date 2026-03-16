package com.artur114.armoredarms.client.integration.roots.modelrender;

import com.artur114.armoredarms.client.integration.galaxyspace.modelrender.ArmModelRenderGSOBJ;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import epicsquid.mysticallib.client.model.ModelArmorBase;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;

public class ArmModelContainerRoots implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {

        if (manager.model instanceof ModelArmorBase) {
            return new ArmModelRendererRoots((ModelArmorBase) manager.model, manager.texture);
        }

        return new ArmModelRendererArmor(manager.model, manager.texture);
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
        return Priority.NORMAL;
    }
}
