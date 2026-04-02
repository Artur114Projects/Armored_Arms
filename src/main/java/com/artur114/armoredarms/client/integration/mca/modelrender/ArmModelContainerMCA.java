package com.artur114.armoredarms.client.integration.mca.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import forge.net.mca.MCAClient;

public class ArmModelContainerMCA implements IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> {
    @Override
    public IArmModelRenderer<ArmModelManagerPlayer> create(ArmModelManagerPlayer manager) {
        return new ArmModelRendererMCA(manager);
    }

    @Override
    public boolean needWork(ArmModelManagerPlayer manager) {
        if (manager.mc.player == null) return false;
        return MCAClient.renderArms(manager.mc.player.getUUID(), "left_arm") || MCAClient.renderArms(manager.mc.player.getUUID(), "right_arm");
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
        return Priority.HIGHEST;
    }
}
