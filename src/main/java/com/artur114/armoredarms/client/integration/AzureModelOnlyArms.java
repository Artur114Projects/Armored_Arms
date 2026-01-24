package com.artur114.armoredarms.client.integration;

import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class AzureModelOnlyArms implements IModelOnlyArms {
    public final AzModelRenderer<UUID, ItemStack> renderer;
    public final AzArmorRendererPipeline pipeline;
    public final AzArmorModel<?> ma;
    public final String[] arms;

    public AzureModelOnlyArms(AzArmorModel<?> model) {
        this.ma = model;

        this.pipeline = Reflector.getPrivateField(AzArmorModel.class, model, "rendererPipeline"); // dark magic!
        this.renderer = Reflector.getPrivateField(AzRendererPipeline.class, this.pipeline, "modelRenderer"); // dark magic!

        this.arms = new String[] {"armorLeftArm", "armorRightArm"};
    }

    @Override
    public void renderArm(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        Minecraft mc = Minecraft.getInstance();
        AzArmorRendererPipelineContext context = this.pipeline.context();
        MultiBufferSource bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        if (Minecraft.getInstance().levelRenderer.shouldShowEntityOutlines() && Minecraft.getInstance().shouldEntityAppearGlowing(player)) {
            bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.outlineBufferSource();
        }

        context.prepare(player, stackArmor, EquipmentSlot.CHEST, this.ma);
        float partialTick = mc.getPartialTick();
        AzArmorRendererConfig config = this.pipeline.config();
        ItemStack anim = Optional.ofNullable(context.animatable()).orElse(stackArmor);
        ResourceLocation textureLocation = config.textureLocation(player, anim);
        RenderType renderType = context.getDefaultRenderType(anim, textureLocation, bufferSource, partialTick, config.getRenderType(context.currentEntity(), anim), config.alpha(anim));
        pBuffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, stackArmor.hasFoil());
        AzBakedModel model = this.pipeline.renderer().provider().provideBakedModel(player, anim);
        context.populate(stackArmor, model, bufferSource, pPackedLight, partialTick, pPoseStack, renderType, pBuffer);

        if (model == null) {
            return;
        }

        AzBone arm = model.getBoneOrNull(this.arms[side.ordinal()]);

        if (arm == null) {
            return;
        }

        arm.setTrackingMatrices(false);
        this.ma.attackTime = 0.0F;
        this.ma.crouching = false;
        this.ma.swimAmount = 0.0F;

        int delta = MiscUtils.handSideDelta(side);
        arm.setRotX(0.0F);
        arm.setRotY(0.0F);
        arm.setRotZ((float) ((Math.PI + 0.1F) * delta));

        arm.setPosX(arm.getPivotX() * 2);
        arm.setPosY(2.0F * -1 * 10);
        arm.setPosZ(0.0F);

        arm.setScaleX(1.0F);
        arm.setScaleY(1.0F);
        arm.setScaleZ(1.0F);

        boolean h = arm.isHidden();
        arm.setHidden(false);
        Reflector.invokeMethod(AzModelRenderer.class, this.renderer, "renderRecursively", new Class[] {AzRendererPipelineContext.class, AzBone.class, boolean.class}, new Object[]{context, arm, false});  // dark magic!
        arm.setHidden(h);
    }

    @Override
    public Model original() {
        return ma;
    }
}
