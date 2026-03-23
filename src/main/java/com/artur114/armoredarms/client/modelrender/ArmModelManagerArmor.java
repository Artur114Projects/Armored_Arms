package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;

public class ArmModelManagerArmor implements IArmModelManager<ArmModelManagerArmor, ArmRenderLayerArmor> {
    public MultiModelRenderContext context = null;
    public boolean deactivated = false;

    @Override
    public void update(ArmRenderLayerArmor layer) {}

    @Override
    public void render(ArmRenderLayerArmor layer, IArmModelRenderer<ArmModelManagerArmor> renderer, EnumHandSideAA side) {
        if (this.deactivated) {
            return;
        }

        this.context.prepare(layer.context.multiBufferSource, layer.context.poseStack, layer.context.packedLight);

    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> cacheRenderer(ArmRenderLayerArmor layer, IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> container) {

        return null;
    }

    @Override
    public Class<ArmRenderLayerArmor> targetLayer() {
        return ArmRenderLayerArmor.class;
    }

    @Override
    public Class<ArmModelManagerArmor> clazz() {
        return ArmModelManagerArmor.class;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
