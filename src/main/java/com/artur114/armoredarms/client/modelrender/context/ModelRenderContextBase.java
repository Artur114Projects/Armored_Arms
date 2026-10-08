package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelRenderContextBase extends AbsModelRenderContext {
    private final ResourceLocation resource;
    private final int color;

    public ModelRenderContextBase(ResourceLocation resource, int color, IPriority priority) {
        super(priority);
        this.resource = resource;
        this.color = color;
    }

    public ModelRenderContextBase(ResourceLocation resource, int color) {
        this(resource, color, Priority.NORMAL);
    }

    @Override
    public VertexConsumer vertexConsumer() {
        return this.buffer.getBuffer(RenderType.armorCutoutNoCull(this.resource));
    }

    @Override
    public int rgba() {
        return this.color;
    }
}
