package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.CoreUtils;
import com.artur114.armoredarms.core.util.ITexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MultiModelRenderContext implements Iterable<IModelRenderContext> {
    private final List<IModelRenderContext> contextList;

    public MultiModelRenderContext(IModelRenderContext... context) {
        this(List.of(context));
    }

    public MultiModelRenderContext(Collection<IModelRenderContext> context) {
        this.contextList = CoreUtils.sortPrioritisedList(context);
    }

    public MultiModelRenderContext prepare(MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        for (IModelRenderContext context : this.contextList) {
            context.prepare(buffer, poseStack, packedLight);
        }
        return this;
    }

    public IModelRenderContext[] context() {
        return this.contextList.toArray(new IModelRenderContext[0]);
    }

    @Override
    public @NotNull Iterator<IModelRenderContext> iterator() {
        return this.contextList.iterator();
    }
}
