package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraftforge.event.TickEvent;

import java.util.Map;

public class ArmRenderEngineForge extends AbstractRenderEngineForge<ArmRenderEngineForge, AbstractRenderPipelineForge<?>> {

    @Override
    public void render(AbstractRenderPipelineForge<?> context) {
        this.renderAllLayers(context.renderContext.arm);
    }

    @Override
    public void tick(AbstractRenderPipelineForge<?> context) {
        this.render = this.updateAllLayers();
    }

    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        return Map.of();
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
