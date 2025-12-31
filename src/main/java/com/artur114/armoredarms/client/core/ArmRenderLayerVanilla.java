package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    public List<ShapelessRL> renderArmWearList = null;
    public boolean currentArmorModelBiped = true;
    public ModelBiped baseArmorModel = null;
    public RenderPlayer renderPlayer = null;
    public ItemStack chestPlate = null;
    public boolean canceled = false;

    @Override
    public void update(AbstractClientPlayer player) {
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (this.chestPlate != chestPlate) {
            this.chestPlate = chestPlate;

            if (chestPlate.getItem() instanceof ItemArmor) {
                ModelBiped armor = chestPlate.getItem().getArmorModel(player, chestPlate, EntityEquipmentSlot.CHEST, this.baseArmorModel);
                this.currentArmorModelBiped = this.renderArmWearList.contains(new ShapelessRL(chestPlate.getItem().getRegistryName())) || armor == null || armor.getClass() == ModelBiped.class;
            }
        }
    }

    @Override
    public void renderNotTransformed(AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
        this.canceled = ForgeHooksClient.renderSpecificFirstPersonHand(hand, partialTicks, interpPitch, swingProgress, equipProgress, stack);
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
        if (player.isInvisible() || this.canceled) {
            return;
        }

        this.renderPlayer.bindTexture(player.getLocationSkin());
        this.renderArm(player, handSide);
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return renderManagerState;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
        this.baseArmorModel = new ModelBiped(1.0F);
        this.renderArmWearList = this.initRenderArmWearList();
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

    public void renderArm(AbstractClientPlayer player, EnumHandSide side) {
        switch (side) {
            case RIGHT:
                this.renderRightArmMC(player, !AAConfig.disableArmWear || (AAConfig.enableArmWearWithVanillaM && this.currentArmorModelBiped));
            break;
            case LEFT:
                this.renderLeftArmMC(player, !AAConfig.disableArmWear || (AAConfig.enableArmWearWithVanillaM && this.currentArmorModelBiped));
            break;
        }
    }

    public void renderRightArmMC(AbstractClientPlayer clientPlayer, boolean renderWear) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        ModelPlayer modelplayer = this.renderPlayer.getMainModel();
        this.setModelVisibilitiesMC(clientPlayer);
        GlStateManager.enableBlend();
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedRightArm.rotateAngleX = 0.0F;
        modelplayer.bipedRightArm.render(0.0625F);
        if (renderWear) {
            modelplayer.bipedRightArmwear.rotateAngleX = 0.0F;
            modelplayer.bipedRightArmwear.render(0.0625F);
        }
        GlStateManager.disableBlend();
    }

    public void renderLeftArmMC(AbstractClientPlayer clientPlayer, boolean renderWear) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        ModelPlayer modelplayer = this.renderPlayer.getMainModel();
        this.setModelVisibilitiesMC(clientPlayer);
        GlStateManager.enableBlend();
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
        modelplayer.bipedLeftArm.render(0.0625F);
        if (renderWear) {
            modelplayer.bipedLeftArmwear.rotateAngleX = 0.0F;
            modelplayer.bipedLeftArmwear.render(0.0625F);
        }
        GlStateManager.disableBlend();
    }

    private void setModelVisibilitiesMC(AbstractClientPlayer clientPlayer) {
        ModelPlayer modelplayer = this.renderPlayer.getMainModel();

        if (clientPlayer.isSpectator())
        {
            modelplayer.setVisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack itemstack = clientPlayer.getHeldItemMainhand();
            ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
            modelplayer.setVisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.isSneak = clientPlayer.isSneaking();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

            if (!itemstack.isEmpty())
            {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (!itemstack1.isEmpty())
            {
                modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction1 = itemstack1.getItemUseAction();

                    if (enumaction1 == EnumAction.BLOCK)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                    }
                    // FORGE: fix MC-88356 allow offhand to use bow and arrow animation
                    else if (enumaction1 == EnumAction.BOW)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                modelplayer.rightArmPose = modelbiped$armpose;
                modelplayer.leftArmPose = modelbiped$armpose1;
            }
            else
            {
                modelplayer.rightArmPose = modelbiped$armpose1;
                modelplayer.leftArmPose = modelbiped$armpose;
            }
        }
    }
}