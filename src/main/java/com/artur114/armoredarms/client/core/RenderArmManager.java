package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.ArmoredArmsApi;
import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.events.AARenderLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.util.RMException;
import com.artur114.armoredarms.client.util.Reflector;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderArmManager {
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = null;
    public final Minecraft mc = Minecraft.getMinecraft();
    public EntityRenderer entityRenderer = null;
    public ItemRenderer itemRenderer = null;
    public boolean initTick = true;
    public boolean render = false;
    public boolean died = false;


    public void renderHandEvent(RenderHandEvent e) {
        if (this.died || !this.render) {
            return;
        }

        try {
            this.tryRender(e);
        } catch (RMException rm) {
            this.onException(rm.setMethod(RMException.Method.RENDER));
        } catch (Throwable exp) {
            this.onException(new RMException(exp).setFatal().setMethod(RMException.Method.RENDER));
        }
    }

    public void tickEventClientTickEvent(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.START || this.mc.thePlayer == null || this.mc.isGamePaused() || this.died) {
            return;
        }

        try {
            this.tryTick(e);
        } catch (RMException rm) {
            this.onException(rm.setMethod(RMException.Method.TICK));
        } catch (Throwable exp) {
            this.onException(new RMException(exp).setFatal().setMethod(RMException.Method.TICK));
        }
    }

    public void tryRender(RenderHandEvent e) {
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
                float f2 = 0.6666667F;
                GL11.glScalef(1.0F, f2, 1.0F);
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
                this.entityRenderer.enableLightmap((double)e.partialTicks);
                this.renderItemInFirstPerson(e.partialTicks);
                this.entityRenderer.disableLightmap((double)e.partialTicks);
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

    public void tryTick(TickEvent.ClientTickEvent e) {
        AbstractClientPlayer player = this.mc.thePlayer;

        if (this.initTick) {
            try {
                this.init(player); this.initTick = false;
            } catch (Throwable exp) {
                throw new RMException("It was not possible to load RenderArmManager, custom hands will not be rendered!", exp).setFatal();
            }
        }

        boolean render = false;

        for (IArmRenderLayer layer : this.renderLayers.values()) {

            try {
                layer.update(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }

            render |= layer.needRender(player, render);
        }

        this.render = render;
    }

    @SuppressWarnings("unchecked")
    public <T extends IArmRenderLayer> T getLayer(Class<T> tClass) {
        return (T) this.renderLayers.get(tClass);
    }

    public boolean killLayer(Class<? extends IArmRenderLayer> tClass) {
        return this.renderLayers.remove(tClass) != null;
    }

    public void init(AbstractClientPlayer player) {
        InitRenderLayersEvent event = new InitRenderLayersEvent();
        event.addLayer(ArmRenderLayerVanilla.class);
        event.addLayer(ArmRenderLayerArmor.class);
        MinecraftForge.EVENT_BUS.post(event);
        this.renderLayers = event.renderLayers();

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            try {
                layer.init(player);
            } catch (RMException rm) {
                throw rm;
            } catch (Throwable tr) {
                throw new RMException(tr).setFatalLayer(layer);
            }
        }

        this.entityRenderer = this.mc.entityRenderer;
        this.itemRenderer = this.entityRenderer.itemRenderer;
    }

    public void onException(RMException exception) {
        if (exception.isFatal()) {
            this.died = true;
        }

        if (exception.isFatalOnLayer()) {
            this.renderLayers.remove(exception.fatalLayer().getClass());
        }

        for (IChatComponent message : exception.messageForPlayer()) {
            this.mc.thePlayer.addChatComponentMessage(message.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
        }

        exception.printStackTrace(System.err);
    }

    public void callRenderNotTransformed(AbstractClientPlayer player, float partialTicks, float interpPitch, float swingProgress, ItemStack stack, float equipProgress) {
        for (IArmRenderLayer renderLayer : this.renderLayers.values()) {
            renderLayer.renderNotTransformed(player, partialTicks, interpPitch, swingProgress, stack, equipProgress);
        }
    }

    public void renderItemInFirstPerson(float partialTicks) {
        float f1 = this.itemRenderer.prevEquippedProgress + (this.itemRenderer.equippedProgress - this.itemRenderer.prevEquippedProgress) * partialTicks;
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * partialTicks;
        GL11.glPushMatrix();
        GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        float f3 = entityclientplayermp.prevRenderArmPitch + (entityclientplayermp.renderArmPitch - entityclientplayermp.prevRenderArmPitch) * partialTicks;
        float f4 = entityclientplayermp.prevRenderArmYaw + (entityclientplayermp.renderArmYaw - entityclientplayermp.prevRenderArmYaw) * partialTicks;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);
        ItemStack itemstack = this.itemRenderer.itemToRender;
        if (itemstack != null && itemstack.getItem() instanceof ItemCloth) {
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        int i = this.mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f5;
        float f6;
        float f7;
        if (itemstack != null) {
            int l = itemstack.getItem().getColorFromItemStack(itemstack, 0);
            f5 = (float)(l >> 16 & 255) / 255.0F;
            f6 = (float)(l >> 8 & 255) / 255.0F;
            f7 = (float)(l & 255) / 255.0F;
            GL11.glColor4f(f5, f6, f7, 1.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        float f9;
        float f10;
        float f13;
        Render render;
        RenderPlayer renderplayer;
        float f8;
        if (itemstack != null && itemstack.getItem() instanceof ItemMap) {
            GL11.glPushMatrix();
            f13 = 0.8F;
            f5 = entityclientplayermp.getSwingProgress(partialTicks);
            f6 = MathHelper.sin(f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F * 2.0F) * 0.2F, -f6 * 0.2F);
            f5 = 1.0F - f2 / 45.0F + 0.1F;
            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            f5 = -MathHelper.cos(f5 * 3.1415927F) * 0.5F + 0.5F;
            GL11.glTranslatef(0.0F, 0.0F * f13 - (1.0F - f1) * 1.2F - f5 * 0.5F + 0.04F, -0.9F * f13);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f5 * -85.0F, 0.0F, 0.0F, 1.0F);
            GL11.glEnable(32826);
            this.mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());

            for(int i1 = 0; i1 < 2; ++i1) {
                int j1 = i1 * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)j1);
                GL11.glRotatef((float)(-45 * j1), 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float)(-65 * j1), 0.0F, 1.0F, 0.0F);
                render = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
                renderplayer = (RenderPlayer)render;
                f10 = 1.0F;
                GL11.glScalef(f10, f10, f10);
                renderplayer.renderFirstPersonArm(this.mc.thePlayer);
                GL11.glPopMatrix();
            }

            f6 = entityclientplayermp.getSwingProgress(partialTicks);
            f7 = MathHelper.sin(f6 * f6 * 3.1415927F);
            f8 = MathHelper.sin(MathHelper.sqrt_float(f6) * 3.1415927F);
            GL11.glRotatef(-f7 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f8 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f8 * 80.0F, 1.0F, 0.0F, 0.0F);
            f9 = 0.38F;
            GL11.glScalef(f9, f9, f9);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
            f10 = 0.015625F;
            GL11.glScalef(f10, f10, f10);
            this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
            Tessellator tessellator = Tessellator.instance;
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            tessellator.startDrawingQuads();
            byte b0 = 7;
            tessellator.addVertexWithUV((double)(0 - b0), (double)(128 + b0), 0.0, 0.0, 1.0);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(128 + b0), 0.0, 1.0, 1.0);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(0 - b0), 0.0, 1.0, 0.0);
            tessellator.addVertexWithUV((double)(0 - b0), (double)(0 - b0), 0.0, 0.0, 0.0);
            tessellator.draw();
            IItemRenderer custom = MinecraftForgeClient.getItemRenderer(itemstack, IItemRenderer.ItemRenderType.FIRST_PERSON_MAP);
            MapData mapdata = ((ItemMap)itemstack.getItem()).getMapData(itemstack, this.mc.theWorld);
            if (custom == null) {
                if (mapdata != null) {
                    this.mc.entityRenderer.getMapItemRenderer().func_148250_a(mapdata, false);
                }
            } else {
                custom.renderItem(IItemRenderer.ItemRenderType.FIRST_PERSON_MAP, itemstack, new Object[]{this.mc.thePlayer, this.mc.getTextureManager(), mapdata});
            }

            GL11.glPopMatrix();
        } else if (!entityclientplayermp.isInvisible()) {
            GL11.glPushMatrix();
            f13 = 0.8F;
            f5 = entityclientplayermp.getSwingProgress(partialTicks);
            f6 = MathHelper.sin(f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glTranslatef(-f7 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F * 2.0F) * 0.4F, -f6 * 0.4F);
            GL11.glTranslatef(0.8F * f13, -0.75F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            f5 = entityclientplayermp.getSwingProgress(partialTicks);
            f6 = MathHelper.sin(f5 * f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glRotatef(f7 * 70.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
            GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(5.6F, 0.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            this.renderArm(entityclientplayermp);
            GL11.glPopMatrix();
        }

        if (itemstack != null && itemstack.getItem() instanceof ItemCloth) {
            GL11.glDisable(3042);
        }

        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
    }

    public void renderArm(AbstractClientPlayer player) {
        IArmRenderLayer vanilla = this.getLayer(ArmRenderLayerVanilla.class); // spaghetti!

        if (vanilla.needRender(player, this.render)) {
            if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, this.render))) {
                vanilla.renderTransformed(player);
            }
        }

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            if (layer == vanilla) {
                continue;
            }
            if (layer.needRender(player, this.render)) {
                try {
                    if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, this.render))) {
                        layer.renderTransformed(player);
                    }
                } catch (RMException rm) {
                    throw rm;
                } catch (Throwable tr) {
                    throw new RMException(tr).setFatalLayer(layer);
                }
            }
        }
    }
}
