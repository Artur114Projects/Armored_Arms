package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.AbstractRenderEngine;
import com.artur114.armoredarms.core.util.ArmsBone;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IAAModContainer;
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

public abstract class AbstractRenderEngineForge<E extends AbstractRenderEngine<?, ?>, P extends AbstractRenderPipelineForge<?>> extends AbstractRenderEngine<E, P> {
    private Float2ObjectMap<HumanoidArmorModel<AbstractClientPlayer>> actualHumanoidModels = null;
    public final Minecraft mc = Minecraft.getInstance();
    private double lastModelSize = Double.MIN_VALUE;
    public ArmRenderContext renderContext = null;
    private ArmsBone bones = null;

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
    public void init(P context, IAAModContainer mod) {
        super.init(context, mod);

        Bone.register(new BoneAdepterModelPart());
        this.bones = new ArmsBone(mod);
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

    @Override
    public ArmsBone mainBones() {
        return this.bones;
    }

    private static class BoneAdepterModelPart implements Bone.IBoneAdapter<ModelPart> {

        @Override
        public void inject(Bone bone, ModelPart to) {
            to.xRot = bone.rotateAngleX;
            to.yRot = bone.rotateAngleY;
            to.zRot = bone.rotateAngleZ;
            to.x = bone.rotationPointX;
            to.y = bone.rotationPointY;
            to.z = bone.rotationPointZ;
        }

        @Override
        public void set(Bone bone, ModelPart from) {
            bone.rotateAngleX = from.xRot;
            bone.rotateAngleY = from.yRot;
            bone.rotateAngleZ = from.zRot;
            bone.rotationPointX = from.x;
            bone.rotationPointY = from.y;
            bone.rotationPointZ = from.z;
        }

        @Override
        public Class<ModelPart> targetObjectClass() {
            return ModelPart.class;
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
