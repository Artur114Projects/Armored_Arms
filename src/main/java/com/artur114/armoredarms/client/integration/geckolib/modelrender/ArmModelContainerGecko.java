package com.artur114.armoredarms.client.integration.geckolib.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArmModelContainerGecko implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        return new ArmModelRendererGecko(manager.context, (GeoArmorRenderer<?>) manager.model);
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return manager.model instanceof GeoArmorRenderer<?>;
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
        return Priority.HIGH;
    }
}
