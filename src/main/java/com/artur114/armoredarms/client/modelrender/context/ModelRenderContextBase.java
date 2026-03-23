package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;

public class ModelRenderContextBase extends AbsModelRenderContext {
    private float r = 1.0F, g = 1.0F, b = 1.0F;
    private final ResourceLocation resource;

    public ModelRenderContextBase(ResourceLocation resource, ItemStackAA armorItem, IPriority priority) {
        super(priority);
        this.resource = resource;
        if (armorItem.item() instanceof DyeableLeatherItem dye) {
            int i = dye.getColor(armorItem.stack());
            this.r = (float)(i >> 16 & 255) / 255.0F;
            this.g = (float)(i >> 8 & 255) / 255.0F;
            this.b = (float)(i & 255) / 255.0F;
        }
    }

    public ModelRenderContextBase(ResourceLocation resource, ItemStackAA armorItem) {
        this(resource, armorItem, Priority.NORMAL);
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
        return this.b;
    }

    @Override
    public float green() {
        return this.g;
    }

    @Override
    public float red() {
        return this.r;
    }
}
