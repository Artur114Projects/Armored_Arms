package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.event.TickEvent;

public abstract class AbstractRenderPipelineForge<I extends AbstractRenderPipelineForge<?>> extends AbstractRenderPipeline<I> {
    public ArmRenderContext renderContext = new ArmRenderContext();
    public final Minecraft mc = Minecraft.getInstance();


    @Override
    public boolean canWork(IAAModContainer mod) {
        return AAConfig.Baked.pipelinesPriority.containsKey(this.clazz());
    }

    @Override
    public IPriority priority() {
        return AAConfig.Baked.pipelinesPriority.get(this.clazz());
    }

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderPipelineForge<?>> clazzz() {
        return (Class<AbstractRenderPipelineForge<?>>) (Class<?>) AbstractRenderPipelineForge.class;
    }
}
