package com.artur114.armoredarms.client.integration.lotr.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import lotr.client.model.LOTRModelSwanChestplate;

public class ArmModelContainerLotr implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    private LOTRModelSwanChestplate model = null;
    private float lastModelSize = -1.0F;

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        if (this.lastModelSize != AAConfig.vanillaArmorModelSize) {
            this.model = new LOTRModelSwanChestplate(this.lastModelSize = (float) AAConfig.vanillaArmorModelSize);
            this.model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, manager.player);
        }

        return new ArmModelRendererArmor(this.model, manager.texture);
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
