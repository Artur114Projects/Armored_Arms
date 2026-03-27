//package com.artur114.armoredarms.client.integration.legacy;
//
//import com.artur114.armoredarms.aalegacy.api.IModelOnlyArms;
//import com.artur114.armoredarms.aalegacy.client.util.MiscUtils;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.model.Model;
//import net.minecraft.client.model.geom.ModelPart;
//import net.minecraft.client.player.AbstractClientPlayer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.entity.ItemRenderer;
//import net.minecraft.world.entity.HumanoidArm;
//import net.minecraft.world.item.ArmorItem;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import software.bernie.geckolib.animatable.GeoItem;
//import software.bernie.geckolib.cache.object.*;
//import software.bernie.geckolib.renderer.GeoArmorRenderer;
//
//import java.lang.Math;
//
//public class GeckoModelOnlyArms implements IModelOnlyArms {
//    public final ModelPart[] playerArms = MiscUtils.playerArms();
//    public final GeoArmorRenderer<?> mg;
//    public final String[] arms;
//
//
//    public GeckoModelOnlyArms(GeoArmorRenderer<?> model) {
//        this.mg = model;
//
//        this.arms = new String[] {"armorLeftArm", "armorRightArm"};
//    }
//
//    @Override
//    public void renderArm(PoseStack pPoseStack, MultiBufferSource multiBuffer, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//        this.render(pPoseStack, multiBuffer, pBuffer, player, itemArmor, stackArmor, side, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T extends Item & GeoItem> void render(PoseStack pPoseStack, MultiBufferSource multiBuffer, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//        T t = (T) itemArmor;
//        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) this.mg;
//        BakedGeoModel baked = model.getGeoModel().getBakedModel(model.getGeoModel().getModelResource(t, model));
//        RenderType renderType = model.getRenderType(t, model.getTextureLocation(t), multiBuffer, Minecraft.getInstance().getPartialTick());
//        pBuffer = ItemRenderer.getArmorFoilBuffer(multiBuffer, renderType, false, stackArmor.hasFoil());
//
//        ModelPart playerArm = this.playerArms[side.ordinal()];
//        GeoBone arm = baked.getBone(this.arms[side.ordinal()]).get();
//        this.mg.attackTime = 0.0F;
//        this.mg.crouching = false;
//        this.mg.swimAmount = 0.0F;
//
//        int delta = MiscUtils.handSideDelta(side);
//        arm.setRotX(playerArm.xRot);
//        arm.setRotY(playerArm.yRot);
//        arm.setRotZ((float) (Math.PI * delta) + playerArm.zRot);
//
//        arm.setPosX(arm.getPivotX() * 2);
//        arm.setPosY(2.0F * -1 * 10);
//        arm.setPosZ(0.0F);
//
//        arm.setScaleX(1.0F);
//        arm.setScaleY(1.0F);
//        arm.setScaleZ(1.0F);
//
//        boolean h = arm.isHidden();
//        arm.setHidden(false);
//        model.renderRecursively(pPoseStack, t, arm, renderType, multiBuffer, pBuffer, false, Minecraft.getInstance().getPartialTick(), pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
//        arm.setHidden(h);
//    }
//
//    @Override
//    public Model original() {
//        return this.mg;
//    }
//}
