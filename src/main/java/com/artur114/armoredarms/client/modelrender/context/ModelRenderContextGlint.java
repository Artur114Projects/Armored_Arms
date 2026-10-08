package com.artur114.armoredarms.client.modelrender.context;

import com.artur114.armoredarms.client.util.AbsModelRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

public class ModelRenderContextGlint extends AbsModelRenderContext {
    public ModelRenderContextGlint(IPriority priority) {
        super(priority);
    }

    public ModelRenderContextGlint() {}

    @Override
    public VertexConsumer vertexConsumer() {
        return this.buffer.getBuffer(RenderType.armorEntityGlint());
    }

    @Override
    public int rgba() {
        return 0xFFFFFFFF;
    }
}
