package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.event.TickEvent;

public abstract class AbstractRenderPipelineForge<I extends AbstractRenderPipelineForge<?>> extends AbstractRenderPipeline<I> {
    public ArmRenderContext renderContext = new ArmRenderContext();
    public final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderPipelineForge<?>> clazzz() {
        return (Class<AbstractRenderPipelineForge<?>>) (Class<?>) AbstractRenderPipelineForge.class;
    }
}
