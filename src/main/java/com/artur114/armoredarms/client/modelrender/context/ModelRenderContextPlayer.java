package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class ModelRenderContextPlayer extends AbsModelRenderContext {
    private final Minecraft mc = Minecraft.getInstance();
    private final boolean wear;

    public ModelRenderContextPlayer(boolean wear, IPriority priority) {
        super(priority);
        this.wear = wear;
    }

    public ModelRenderContextPlayer(boolean wear) {
        this(wear, Priority.NORMAL);
    }


    @Override
    public VertexConsumer vertexConsumer() {
        if (this.mc.player == null) return null;
        if (this.wear) {
            return this.buffer.getBuffer(RenderType.entityTranslucent(this.mc.player.getSkinTextureLocation()));
        } else {
            return this.buffer.getBuffer(RenderType.entitySolid(this.mc.player.getSkinTextureLocation()));
        }
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
