package com.artur114.armoredarms.client.integration.cyberware.engines;

import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.integration.cyberware.EventHandler;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.IAAModContainer;
import flaxbeard.cyberware.client.render.RenderCyberlimbHand;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

import static flaxbeard.cyberware.common.handler.EssentialsMissingHandlerClient.firstNonNull;

public class ArmRenderEngineCyberwareForge extends ArmRenderEngineForge {

    @Override
    public boolean canWork(IAAModContainer mod) {
        return EnumMods.CYBERWARE.isLoaded();
    }

    @Override
    public IPriority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public void tick(ArmRenderPipelineForge context) {
        super.tick(context);

        this.render |= EventHandler.HANDLER.missingSecondArm || EventHandler.HANDLER.missingArm || EventHandler.HANDLER.hasRoboLeft || EventHandler.HANDLER.hasRoboRight;
    }

    @Override
    public void renderItemInFirstPerson(float partialTicks) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        AbstractClientPlayer abstractclientplayer = mc.player;
        float swingProgress = abstractclientplayer.getSwingProgress(partialTicks);
        EnumHand enumhand = firstNonNull(abstractclientplayer.swingingHand, EnumHand.MAIN_HAND);
        float rotationPitch = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float rotationYaw = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        boolean doRenderMainHand = true;
        boolean doRenderOffHand = true;
        if (abstractclientplayer.isHandActive()) {
            ItemStack itemstack = abstractclientplayer.getActiveItemStack();
            if (!itemstack.isEmpty() && itemstack.getItem() == Items.BOW) {
                EnumHand enumhand1 = abstractclientplayer.getActiveHand();
                doRenderMainHand = enumhand1 == EnumHand.MAIN_HAND;
                doRenderOffHand = !doRenderMainHand;
            }
        }

        this.rotateArroundXAndY(rotationPitch, rotationYaw);
        this.setLightmap();
        this.rotateArm(partialTicks);
        GlStateManager.enableRescaleNormal();
        if (doRenderMainHand && !EventHandler.HANDLER.missingSecondArm) {
            float f3 = enumhand == EnumHand.MAIN_HAND ? swingProgress : 0.0F;
            float f5 = 1.0F - (itemRenderer.prevEquippedProgressMainHand + (itemRenderer.equippedProgressMainHand - itemRenderer.prevEquippedProgressMainHand) * partialTicks);
            this.renderItemInFirstPerson(abstractclientplayer, partialTicks, rotationPitch, EnumHand.MAIN_HAND, f3, itemRenderer.itemStackMainHand, f5);
        }

        if (doRenderOffHand && !EventHandler.HANDLER.missingArm) {
            float f4 = enumhand == EnumHand.OFF_HAND ? swingProgress : 0.0F;
            float f6 = 1.0F - (itemRenderer.prevEquippedProgressOffHand + (itemRenderer.equippedProgressOffHand - itemRenderer.prevEquippedProgressOffHand) * partialTicks);
            this.renderItemInFirstPerson(abstractclientplayer, partialTicks, rotationPitch, EnumHand.OFF_HAND, f4, itemRenderer.itemStackOffHand, f6);
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void renderArm(EnumHandSide handSide) {
        if (handSide == EnumHandSide.RIGHT && EventHandler.HANDLER.missingArm) {
            return;
        }
        if (handSide == EnumHandSide.LEFT && EventHandler.HANDLER.missingSecondArm) {
            return;
        }
        super.renderArm(handSide);
    }
}
