package com.artur114.armoredarms.client.integration.optifine.engine;

import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.util.Optifine;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.Reflector;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.event.RenderHandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

public class ArmRenderEngineOptifine extends ArmRenderEngineForge {
    @Override
    public void renderHand(RenderHandEvent e) {
        if (this.hasInStackTrace(ShadersRender.class)) {
            this.render(e, Optifine.isShaders(), e.partialTicks, false); e.setCanceled(true); return;
        }

        if (this.entityRenderer.cameraZoom == 1.0D && !Shaders.isShadowPass) {
            boolean isShaders = Optifine.isShaders();

            if (isShaders) {
                ShadersRender_renderHand1(e, e.partialTicks, e.renderPass);
                Shaders.renderCompositeFinal();
            }

            GL11.glClear(256);
            if (isShaders) {
                ShadersRender_renderFPOverlay(e, e.partialTicks, e.renderPass);
            } else {
                this.renderHand(e, e.partialTicks, e.renderPass);
            }
            e.setCanceled(true);
        }
    }

    @Override
    protected void renderHand(RenderHandEvent e, float partialTicks, int renderPass) {
        this.renderHand(e, partialTicks, renderPass, true, true, false);
    }

    protected void renderHand(RenderHandEvent e, float partialTicks, int renderPass, boolean renderItem, boolean renderOverlay, boolean renderTranslucent) {
        if (this.entityRenderer.debugViewDirection <= 0) {
            boolean isShader = Optifine.isShaders();
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float var3 = 0.07F;
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float) (-(renderPass * 2 - 1)) * var3, 0.0F, 0.0F);
            }

            if (this.entityRenderer.cameraZoom != (double) 1.0F) {
                GL11.glTranslatef((float) this.entityRenderer.cameraYaw, (float) (-this.entityRenderer.cameraPitch), 0.0F);
                GL11.glScaled(this.entityRenderer.cameraZoom, this.entityRenderer.cameraZoom, 1.0F);
            }

            if (isShader) {
                Shaders.applyHandDepth();
            }

            Project.gluPerspective(this.entityRenderer.getFOVModifier(partialTicks, false), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.entityRenderer.farPlaneDistance * 2.0F);
            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                float var4 = 0.6666667F;
                GL11.glScalef(1.0F, var4, 1.0F);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float) (renderPass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            if (renderItem) {
                GL11.glPushMatrix();
                this.entityRenderer.hurtCameraEffect(partialTicks);
                if (this.mc.gameSettings.viewBobbing) {
                    this.entityRenderer.setupViewBobbing(partialTicks);
                }

                this.render(e, isShader, partialTicks, renderTranslucent);

                GL11.glPopMatrix();
            }

            if (!renderOverlay) {
                return;
            }

            this.entityRenderer.disableLightmap(partialTicks);
            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(partialTicks);
                this.entityRenderer.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.entityRenderer.setupViewBobbing(partialTicks);
            }
        }
    }

    protected void render(RenderHandEvent e, boolean isShader, float partialTicks, boolean renderTranslucent) {
        if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            this.entityRenderer.enableLightmap(partialTicks);
            if (isShader) {
                ShadersRender_renderItemFP(renderTranslucent, e);
            } else {
                this.renderItemInFirstPerson(EnumHandSideAA.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
            }

            this.entityRenderer.disableLightmap(partialTicks);
        }
    }

    protected void ShadersRender_renderItemFP(boolean renderTranslucent, RenderHandEvent e) {
        Shaders.setRenderingFirstPersonHand(true);
        GL11.glDepthMask(true);
        if (renderTranslucent) {
            GL11.glDepthFunc(519);
            GL11.glPushMatrix();
            IntBuffer drawBuffers = Reflector.getPrivateField(Shaders.class, null, "activeDrawBuffers");
            Shaders.setDrawBuffers(Reflector.getPrivateField(Shaders.class, null, "drawBuffersNone"));
            Shaders.renderItemKeepDepthMask = true;
            this.renderItemInFirstPerson(EnumHandSideAA.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
            Shaders.renderItemKeepDepthMask = false;
            Shaders.setDrawBuffers(drawBuffers);
            GL11.glPopMatrix();
        }

        GL11.glDepthFunc(515);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderItemInFirstPerson(EnumHandSideAA.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
        Shaders.setRenderingFirstPersonHand(false);
    }

    protected void ShadersRender_renderHand1(RenderHandEvent e, float partialTicks, int renderPass) {
        if (!Shaders.isShadowPass && !Shaders.isHandRenderedMain()) {
            Shaders.readCenterDepth();
            GL11.glEnable(3042);
            Shaders.beginHand(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.renderHand(e, partialTicks, renderPass, true, false, true);
            Shaders.endHand();
            Shaders.setHandRenderedMain(true);
        }
    }

    protected void ShadersRender_renderFPOverlay(RenderHandEvent e, float partialTicks, int renderPass) {
        if (!Shaders.isShadowPass) {
            Shaders.beginFPOverlay();
            this.renderHand(e, partialTicks, renderPass, false, true, false);
            Shaders.endFPOverlay();
        }
    }

    protected boolean hasInStackTrace(Class<?> clazz) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : trace) {
            if (element.getClassName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    private void printStackTrace() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        Logger logger = LogManager.getLogger("OP/TRACE");
        for (StackTraceElement element : trace) {
            logger.info(element);
        }
        logger.info("---end---");
    }


    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return Optifine.isLoaded();
    }
}
