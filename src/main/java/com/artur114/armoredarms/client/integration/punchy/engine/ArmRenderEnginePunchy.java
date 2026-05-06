package com.artur114.armoredarms.client.integration.punchy.engine;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.integration.punchy.pipeline.ArmRenderPipelinePunchy;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.mixin.IPunchyArmRenderProvider;
import com.artur114.armoredarms.client.mixin.RenderArmPunchyMixinEvent;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import punchy.client.render.PunchyArmRenderer;
import punchy.config.PunchyConfig;

import java.util.Map;

public class ArmRenderEnginePunchy extends AbstractRenderEngineForge<ArmRenderEnginePunchy, ArmRenderPipelinePunchy> {
    /**
        @see com.artur114.armoredarms.client.mixin.impl.PunchyArmRendererMixin
     */
    private final IPunchyArmRenderProvider provider = IPunchyArmRenderProvider.INSTANCE;

    @Override
    public void render(ArmRenderPipelinePunchy context) {
        RenderArmPunchyMixinEvent renderContext = context.context;
        PlayerModel<?> playerModel = renderContext.playerModel();
        AbstractClientPlayer player = renderContext.player();
        float partialTicks = renderContext.partialTicks();
        int combinedLight = renderContext.combinedLight();
        MultiBufferSource buffer = renderContext.buffer();
        PoseStack poseStack = renderContext.poseStack();
        HumanoidArm arm = renderContext.arm();
        boolean slim = renderContext.slim();

        ModelPart armPart = arm == HumanoidArm.LEFT ? playerModel.leftArm : playerModel.rightArm;
        ModelPart sleevePart = arm == HumanoidArm.LEFT ? playerModel.leftSleeve : playerModel.rightSleeve;
        boolean isLeft = arm == HumanoidArm.LEFT;
        this.provider.armoredarms$copyMatrixToSleeve(armPart, sleevePart);
        poseStack.pushPose();
        if (slim) {
            poseStack.translate((isLeft ? 1.0F : -1.0F) * 0.5F / 16.0F, 0.0F, 0.0F);
        }

        this.provider.armoredarms$applyArmMeshOffsets(poseStack, isLeft);
        this.provider.armoredarms$applyFreezeShake(poseStack, player, partialTicks);
        this.renderAllLayers(AAUtils.fromMc(renderContext.arm()));
        this.provider.armoredarms$renderLavaHandOverlay(armPart, poseStack, buffer, combinedLight, player, isLeft ? HumanoidArm.LEFT : HumanoidArm.RIGHT, partialTicks);
        this.provider.armoredarms$renderFreezeOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight, player, partialTicks);
        this.provider.armoredarms$renderMudOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
        this.provider.armoredarms$renderSweatOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
        if (player.isOnFire()) {
            poseStack.pushPose();
            armPart.translateAndRotate(poseStack);
            if (!PunchyConfig.disableEnhancedFireArmEffects()) {
                this.provider.armoredarms$renderFlameOnArm(poseStack, buffer);
            }

            poseStack.popPose();
        }

        poseStack.popPose();

        if (!this.sortedLayers.isEmpty()) {
            renderContext.setCanceled(true);
        }
    }

    @Override
    public void tick(ArmRenderPipelinePunchy context) {
        this.cleanUpLayers();
        this.render = this.updateAllLayers();
    }

    @Override
    public boolean onLayerRendering(IArmRenderLayer<ArmRenderEnginePunchy> layer, EnumHandSideAA side) {
        return !this.mod.post(new ArmLayerRenderingEvent(layer, side));
    }

    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineForge.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public Class<ArmRenderPipelinePunchy> targetPipeline() {
        return ArmRenderPipelinePunchy.class;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
