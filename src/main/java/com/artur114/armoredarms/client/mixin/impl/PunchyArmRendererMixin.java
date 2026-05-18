package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.client.mixin.IPunchyArmRenderProvider;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.util.ObjectBuff;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchy.client.render.PunchyArmRenderer;

@Mixin(value = PunchyArmRenderer.class, remap = false)
public class PunchyArmRendererMixin implements IPunchyArmRenderProvider {
    @Shadow(remap = false)
    private static void copyMatrixToSleeve(ModelPart arm, ModelPart sleeve) {}
    @Shadow(remap = false)
    private static void applyArmMeshOffsets(PoseStack poseStack, boolean leftArm) {}
    @Shadow(remap = false)
    private static void applyFreezeShake(PoseStack poseStack, AbstractClientPlayer player, float partialTicks) {}
    @Shadow(remap = false)
    private static void renderLavaHandOverlay(ModelPart armPart, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm arm, float partialTicks) {}
    @Shadow(remap = false)
    private static void renderFreezeOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, float partialTicks) {}
    @Shadow(remap = false)
    private static void renderMudOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {}
    @Shadow(remap = false)
    private static void renderSweatOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {}
    @Shadow(remap = false)
    private static void renderFlameOnArm(PoseStack poseStack, MultiBufferSource buffer) {}
    @Shadow(remap = false)
    private static boolean isSlim(AbstractClientPlayer player) {return false;}
    @Shadow(remap = false)
    private static PlayerModel getFirstPersonModel(boolean slim) {return null;}

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private static void renderArm(PlayerModel playerModel, AbstractClientPlayer player, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, ResourceLocation texture, boolean slim, float partialTicks, CallbackInfo ci) {
        ObjectBuff args = ArmoredArmsApi.renderArgs();
        args.writeObject("FORCED_RENDER");
        args.writeObject(playerModel);
        args.writeFloat(partialTicks);
        args.writeBoolean(slim);
        args.reset();
        AAUtils.renderArmPlayerRenderer(arm, poseStack, buffer, combinedLight, player);
        ci.cancel();
    }

    @Override
    public void armoredarms$copyMatrixToSleeve(ModelPart arm, ModelPart sleeve) {
        copyMatrixToSleeve(arm, sleeve);
    }

    @Override
    public void armoredarms$applyArmMeshOffsets(PoseStack poseStack, boolean leftArm) {
        applyArmMeshOffsets(poseStack, leftArm);
    }

    @Override
    public void armoredarms$applyFreezeShake(PoseStack poseStack, AbstractClientPlayer player, float partialTicks) {
        applyFreezeShake(poseStack, player, partialTicks);
    }

    @Override
    public void armoredarms$renderLavaHandOverlay(ModelPart armPart, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm arm, float partialTicks) {
        renderLavaHandOverlay(armPart, poseStack, buffer, combinedLight, player, arm, partialTicks);
    }

    @Override
    public void armoredarms$renderFreezeOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, float partialTicks) {
        renderFreezeOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight, player, partialTicks);
    }

    @Override
    public void armoredarms$renderMudOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        renderMudOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
    }

    @Override
    public void armoredarms$renderSweatOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
        renderSweatOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
    }

    @Override
    public void armoredarms$renderFlameOnArm(PoseStack poseStack, MultiBufferSource buffer) {
        renderFlameOnArm(poseStack, buffer);
    }

    @Override
    public PlayerModel<?> armoredarms$punchyFirstPersonModel(AbstractClientPlayer player) {
        return getFirstPersonModel(isSlim(player));
    }
}
