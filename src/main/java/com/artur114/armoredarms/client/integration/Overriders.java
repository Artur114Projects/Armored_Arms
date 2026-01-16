package com.artur114.armoredarms.client.integration;


import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.api.IOverriderGetModel;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.main.AAConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Here i ultrashitcoding
 */
@Mod.EventBusSubscriber
public class Overriders {
    @SubscribeEvent
    public static void initArmorRenderLayer(InitArmorRenderLayerEvent e) {
        e.registerOverrider("create", "netherite_backtank", new CreateBackTankOverrider(), false);


        e.addArmorToBlackList(AAConfig.renderBlackList);
    }

    @SubscribeEvent
    public static void initRenderLayers(InitRenderLayersEvent e) {

    }

    public static class CreateBackTankOverrider extends ArmRenderLayerArmor.DefaultRender implements IOverriderGetModel {
        private final HumanoidArmorModel<AbstractClientPlayer> mb = new HumanoidArmorModel<>(HumanoidArmorModel.createMesh(new CubeDeformation(1.0F), 0.0F).getRoot().bake(64, 32));
        private final ResourceLocation tex = new ResourceLocation("create", "textures/models/armor/netherite_diving_arm.png");

        @Override
        protected void renderBase(PoseStack pPoseStack, MultiBufferSource pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, IModelOnlyArms pModel, ResourceLocation armorResource) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entitySolid(this.tex));
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            PlayerModel<AbstractClientPlayer> model = ((PlayerRenderer) renderer).getModel();
            model.attackTime = 0.0F;
            model.crouching = false;
            model.swimAmount = 0.0F;
            model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            ModelPart armPart = side == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
            armPart.xRot = 0.0F;
            boolean s = armPart.skipDraw;
            boolean v = armPart.visible;
            armPart.skipDraw = false;
            armPart.visible = true;
            armPart.render(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            armPart.skipDraw = s;
            armPart.visible = v;
            super.renderBase(pPoseStack, pBuffer, player, itemArmor, stackArmor, side, pPackedLight, pModel, armorResource);
        }

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stack) {
            return new ArmRenderLayerArmor.DefaultModelOnlyArms(this.mb);
        }
    }
}
