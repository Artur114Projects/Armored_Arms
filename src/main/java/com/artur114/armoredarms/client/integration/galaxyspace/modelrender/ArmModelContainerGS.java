package com.artur114.armoredarms.client.integration.galaxyspace.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.TextureEnchant;
import com.artur114.armoredarms.client.util.TextureLocationBlocks;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel;

public class ArmModelContainerGS implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    private static final IMultiTexture TEXTURE_ENCHANTED = new MultiTexture(TextureLocationBlocks.INSTANCE, TextureEnchant.FIRST, TextureEnchant.SECOND);
    private static final IMultiTexture TEXTURE = new MultiTexture(TextureLocationBlocks.INSTANCE);

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {

        if (manager.model instanceof ItemSpaceSuitModel) {
            return new ArmModelRenderGSOBJ((ItemSpaceSuitModel) manager.model, manager.stack.item().hasEffect(manager.stack.stack()) ? TEXTURE_ENCHANTED : TEXTURE);
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
