package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends AbstractRenderPipelineForge<?>> extends AbstractRenderEngine<E, P> {
    public final Minecraft mc = Minecraft.getInstance();
    public ArmRenderContext renderContext = null;
    private HumanoidArmorModel<AbstractClientPlayer> actualHumanoidModel = null;
    private double lastModelSize = Double.MIN_VALUE;

    public HumanoidArmorModel<AbstractClientPlayer> actualHumanoidModel() {
        if (this.lastModelSize != AAConfig.vanillaArmorModelSize || this.actualHumanoidModel == null) {
            this.actualHumanoidModel = this.createHumanoidArmorModel(this.lastModelSize = AAConfig.vanillaArmorModelSize);
        }
        return this.actualHumanoidModel;
    }

    public <T extends LivingEntity> HumanoidArmorModel<T> createHumanoidArmorModel(double cubeDeform) {
        return new HumanoidArmorModel<>(HumanoidArmorModel.createBodyLayer(new CubeDeformation((float) cubeDeform)).getRoot().bake(64, 32));
    }

    @Override
    public void tryRender(P context) {
        this.renderContext = context.renderContext;
        super.tryRender(context);
    }

    @Override
    public void tryTick(P context) {
        super.tryTick(context);
    }

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderEngineForge<?, ?>> clazz() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }
}
