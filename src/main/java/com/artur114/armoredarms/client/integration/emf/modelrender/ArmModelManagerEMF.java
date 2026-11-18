package com.artur114.armoredarms.client.integration.emf.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;

public class ArmModelManagerEMF extends ArmModelManagerPlayer {
    @Override
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer,
            EnumHandSideAA side) {
        try (EMFHandContext ignored = EMFHandContext.open()) {
            super.render(layer, renderer, side);
        }
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}