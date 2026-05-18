package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.*;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends AbstractRenderPipelineForge<?>> extends AbstractRenderEngine<E, P> {
    private Float2ObjectMap<HumanoidArmorModel<AbstractClientPlayer>> actualHumanoidModels = null;
    public final Minecraft mc = Minecraft.getInstance();
    private double lastModelSize = Double.MIN_VALUE;
    public ArmRenderContext renderContext = null;
    protected boolean forcedRender = false;

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
    protected List<IBoneAdapter<?>> initBoneAdapters() {
        InitBoneAdaptersEvent event = new InitBoneAdaptersEvent(this.mod);
        event.registerAdapter(new BoneAdepterModelPart());
        this.mod.post(event);
        return event.adaptersList();
    }

    @Override
    public void tryRender(P context) {
        this.forcedRender = this.readForcedRender(context.renderArgs());
        this.renderContext = context.renderContext;
        super.tryRender(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void renderAllLayers(EnumHandSideAA side) {
        for (IArmRenderLayer<E> layer : this.sortedLayers) {
            if (this.forcedRender || layer.needRender((E) this, this.render)) {
                try {
                    if (this.onLayerRendering(layer, side)) {
                        layer.render((E) this, side);
                    }
                } catch (RenderException rm) {
                    throw rm;
                } catch (Throwable tr) {
                    throw new RenderException(tr).setComponent(layer);
                }
            }
        }
    }

    @Override
    public boolean updateAllLayers() {
        return super.updateAllLayers() || this.forcedRender;
    }

    protected boolean readForcedRender(ObjectBuff buff) {
        if (buff.size() > 0) {
            Object obj = buff.readObject(); buff.reset();
            return "FORCED_RENDER".equals(obj);
        }
        return false;
    }

    private static class BoneAdepterModelPart implements IBoneAdapter<ModelPart> {
        @Override
        public void inject(Bone bone, ObjectBuff data, ModelPart to) {
            to.xRot = data.readFloat();
            to.yRot = data.readFloat();
            to.zRot = data.readFloat();
            to.x = data.readFloat();
            to.y = data.readFloat();
            to.z = data.readFloat();
        }

        @Override
        public void set(Bone bone, ObjectBuff data, ModelPart from) {
            data.writeFloat("xRot", from.xRot);
            data.writeFloat("yRot", from.yRot);
            data.writeFloat("zRot", from.zRot);
            data.writeFloat("x", from.x);
            data.writeFloat("y", from.y);
            data.writeFloat("z", from.z);
        }

        @Override
        public Class<ModelPart> targetObjectClass() {
            return ModelPart.class;
        }

        @Override
        public boolean canWork(ModelPart modelPart) {
            return true;
        }

        @Override
        public IPriority priority() {
            return Priority.NORMAL;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<AbstractRenderEngineForge<?, ?>> clazz() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }
}
