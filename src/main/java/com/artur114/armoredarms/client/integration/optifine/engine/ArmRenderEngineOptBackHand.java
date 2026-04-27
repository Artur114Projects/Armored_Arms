package com.artur114.armoredarms.client.integration.optifine.engine;

import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.client.util.Optifine;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.Reflector;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;
import thaumcraft.common.items.relics.ItemThaumometer;
import xonin.backhand.api.core.BackhandUtils;
import xonin.backhand.api.core.IBackhandPlayer;

import java.nio.IntBuffer;

public class ArmRenderEngineOptBackHand extends ArmRenderEngineOptifine {

    @Override
    protected void render(RenderHandEvent e, boolean isShader, float partialTicks, boolean renderTranslucent) {
        super.render(e, isShader, partialTicks, renderTranslucent);
        if (BackhandUtils.getOffhandItem(this.mc.thePlayer) != null && !this.isUsed2Arm(this.itemRenderer.itemToRender)) {
            GL11.glEnable(2884);
            GL11.glCullFace(1028);
            GL11.glPushMatrix();
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            EntityPlayerSP player = this.mc.thePlayer;
            float f3 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * e.partialTicks;
            float f4 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * e.partialTicks;
            GL11.glRotatef((player.rotationPitch - f3) * -0.1F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((player.rotationYaw - f4) * -0.1F, 0.0F, 1.0F, 0.0F);
            this.entityRenderer.enableLightmap(partialTicks);
            if (isShader) {
                Shaders.setRenderingFirstPersonHand(true);
                GL11.glDepthMask(true);
                if (renderTranslucent) {
                    GL11.glDepthFunc(519);
                    GL11.glPushMatrix();
                    IntBuffer drawBuffers = Reflector.getPrivateField(Shaders.class, null, "activeDrawBuffers");
                    Shaders.setDrawBuffers(Reflector.getPrivateField(Shaders.class, null, "drawBuffersNone"));
                    Shaders.renderItemKeepDepthMask = true;
                    this.renderItemInFirstPerson(EnumHandSideAA.LEFT, e.partialTicks, ((IBackhandPlayer) player).getOffSwingProgress(e.partialTicks), BackhandUtils.getOffhandItem(this.mc.thePlayer));
                    Shaders.renderItemKeepDepthMask = false;
                    Shaders.setDrawBuffers(drawBuffers);
                    GL11.glPopMatrix();
                }

                GL11.glDepthFunc(515);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.renderItemInFirstPerson(EnumHandSideAA.LEFT, e.partialTicks, ((IBackhandPlayer) player).getOffSwingProgress(e.partialTicks), BackhandUtils.getOffhandItem(this.mc.thePlayer));
                Shaders.setRenderingFirstPersonHand(false);
            } else {
                this.renderItemInFirstPerson(EnumHandSideAA.LEFT, e.partialTicks, ((IBackhandPlayer) player).getOffSwingProgress(e.partialTicks), BackhandUtils.getOffhandItem(this.mc.thePlayer));
            }

            this.entityRenderer.disableLightmap(partialTicks);
            GL11.glPopMatrix();
            GL11.glCullFace(1029);
        }
    }

    private boolean isUsed2Arm(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemMap || EnumMods.THAUMCRAFT.isLoaded() && itemStack.getItem() instanceof ItemThaumometer);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return Optifine.isLoaded() && EnumMods.BACKHAND.isLoaded();
    }
}
