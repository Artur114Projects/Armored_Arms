package com.artur114.armoredarms.client.integration;

import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.Reflector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class GeckoModelOnlyArms implements IModelOnlyArms {
    public final ModelPart[] playerArms = MiscUtils.playerArms();
    public final GeoArmorRenderer<?> mg;
    public final String[] arms;


    public GeckoModelOnlyArms(GeoArmorRenderer<?> model) {
        this.mg = model;

        this.arms = new String[] {"armorLeftArm", "armorRightArm"};

        System.out.println("Geckolib is gavno");
    }

    @Override
    public void renderArm(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.render(pPoseStack, pBuffer, player, itemArmor, stackArmor, side, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    @SuppressWarnings("unchecked")
    public <T extends Item & GeoItem> void render(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        T t = (T) itemArmor;
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) this.mg;
        BakedGeoModel baked = model.getGeoModel().getBakedModel(model.getGeoModel().getModelResource(t, model));

        MultiBufferSource bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        RenderType renderType = model.getRenderType(t, model.getTextureLocation(t), bufferSource, Minecraft.getInstance().getPartialTick());
        pBuffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, stackArmor.hasFoil());

        GeoBone arm = baked.getBone(this.arms[side.ordinal()]).get();
//        arm.setTrackingMatrices(false);
        this.mg.attackTime = 0.0F;
        this.mg.crouching = false;
        this.mg.swimAmount = 0.0F;
        this.mg.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        boolean h = arm.isHidden();
        arm.setHidden(false);
        model.renderRecursively(pPoseStack, t, arm, renderType, bufferSource, pBuffer, false, Minecraft.getInstance().getPartialTick(), pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        arm.setHidden(h);
    }

    @Override
    public Model original() {
        return this.mg;
    }
}
