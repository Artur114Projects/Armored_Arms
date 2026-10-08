package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPrioritised;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;

public interface IModelRenderContext extends IPrioritised {
    void prepare(MultiBufferSource buffer, PoseStack poseStack, int packedLight);
    MultiBufferSource multiBuffer();
    VertexConsumer vertexConsumer();
    PoseStack poseStack();
    int packedOverlay();
    int packedLight();
    int rgba();

    default void renderPart(ModelPart part) {
        if (this.poseStack() != null && this.vertexConsumer() != null) {
            part.render(this.poseStack(), this.vertexConsumer(), this.packedLight(), this.packedOverlay(), this.rgba());
        }
    }
}
