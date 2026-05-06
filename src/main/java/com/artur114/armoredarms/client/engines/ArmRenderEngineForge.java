package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.ArmsBone;
import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraftforge.event.TickEvent;

import java.util.Map;

public class ArmRenderEngineForge extends AbstractRenderEngineForge<ArmRenderEngineForge, AbstractRenderPipelineForge<?>> {

    @Override
    public void render(AbstractRenderPipelineForge<?> context) {
        this.renderAllLayers(context.renderContext.arm);

        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.cancel();
        }
    }

    @Override
    public void tick(AbstractRenderPipelineForge<?> context) {
        this.cleanUpLayers();
        this.render = this.updateAllLayers();
    }

    @Override
    public boolean onLayerRendering(IArmRenderLayer<ArmRenderEngineForge> layer, EnumHandSideAA side) {
        return !this.mod.post(new ArmLayerRenderingEvent(layer, side));
    }

    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineForge.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public Class<AbstractRenderPipelineForge<?>> targetPipeline() {
        return AbstractRenderPipelineForge.clazzz();
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
