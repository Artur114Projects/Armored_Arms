package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.main.AAConfig;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends AbstractRenderPipelineForge<?>> extends AbstractRenderEngine<E, P> {
    private Float2ObjectMap<HumanoidArmorModel<AbstractClientPlayer>> actualHumanoidModels = null;
    public final Minecraft mc = Minecraft.getInstance();
    private double lastModelSize = Double.MIN_VALUE;
    public ArmRenderContext renderContext = null;

    public HumanoidArmorModel<AbstractClientPlayer> actualHumanoidModel(float delta) {
        if (this.actualHumanoidModels == null) {
            this.actualHumanoidModels = new Float2ObjectOpenHashMap<>();
        }

        if (this.lastModelSize != AAConfig.vanillaArmorModelSize) {
            this.actualHumanoidModels.clear(); this.lastModelSize = AAConfig.vanillaArmorModelSize;
        }

        HumanoidArmorModel<AbstractClientPlayer> model = this.actualHumanoidModels.get(delta);

        if (model == null) {
            model = this.createHumanoidArmorModel(AAConfig.vanillaArmorModelSize + delta);
            this.actualHumanoidModels.put(delta, model);
        }

        return model;
    }

    public HumanoidArmorModel<AbstractClientPlayer> actualHumanoidModel() {
        return this.actualHumanoidModel(0.0F);
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
