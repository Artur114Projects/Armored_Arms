package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelRenderContextOverlay extends AbsModelRenderContext {
    private final ResourceLocation resource;

    public ModelRenderContextOverlay(ResourceLocation resource, IPriority priority) {
        super(priority);
        this.resource = resource;
    }

    public ModelRenderContextOverlay(ResourceLocation resource) {
        this(resource, Priority.NORMAL);
    }

    @Override
    public VertexConsumer vertexConsumer() {
        return this.buffer.getBuffer(RenderType.armorCutoutNoCull(this.resource));
    }

    @Override
    public float alpha() {
        return 1.0F;
    }

    @Override
    public float blue() {
        return 1.0F;
    }

    @Override
    public float green() {
        return 1.0F;
    }

    @Override
    public float red() {
        return 1.0F;
    }
}
