package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends AbstractRenderPipelineForge<?>> extends AbstractRenderEngine<E, P> {
    public final Minecraft mc = Minecraft.getInstance();
    public ArmRenderContext renderContext = null;

    @Override
    public void tryRender(P context) {
        this.renderContext = context.renderContext;
        super.tryRender(context);
    }

    @Override
    public void tryTick(P context) {
        super.tryTick(context);
    }

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderEngineForge<?, ?>> clazz() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }
}
