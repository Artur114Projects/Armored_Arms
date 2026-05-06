package com.artur114.armoredarms.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import punchy.client.render.PunchyArmRenderer;

public interface IPunchyArmRenderProvider {
    IPunchyArmRenderProvider INSTANCE = (IPunchyArmRenderProvider) new PunchyArmRenderer();

    void armoredarms$copyMatrixToSleeve(ModelPart arm, ModelPart sleeve);
    void armoredarms$applyArmMeshOffsets(PoseStack poseStack, boolean leftArm);
    void armoredarms$applyFreezeShake(PoseStack poseStack, AbstractClientPlayer player, float partialTicks);
    void armoredarms$renderLavaHandOverlay(ModelPart armPart, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm arm, float partialTicks);
    void armoredarms$renderFreezeOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, float partialTicks);
    void armoredarms$renderMudOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight);
    void armoredarms$renderSweatOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight);
    void armoredarms$renderFlameOnArm(PoseStack poseStack, MultiBufferSource buffer);
    PlayerModel<?> armoredarms$punchyFirstPersonModel(AbstractClientPlayer player);
}
