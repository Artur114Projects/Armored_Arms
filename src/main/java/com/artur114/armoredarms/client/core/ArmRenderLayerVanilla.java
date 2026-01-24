package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    public List<ShapelessRL> noRenderArmWearList = null;
    public List<ShapelessRL> renderArmWearList = null;
    public PlayerRenderer renderPlayer = null;
    public ItemStack chestPlate = null;
    public boolean renderWear = true;
    public boolean canceled = false;

    @Override
    public void update(AbstractClientPlayer player) {
        ItemStack chestPlate = player.getItemBySlot(EquipmentSlot.CHEST);

        if (this.chestPlate != chestPlate) {
            this.chestPlate = chestPlate;

            ResourceLocation rl = ForgeRegistries.ITEMS.getKey(chestPlate.getItem());

            if (chestPlate.getItem() instanceof ArmorItem && rl != null) {
                Model armor = ForgeHooksClient.getArmorModel(player, chestPlate, EquipmentSlot.CHEST, ArmoredArms.RENDER_ARM_MANAGER.actualHumanoidModel);
                this.renderWear = !this.noRenderArmWearList.contains(new ShapelessRL(rl)) && (this.renderArmWearList.contains(new ShapelessRL(rl)) || armor == null || armor.getClass() == HumanoidArmorModel.class);
            }
        }
    }

    @Override
    public void renderTransformed(PoseStack poseStack, MultiBufferSource buffer, AbstractClientPlayer player, HumanoidArm side, int combinedLight) {
        if (player.isInvisible() || this.canceled) {
            return;
        }

        this.renderArm(poseStack, buffer, combinedLight, player, side, !AAConfig.disableArmWear || (AAConfig.enableArmWearWithVanillaM && this.renderWear));
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return renderManagerState;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderArmWearList = this.initRenderArmWearList();
        this.noRenderArmWearList = this.initNoRenderArmWearList();
    }

    public List<ShapelessRL> initRenderArmWearList() {
        List<ShapelessRL> ret = new ArrayList<>(AAConfig.renderArmWearList.length);
        for (String id : AAConfig.renderArmWearList) {
            ShapelessRL rl = new ShapelessRL(id);

            if (!rl.isEmpty()) {
                ret.add(rl);
            }
        }
        return ret;
    }

    public List<ShapelessRL> initNoRenderArmWearList() {
        List<ShapelessRL> ret = new ArrayList<>(AAConfig.noRenderArmWearList.length);
        for (String id : AAConfig.noRenderArmWearList) {
            ShapelessRL rl = new ShapelessRL(id);

            if (!rl.isEmpty()) {
                ret.add(rl);
            }
        }
        return ret;
    }

    public void renderArm(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer player, HumanoidArm side, boolean renderWear) {
        this.renderPlayer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        PlayerModel<AbstractClientPlayer> playerModel = this.renderPlayer.getModel();
        ModelPart pRendererArmWear;
        ModelPart pRendererArm = switch (side) {
            case RIGHT -> {
                pRendererArmWear = playerModel.rightSleeve;
                yield playerModel.rightArm;
            }
            case LEFT -> {
                pRendererArmWear = playerModel.leftSleeve;
                yield playerModel.leftArm;
            }
        };

        this.setModelProperties(player);
        playerModel.attackTime = 0.0F;
        playerModel.crouching = false;
        playerModel.swimAmount = 0.0F;
        playerModel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        pRendererArm.xRot = 0.0F;
        if (AAConfig.useForcedRotations) MiscUtils.setForcedRotations(pRendererArm, side);
        pRendererArm.render(pPoseStack, pBuffer.getBuffer(RenderType.entitySolid(player.getSkinTextureLocation())), pCombinedLight, OverlayTexture.NO_OVERLAY);
        if (renderWear) {
            pRendererArmWear.xRot = 0.0F;
            if (AAConfig.useForcedRotations) MiscUtils.setForcedRotations(pRendererArmWear, side);
            pRendererArmWear.render(pPoseStack, pBuffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation())), pCombinedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private void setModelProperties(AbstractClientPlayer pClientPlayer) {
        PlayerModel<AbstractClientPlayer> playermodel = this.renderPlayer.getModel();
        if (pClientPlayer.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = pClientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = pClientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = pClientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = pClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = pClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = pClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = pClientPlayer.isCrouching();
        }
    }

}