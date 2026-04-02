package com.artur114.armoredarms.client.integration.azurelib.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Reflector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.AzArmorModel;
import mod.azure.azurelib.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.render.armor.AzArmorRendererPipeline;
import mod.azure.azurelib.render.armor.AzArmorRendererPipelineContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public class ArmModelRendererAzure implements IArmModelRenderer<ArmModelManagerArmor> {
    public final ModelPart[] playerArms = AAUtils.playerArms();
    public final AzModelRenderer<UUID, ItemStack> renderer;
    public final MultiModelRenderContext context;
    public final AzArmorRendererPipeline pipeline;
    public final Method renderRecursively;
    public final AzArmorModel<?> ma;
    public final String[] arms;

    public ArmModelRendererAzure(MultiModelRenderContext context, AzArmorModel<?> model) {
        this.context = context;
        this.ma = model;

        this.pipeline = Reflector.getPrivateField(AzArmorModel.class, model, "rendererPipeline"); // dark magic!
        this.renderer = Reflector.getPrivateField(AzRendererPipeline.class, this.pipeline, "modelRenderer"); // dark magic!

        this.arms = new String[] {"armorLeftArm", "armorRightArm"};

        this.renderRecursively = Reflector.findMethod(AzModelRenderer.class, "renderRecursively", AzRendererPipelineContext.class, AzBone.class, boolean.class);  // dark magic!
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        for (IModelRenderContext contextPart : this.context) {
            this.render(contextPart.poseStack(), contextPart.multiBuffer(), context.chestPlate, side, contextPart.packedLight());
        }
    }

    public void render(PoseStack pPoseStack, MultiBufferSource multiBuffer, ItemStackAA stack, EnumHandSideAA side, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        AzArmorRendererPipelineContext context = this.pipeline.context();
        context.prepare(player, stack.stack(), EquipmentSlot.CHEST, this.ma);
        float partialTick = mc.getPartialTick();
        AzArmorRendererConfig config = this.pipeline.config();
        ItemStack anim = Optional.ofNullable(context.animatable()).orElse(stack.stack());
        ResourceLocation textureLocation = config.textureLocation(player, anim);
        RenderType renderType = context.getDefaultRenderType(anim, textureLocation, multiBuffer, partialTick, config.getRenderType(context.currentEntity(), anim), config.alpha(anim));
        VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(multiBuffer, renderType, false, stack.stack().hasFoil());
        AzBakedModel model = this.pipeline.renderer().provider().provideBakedModel(player, anim);
        context.populate(stack.stack(), model, multiBuffer, pPackedLight, partialTick, pPoseStack, renderType, buffer);

        if (model == null) {
            return;
        }

        ModelPart playerArm = this.playerArms[side.ordinal()];
        AzBone arm = model.getBoneOrNull(this.arms[side.ordinal()]);

        if (arm == null) {
            return;
        }

        this.ma.attackTime = 0.0F;
        this.ma.crouching = false;
        this.ma.swimAmount = 0.0F;

        int delta = side.delta();
        arm.setRotX(playerArm.xRot);
        arm.setRotY(playerArm.yRot);
        arm.setRotZ((float) (Math.PI * delta) + playerArm.zRot);

        arm.setPosX(arm.getPivotX() * 2);
        arm.setPosY(2.0F * -1 * 10);
        arm.setPosZ(0.0F);

        arm.setScaleX(1.0F);
        arm.setScaleY(1.0F);
        arm.setScaleZ(1.0F);

        boolean h = arm.isHidden();
        arm.setHidden(false);
        Reflector.invokeMethod(this.renderRecursively, this.renderer, context, arm, false);  // dark magic!
        arm.setHidden(h);
    }
}
