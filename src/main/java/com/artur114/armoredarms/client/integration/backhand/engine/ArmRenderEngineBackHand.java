package com.artur114.armoredarms.client.integration.backhand.engine;

import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import thaumcraft.common.items.relics.ItemThaumometer;
import xonin.backhand.api.core.BackhandUtils;
import xonin.backhand.api.core.IBackhandPlayer;

public class ArmRenderEngineBackHand extends ArmRenderEngineForge {
    @Override
    public void renderHand(RenderHandEvent e) {
        boolean flag = this.itemRenderer.itemToRender == null || this.itemRenderer.itemToRender.getItem() instanceof ItemMap;
        if (flag && this.entityRenderer.debugViewDirection <= 0) {
            e.setCanceled(true);
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float f1 = 0.07F;
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(-(e.renderPass * 2 - 1)) * f1, 0.0F, 0.0F);
            }

            if (this.entityRenderer.cameraZoom != 1.0) {
                GL11.glTranslatef((float)this.entityRenderer.cameraYaw, (float)(-this.entityRenderer.cameraPitch), 0.0F);
                GL11.glScaled(this.entityRenderer.cameraZoom, this.entityRenderer.cameraZoom, 1.0);
            }

            Project.gluPerspective(this.entityRenderer.getFOVModifier(e.partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.entityRenderer.farPlaneDistance * 2.0F);
            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                GL11.glScalef(1.0F, 0.6666667F, 1.0F);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(e.renderPass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            this.entityRenderer.hurtCameraEffect(e.partialTicks);
            if (this.mc.gameSettings.viewBobbing) {
                this.entityRenderer.setupViewBobbing(e.partialTicks);
            }

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                this.entityRenderer.enableLightmap(e.partialTicks);
                this.renderItemInFirstPerson(EnumHandSideAA.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
                if (EnumMods.BACKHAND.isLoaded() && BackhandUtils.getOffhandItem(this.mc.thePlayer) != null && !this.isUsed2Arm(this.itemRenderer.itemToRender)) {
                    GL11.glEnable(2884);
                    GL11.glCullFace(1028);
                    GL11.glPushMatrix();
                    GL11.glScalef(-1.0F, 1.0F, 1.0F);
                    EntityPlayerSP player = this.mc.thePlayer;
                    float f3 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * e.partialTicks;
                    float f4 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * e.partialTicks;
                    GL11.glRotatef((player.rotationPitch - f3) * -0.1F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef((player.rotationYaw - f4) * -0.1F, 0.0F, 1.0F, 0.0F);
                    this.renderItemInFirstPerson(EnumHandSideAA.LEFT, e.partialTicks, ((IBackhandPlayer) player).getOffSwingProgress(e.partialTicks), BackhandUtils.getOffhandItem(this.mc.thePlayer));
                    GL11.glPopMatrix();
                    GL11.glCullFace(1029);
                }
                this.entityRenderer.disableLightmap(e.partialTicks);
            }

            GL11.glPopMatrix();
            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(e.partialTicks);
                this.entityRenderer.hurtCameraEffect(e.partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.entityRenderer.setupViewBobbing(e.partialTicks);
            }
        }
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return EnumMods.BACKHAND.isLoaded();
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }

    private boolean isUsed2Arm(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemMap || EnumMods.THAUMCRAFT.isLoaded() && itemStack.getItem() instanceof ItemThaumometer);
    }
}
