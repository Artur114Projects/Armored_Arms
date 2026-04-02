package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public abstract class AbsModelRenderContext implements IModelRenderContext {
    protected final IPriority priority;
    protected MultiBufferSource buffer;
    protected PoseStack poseStack;
    protected int packedLight;

    public AbsModelRenderContext(IPriority priority) {
        this.priority = priority;
    }

    public AbsModelRenderContext() {
        this(Priority.NORMAL);
    }

    @Override
    public void prepare(MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        this.packedLight = packedLight;
        this.poseStack = poseStack;
        this.buffer = buffer;
    }

    @Override
    public IPriority priority() {
        return this.priority;
    }

    @Override
    public MultiBufferSource multiBuffer() {
        return this.buffer;
    }

    @Override
    public PoseStack poseStack() {
        return this.poseStack;
    }

    @Override
    public int packedOverlay() {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public int packedLight() {
        return this.packedLight;
    }
}
