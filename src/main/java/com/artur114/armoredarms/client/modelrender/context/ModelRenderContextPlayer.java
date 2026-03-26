package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class ModelRenderContextPlayer extends AbsModelRenderContext {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public VertexConsumer vertexConsumer() {
        if (this.mc.player == null) return null;
        return this.buffer.getBuffer(RenderType.entitySolid(this.mc.player.getSkinTextureLocation()));
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
