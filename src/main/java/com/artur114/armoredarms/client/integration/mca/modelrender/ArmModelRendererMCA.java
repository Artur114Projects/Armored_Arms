package com.artur114.armoredarms.client.integration.mca.modelrender;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.modelrender.INeedRenderProvider;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Reflector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import forge.net.mca.MCAClient;
import forge.net.mca.client.model.CommonVillagerModel;
import forge.net.mca.client.model.PlayerEntityExtendedModel;
import forge.net.mca.client.render.layer.ClothingLayer;
import forge.net.mca.client.render.layer.SkinLayer;
import forge.net.mca.client.render.layer.VillagerLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ArmModelRendererMCA implements IArmModelRenderer<ArmModelManagerPlayer>, INeedRenderProvider {
    private final ClothingLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> mca$clothingLayer;
    private final SkinLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> mca$skinLayer;

    public ArmModelRendererMCA(ArmModelManagerPlayer manager) {
        this.mca$clothingLayer = Reflector.getPrivateField(manager.renderPlayer, "mca$clothingLayer");
        this.mca$skinLayer = Reflector.getPrivateField(manager.renderPlayer, "mca$skinLayer");
    }

    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        if (context.mc.player == null) return;

        if (!MCAClient.renderArms(context.mc.player.getUUID(), side == EnumHandSideAA.RIGHT ? "right_arm" : "left_arm")) {
            return;
        }

        this.mca$renderCustomArm(context, context.rawContext.poseStack, context.rawContext.multiBufferSource, context.rawContext.packedLight, context.mc.player, this.mca$skinLayer.model.rightArm, this.mca$skinLayer.model.rightSleeve, this.mca$skinLayer);
        this.mca$renderCustomArm(context, context.rawContext.poseStack, context.rawContext.multiBufferSource, context.rawContext.packedLight, context.mc.player, this.mca$clothingLayer.model.rightArm, this.mca$clothingLayer.model.rightSleeve, this.mca$clothingLayer);
    }

    private void mca$renderCustomArm(ArmModelManagerPlayer context, PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, VillagerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer) {
        PlayerEntityExtendedModel<AbstractClientPlayer> model = (PlayerEntityExtendedModel<AbstractClientPlayer>) layer.model;
        context.prepareModel(model);
        model.applyVillagerDimensions(CommonVillagerModel.getVillager(player), player.isCrouching());
        ResourceLocation skin = layer.getSkin(player);
        if (layer.canUse(skin)) {
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(skin));
            float[] color = layer.getColor(player, 0.0F);
            arm.xRot = 0.0F;
            arm.render(matrices, buffer, light, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
            sleeve.xRot = 0.0F; //TODO: Сделать обработку одежды
            sleeve.render(matrices, buffer, light, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
        }
    }

    @Override
    public boolean needRender(IArmRenderEngine<?> engine, boolean renderEngineState) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            return MCAClient.renderArms(mc.player.getUUID(), "left_arm") || MCAClient.renderArms(mc.player.getUUID(), "right_arm");
        }
        return false;
    }
}
