package com.artur114.armoredarms.client.integration.punchy.engine;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.mixin.IPunchyArmRenderProvider;
import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ObjectBuff;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import punchy.config.PunchyConfig;

import java.util.Map;

public class ArmRenderEnginePunchy extends AbstractRenderEngineForge<ArmRenderEnginePunchy, AbstractRenderPipelineForge<?>> {
    /**
        @see com.artur114.armoredarms.client.mixin.impl.PunchyArmRendererMixin
     */
    private final IPunchyArmRenderProvider provider = IPunchyArmRenderProvider.INSTANCE;

    @Override
    public void render(AbstractRenderPipelineForge<?> context) {
        ObjectBuff data = context.renderArgs();

        PlayerModel<?> playerModel = null;
        float partialTicks = -1;
        boolean slim = false;
        boolean hasContext = true;

        try {
            data.jump(1);
            playerModel = data.readObject(PlayerModel.class);
            partialTicks = data.readFloat();
            slim = data.readBoolean();
        } catch (Exception e) {
            hasContext = false;
        }

        if (hasContext) {
            AbstractClientPlayer player = this.renderContext.player;
            int combinedLight = this.renderContext.packedLight;
            MultiBufferSource buffer = this.renderContext.multiBufferSource;
            PoseStack poseStack = this.renderContext.poseStack;
            EnumHandSideAA arm = this.renderContext.arm;

            ModelPart armPart = arm == EnumHandSideAA.LEFT ? playerModel.leftArm : playerModel.rightArm;
            ModelPart sleevePart = arm == EnumHandSideAA.LEFT ? playerModel.leftSleeve : playerModel.rightSleeve;
            boolean isLeft = arm == EnumHandSideAA.LEFT;
            this.provider.armoredarms$copyMatrixToSleeve(armPart, sleevePart);
            poseStack.pushPose();
            if (slim) {
                poseStack.translate((isLeft ? 1.0F : -1.0F) * 0.5F / 16.0F, 0.0F, 0.0F);
            }

            this.provider.armoredarms$applyArmMeshOffsets(poseStack, isLeft);
            this.provider.armoredarms$applyFreezeShake(poseStack, player, partialTicks);
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

            this.renderAllLayers(arm);

            poseStack.popPose();
        } else {
            this.renderAllLayers(context.renderContext.arm);
        }

        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.cancel();
        }
    }

    @Override
    public void tick(AbstractRenderPipelineForge<?> context) {
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
    public Class<AbstractRenderPipelineForge<?>> targetPipeline() {
        return AbstractRenderPipelineForge.clazzz();
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
