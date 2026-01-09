package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.api.events.AARenderLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.integration.ModsList;
import com.artur114.armoredarms.client.util.EnumHandSide;
import com.artur114.armoredarms.client.util.RMException;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import thaumcraft.common.items.relics.ItemThaumometer;
import xonin.backhand.api.core.BackhandUtils;
import xonin.backhand.api.core.IBackhandPlayer;
import xonin.backhand.utils.Mods;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderArmManager {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = null;
    private final RenderBlocks renderBlocksIr = new RenderBlocks();
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
                this.renderItemInFirstPerson(EnumHandSide.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
                if (ModsList.BACKHAND.isLoaded() && BackhandUtils.getOffhandItem(this.mc.thePlayer) != null && !this.isUsed2Arm(this.itemRenderer.itemToRender)) {
                    GL11.glEnable(2884);
                    GL11.glCullFace(1028);
                    GL11.glPushMatrix();
                    GL11.glScalef(-1.0F, 1.0F, 1.0F);
                    EntityPlayerSP player = this.mc.thePlayer;
                    float f3 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * e.partialTicks;
                    float f4 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * e.partialTicks;
                    GL11.glRotatef((player.rotationPitch - f3) * -0.1F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef((player.rotationYaw - f4) * -0.1F, 0.0F, 1.0F, 0.0F);
                    this.renderItemInFirstPerson(EnumHandSide.LEFT, e.partialTicks, ((IBackhandPlayer) player).getOffSwingProgress(e.partialTicks), BackhandUtils.getOffhandItem(this.mc.thePlayer));
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

    public void renderItemInFirstPerson(EnumHandSide side, float partialTicks, float swingProgress, ItemStack itemstack) {
        float equippedProgress = this.itemRenderer.prevEquippedProgress + (this.itemRenderer.equippedProgress - this.itemRenderer.prevEquippedProgress) * partialTicks;
        EntityClientPlayerMP entityclientplayermp = this.mc.thePlayer;
        float rotationPitch = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * partialTicks;
        GL11.glPushMatrix();
        GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        float renderArmPitch = entityclientplayermp.prevRenderArmPitch + (entityclientplayermp.renderArmPitch - entityclientplayermp.prevRenderArmPitch) * partialTicks;
        float renderArmYaw = entityclientplayermp.prevRenderArmYaw + (entityclientplayermp.renderArmYaw - entityclientplayermp.prevRenderArmYaw) * partialTicks;
        GL11.glRotatef((entityclientplayermp.rotationPitch - renderArmPitch) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - renderArmYaw) * 0.1F, 0.0F, 1.0F, 0.0F);
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
        float f8;
        if (itemstack != null && itemstack.getItem() instanceof ItemMap) {
            GL11.glPushMatrix();
            f13 = 0.8F;
            f5 = swingProgress;
            f6 = MathHelper.sin(f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F * 2.0F) * 0.2F, -f6 * 0.2F);
            f5 = 1.0F - rotationPitch / 45.0F + 0.1F;
            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            f5 = -MathHelper.cos(f5 * 3.1415927F) * 0.5F + 0.5F;
            GL11.glTranslatef(0.0F, 0.0F * f13 - (1.0F - equippedProgress) * 1.2F - f5 * 0.5F + 0.04F, -0.9F * f13);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f5 * -85.0F, 0.0F, 0.0F, 1.0F);
            GL11.glEnable(32826);

            for(EnumHandSide sideH : EnumHandSide.values()) {
                int i1 = sideH == EnumHandSide.RIGHT ? 1 : 0;
                GL11.glPushMatrix();
                GL11.glRotatef(92.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-41.0F * sideH.delta(), 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.3F * sideH.delta(), -1.1F, 0.45F);
                this.renderArm(entityclientplayermp, sideH);
                GL11.glPopMatrix();
            }

            f6 = swingProgress;
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
        } else if (itemstack != null) {
            GL11.glPushMatrix();
            f13 = 0.8F;
            if (entityclientplayermp.getItemInUseCount() > 0) {
                EnumAction enumaction = itemstack.getItemUseAction();
                if (enumaction == EnumAction.eat || enumaction == EnumAction.drink) {
                    f6 = (float)entityclientplayermp.getItemInUseCount() - partialTicks + 1.0F;
                    f7 = 1.0F - f6 / (float)itemstack.getMaxItemUseDuration();
                    f8 = 1.0F - f7;
                    f8 = f8 * f8 * f8;
                    f8 = f8 * f8 * f8;
                    f8 = f8 * f8 * f8;
                    f9 = 1.0F - f8;
                    GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f6 / 4.0F * 3.1415927F) * 0.1F) * (float)((double)f7 > 0.2 ? 1 : 0), 0.0F);
                    GL11.glTranslatef(f9 * 0.6F, -f9 * 0.5F, 0.0F);
                    GL11.glRotatef(f9 * 90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(f9 * 10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f9 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                f5 = swingProgress;
                f6 = MathHelper.sin(f5 * 3.1415927F);
                f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
                GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F * 2.0F) * 0.2F, -f6 * 0.2F);
            }

            GL11.glTranslatef(0.7F * f13, -0.65F * f13 - (1.0F - equippedProgress) * 0.6F, -0.9F * f13);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            f5 = swingProgress;
            f6 = MathHelper.sin(f5 * f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
            f8 = 0.4F;
            GL11.glScalef(f8, f8, f8);
            float f12;
            float f11;
            if (entityclientplayermp.getItemInUseCount() > 0) {
                EnumAction enumaction1 = itemstack.getItemUseAction();
                if (enumaction1 == EnumAction.block) {
                    GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                    GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                } else if (enumaction1 == EnumAction.bow) {
                    GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                    f10 = (float)itemstack.getMaxItemUseDuration() - ((float)entityclientplayermp.getItemInUseCount() - partialTicks + 1.0F);
                    f11 = f10 / 20.0F;
                    f11 = (f11 * f11 + f11 * 2.0F) / 3.0F;
                    if (f11 > 1.0F) {
                        f11 = 1.0F;
                    }

                    if (f11 > 0.1F) {
                        GL11.glTranslatef(0.0F, MathHelper.sin((f10 - 0.1F) * 1.3F) * 0.01F * (f11 - 0.1F), 0.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.0F, f11 * 0.1F);
                    GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    f12 = 1.0F + f11 * 0.2F;
                    GL11.glScalef(1.0F, 1.0F, f12);
                    GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                }
            }

            if (itemstack.getItem().shouldRotateAroundWhenRendering()) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (itemstack.getItem().requiresMultipleRenderPasses()) {
                this.renderItem(entityclientplayermp, itemstack, 0, IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);

                for(int x = 1; x < itemstack.getItem().getRenderPasses(itemstack.getMetadata()); ++x) {
                    int k1 = itemstack.getItem().getColorFromItemStack(itemstack, x);
                    f10 = (float)(k1 >> 16 & 255) / 255.0F;
                    f11 = (float)(k1 >> 8 & 255) / 255.0F;
                    f12 = (float)(k1 & 255) / 255.0F;
                    GL11.glColor4f(1.0F * f10, 1.0F * f11, 1.0F * f12, 1.0F);
                    this.renderItem(entityclientplayermp, itemstack, x, IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
                }
            } else {
                this.renderItem(entityclientplayermp, itemstack, 0, IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
            }

            GL11.glPopMatrix();
        } else if (!entityclientplayermp.isInvisible()) {
            GL11.glPushMatrix();
            f13 = 0.8F;
            f5 = swingProgress;
            f6 = MathHelper.sin(f5 * 3.1415927F);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F);
            GL11.glTranslatef(-f7 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f5) * 3.1415927F * 2.0F) * 0.4F, -f6 * 0.4F);
            GL11.glTranslatef(0.8F * f13, -0.75F * f13 - (1.0F - equippedProgress) * 0.6F, -0.9F * f13);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            f5 = swingProgress;
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
            this.renderArm(entityclientplayermp, side);
            GL11.glPopMatrix();
        }

        if (itemstack != null && itemstack.getItem() instanceof ItemCloth) {
            GL11.glDisable(3042);
        }

        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
    }


    public void renderItem(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, IItemRenderer.ItemRenderType type) {
        GL11.glPushMatrix();
        TextureManager texturemanager = this.mc.getTextureManager();
        Item item = p_78443_2_.getItem();
        Block block = Block.getBlockFromItem(item);
        if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0) {
            GL11.glEnable(3042);
            GL11.glEnable(2884);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(p_78443_2_, type);
        if (customRenderer != null) {
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            ForgeHooksClient.renderEquippedItem(type, customRenderer, this.renderBlocksIr, p_78443_1_, p_78443_2_);
        } else if (p_78443_2_.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            texturemanager.bindTexture(texturemanager.getResourceLocation(0));
            if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0) {
                GL11.glDepthMask(false);
                this.renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getMetadata(), 1.0F);
                GL11.glDepthMask(true);
            } else {
                this.renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getMetadata(), 1.0F);
            }
        } else {
            IIcon iicon = p_78443_1_.getItemIcon(p_78443_2_, p_78443_3_);
            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }

            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            float f = iicon.getMinU();
            float f1 = iicon.getMaxU();
            float f2 = iicon.getMinV();
            float f3 = iicon.getMaxV();
            float f4 = 0.0F;
            float f5 = 0.3F;
            GL11.glEnable(32826);
            GL11.glTranslatef(-f4, -f5, 0.0F);
            float f6 = 1.5F;
            GL11.glScalef(f6, f6, f6);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
            if (p_78443_2_.hasEffect(p_78443_3_)) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                texturemanager.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
            }

            GL11.glDisable(32826);
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }

        if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0) {
            GL11.glDisable(3042);
        }

        GL11.glPopMatrix();
    }

    public static void renderItemIn2D(Tessellator p_78439_0_, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_) {
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 0.0F, 1.0F);
        p_78439_0_.addVertexWithUV(0.0, 0.0, 0.0, (double)p_78439_1_, (double)p_78439_4_);
        p_78439_0_.addVertexWithUV(1.0, 0.0, 0.0, (double)p_78439_3_, (double)p_78439_4_);
        p_78439_0_.addVertexWithUV(1.0, 1.0, 0.0, (double)p_78439_3_, (double)p_78439_2_);
        p_78439_0_.addVertexWithUV(0.0, 1.0, 0.0, (double)p_78439_1_, (double)p_78439_2_);
        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 0.0F, -1.0F);
        p_78439_0_.addVertexWithUV(0.0, 1.0, (double)(0.0F - p_78439_7_), (double)p_78439_1_, (double)p_78439_2_);
        p_78439_0_.addVertexWithUV(1.0, 1.0, (double)(0.0F - p_78439_7_), (double)p_78439_3_, (double)p_78439_2_);
        p_78439_0_.addVertexWithUV(1.0, 0.0, (double)(0.0F - p_78439_7_), (double)p_78439_3_, (double)p_78439_4_);
        p_78439_0_.addVertexWithUV(0.0, 0.0, (double)(0.0F - p_78439_7_), (double)p_78439_1_, (double)p_78439_4_);
        p_78439_0_.draw();
        float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float)p_78439_5_;
        float f6 = 0.5F * (p_78439_4_ - p_78439_2_) / (float)p_78439_6_;
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(-1.0F, 0.0F, 0.0F);

        int k;
        float f7;
        float f8;
        for(k = 0; k < p_78439_5_; ++k) {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            p_78439_0_.addVertexWithUV((double)f7, 0.0, (double)(0.0F - p_78439_7_), (double)f8, (double)p_78439_4_);
            p_78439_0_.addVertexWithUV((double)f7, 0.0, 0.0, (double)f8, (double)p_78439_4_);
            p_78439_0_.addVertexWithUV((double)f7, 1.0, 0.0, (double)f8, (double)p_78439_2_);
            p_78439_0_.addVertexWithUV((double)f7, 1.0, (double)(0.0F - p_78439_7_), (double)f8, (double)p_78439_2_);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(1.0F, 0.0F, 0.0F);

        float f9;
        for(k = 0; k < p_78439_5_; ++k) {
            f7 = (float)k / (float)p_78439_5_;
            f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            f9 = f7 + 1.0F / (float)p_78439_5_;
            p_78439_0_.addVertexWithUV((double)f9, 1.0, (double)(0.0F - p_78439_7_), (double)f8, (double)p_78439_2_);
            p_78439_0_.addVertexWithUV((double)f9, 1.0, 0.0, (double)f8, (double)p_78439_2_);
            p_78439_0_.addVertexWithUV((double)f9, 0.0, 0.0, (double)f8, (double)p_78439_4_);
            p_78439_0_.addVertexWithUV((double)f9, 0.0, (double)(0.0F - p_78439_7_), (double)f8, (double)p_78439_4_);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, 1.0F, 0.0F);

        for(k = 0; k < p_78439_6_; ++k) {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            f9 = f7 + 1.0F / (float)p_78439_6_;
            p_78439_0_.addVertexWithUV(0.0, (double)f9, 0.0, (double)p_78439_1_, (double)f8);
            p_78439_0_.addVertexWithUV(1.0, (double)f9, 0.0, (double)p_78439_3_, (double)f8);
            p_78439_0_.addVertexWithUV(1.0, (double)f9, (double)(0.0F - p_78439_7_), (double)p_78439_3_, (double)f8);
            p_78439_0_.addVertexWithUV(0.0, (double)f9, (double)(0.0F - p_78439_7_), (double)p_78439_1_, (double)f8);
        }

        p_78439_0_.draw();
        p_78439_0_.startDrawingQuads();
        p_78439_0_.setNormal(0.0F, -1.0F, 0.0F);

        for(k = 0; k < p_78439_6_; ++k) {
            f7 = (float)k / (float)p_78439_6_;
            f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            p_78439_0_.addVertexWithUV(1.0, (double)f7, 0.0, (double)p_78439_3_, (double)f8);
            p_78439_0_.addVertexWithUV(0.0, (double)f7, 0.0, (double)p_78439_1_, (double)f8);
            p_78439_0_.addVertexWithUV(0.0, (double)f7, (double)(0.0F - p_78439_7_), (double)p_78439_1_, (double)f8);
            p_78439_0_.addVertexWithUV(1.0, (double)f7, (double)(0.0F - p_78439_7_), (double)p_78439_3_, (double)f8);
        }

        p_78439_0_.draw();
    }

    private boolean isUsed2Arm(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemMap || ModsList.THAUMCRAFT.isLoaded() && itemStack.getItem() instanceof ItemThaumometer);
    }


    public void renderArm(AbstractClientPlayer player, EnumHandSide side) {
        IArmRenderLayer vanilla = this.getLayer(ArmRenderLayerVanilla.class); // spaghetti!

        if (vanilla.needRender(player, this.render)) {
            if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, this.render))) {
                vanilla.renderTransformed(player, side);
            }
        }

        for (IArmRenderLayer layer : this.renderLayers.values()) {
            if (layer == vanilla) {
                continue;
            }
            if (layer.needRender(player, this.render)) {
                try {
                    if (!MinecraftForge.EVENT_BUS.post(new AARenderLayerRenderingEvent(vanilla, this.render))) {
                        layer.renderTransformed(player, side);
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
