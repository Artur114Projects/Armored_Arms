package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.RenderException;

import java.util.Collections;
import java.util.Map;

public class ArmRenderEngineCleanRoom extends AbstractRenderEngineForge<ArmRenderEngineCleanRoom, ArmRenderPipelineCleanRoom>{
    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineForge.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public void render(ArmRenderPipelineCleanRoom context) {
        EnumHandSideAA handSide = AAUtils.fromMc(context.renderContext.getArm());
        for (IArmRenderLayer<ArmRenderEngineCleanRoom> layer : this.sortedLayers) {
            if (layer.needRender(this, this.render)) {
                try {
                    if (!this.mod.post(new ArmLayerRenderingEvent(layer, handSide))) {
                        layer.render(this, handSide);
                    }
                } catch (RenderException rm) {
                    throw rm;
                } catch (Throwable tr) {
                    throw new RenderException(tr).setComponent(layer);
                }
            }
        }
        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.setCanceled(true);
        }
    }

    @Override
    public void tick(ArmRenderPipelineCleanRoom context) {
        boolean render = false;

        for (IArmRenderLayer<ArmRenderEngineCleanRoom> layer : this.sortedLayers) {

            try {
                layer.update(this);
            } catch (RenderException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RenderException(tr).setComponent(layer);
            }

            render |= layer.needRender(this, render);
        }

        this.render = render;
    }

    @Override
    public Class<ArmRenderPipelineCleanRoom> targetPipeline() {
        return ArmRenderPipelineCleanRoom.class;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
