package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineMixin;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

public class ArmRenderEngineMixin extends AbstractRenderEngineForge<ArmRenderEngineMixin, ArmRenderPipelineMixin>{
    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineMixin.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public boolean onLayerRendering(IArmRenderLayer<ArmRenderEngineMixin> layer, EnumHandSideAA side) {
        return !MinecraftForge.EVENT_BUS.post(new ArmLayerRenderingEvent(layer, side));
    }

    @Override
    public void render(ArmRenderPipelineMixin context) {
        this.renderAllLayers(context.renderContext.armSide());

        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.setCanceled(true);
        }
    }

    @Override
    public void tick(ArmRenderPipelineMixin context) {
        this.render = this.updateAllLayers();
    }

    @Override
    public Class<ArmRenderPipelineMixin> targetPipeline() {
        return ArmRenderPipelineMixin.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
