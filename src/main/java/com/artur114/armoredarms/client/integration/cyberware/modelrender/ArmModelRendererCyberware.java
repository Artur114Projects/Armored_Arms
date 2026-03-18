package com.artur114.armoredarms.client.integration.cyberware.modelrender;

import com.artur114.armoredarms.client.integration.cyberware.EventHandler;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class ArmModelRendererCyberware implements IArmModelRenderer<ArmModelManagerPlayer> {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final RenderPlayer renderPlayer;

    public ArmModelRendererCyberware(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }
    
    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        if (EventHandler.HANDLER == null) {
            return;
        }
        this.mc.getTextureManager().bindTexture(context.playerSkin);
        this.renderArm(context, this.mc.player, side);
    }

    public void renderArm(ArmModelManagerPlayer context, AbstractClientPlayer player, EnumHandSideAA side) {
        if (side == EnumHandSideAA.RIGHT && EventHandler.HANDLER.missingArm) {
            return;
        }
        if (side == EnumHandSideAA.LEFT && EventHandler.HANDLER.missingSecondArm) {
            return;
        }
        boolean flag = context.shouldRenderWear;
        switch (side) {
            case RIGHT:
                this.renderRightArm(player, flag);
                break;
            case LEFT:
                this.renderLeftArm(player, flag);
                break;
        }
    }

    public void renderRightArm(AbstractClientPlayer clientPlayer, boolean renderWear) {
        if (EventHandler.HANDLER.hasRoboRight) {
            this.mc.getTextureManager().bindTexture(EventHandler.HANDLER.robo);
            this.renderRightArmMC(clientPlayer, renderWear);
        } else {
            this.renderRightArmMC(clientPlayer, renderWear);
            return;
        }
        this.mc.getTextureManager().bindTexture(EventHandler.HANDLER.robo);
        if (this.mc.gameSettings.mainHand == EnumHandSide.RIGHT && clientPlayer.getHeldItemMainhand().isEmpty()) {
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
            if (cyberwareUserData != null) {
                ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(1));
                if (!itemStackClaws.isEmpty() && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(1)) && EnableDisableHelper.isEnabled(itemStackClaws)) {
                    GlStateManager.pushMatrix();
                    float percent = ((float)this.mc.player.ticksExisted + this.mc.getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4.0F;
                    percent = Math.min(1.0F, percent);
                    percent = Math.max(0.0F, percent);
                    percent = (float)Math.sin((double)percent * Math.PI / 2.0);
                    EventHandler.HANDLER.claws.claw1.rotateAngleY = 0.0F;
                    EventHandler.HANDLER.claws.claw1.rotateAngleZ = this.renderPlayer.getMainModel().bipedRightArm.rotateAngleZ;
                    EventHandler.HANDLER.claws.claw1.rotateAngleX = 0.0F;
                    EventHandler.HANDLER.claws.claw1.setRotationPoint(-5.0F, -5.0F + 7.0F * percent, 0.0F);
                    EventHandler.HANDLER.claws.claw1.render(0.0625F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer, boolean renderWear) {
        if (EventHandler.HANDLER.hasRoboLeft) {
            this.mc.getTextureManager().bindTexture(EventHandler.HANDLER.robo);
            this.renderLeftArmMC(clientPlayer, renderWear);
        } else {
            this.renderLeftArmMC(clientPlayer, renderWear);
            return;
        }
        this.mc.getTextureManager().bindTexture(EventHandler.HANDLER.robo);
        if (this.mc.gameSettings.mainHand == EnumHandSide.LEFT && clientPlayer.getHeldItemMainhand().isEmpty()) {
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
            if (cyberwareUserData != null) {
                ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(1));
                if (!itemStackClaws.isEmpty() && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(0)) && EnableDisableHelper.isEnabled(itemStackClaws)) {
                    GlStateManager.pushMatrix();
                    float percent = ((float)this.mc.player.ticksExisted + this.mc.getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4.0F;
                    percent = Math.min(1.0F, percent);
                    percent = Math.max(0.0F, percent);
                    percent = (float)Math.sin((double)percent * Math.PI / 2.0);
                    EventHandler.HANDLER.claws.claw1.rotateAngleY = 0.0F;
                    EventHandler.HANDLER.claws.claw1.rotateAngleZ = this.renderPlayer.getMainModel().bipedLeftArm.rotateAngleZ;
                    EventHandler.HANDLER.claws.claw1.rotateAngleX = 0.0F;
                    EventHandler.HANDLER.claws.claw1.setRotationPoint(8.0F, -5.0F + 7.0F * percent, 0.0F);
                    EventHandler.HANDLER.claws.claw1.render(0.0625F);
                    GlStateManager.popMatrix();
                }
            }
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
            this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
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
            this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
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