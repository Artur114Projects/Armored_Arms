package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.BoneAdapterModelRenderer;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.List;
import java.util.Map;

public class ArmRenderEngineForge extends AbstractRenderEngineForge<ArmRenderEngineForge, ArmRenderPipelineForge> {
    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineForge.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    protected List<IBoneAdapter<?>> initBoneAdapters() {
        InitBoneAdaptersEvent event = new InitBoneAdaptersEvent(this.mod);
        event.registerAdapter(new BoneAdapterModelRenderer());
        this.mod.post(event);
        return event.adaptersList();
    }

    @Override
    public boolean onLayerRendering(IArmRenderLayer<ArmRenderEngineForge> layer, EnumHandSideAA side) {
        return !MinecraftForge.EVENT_BUS.post(new ArmLayerRenderingEvent(layer, side));
    }

    @Override
    public void render(ArmRenderPipelineForge context) {
        this.renderHand(context.renderContext);
    }

    @Override
    public void tick(ArmRenderPipelineForge context) {
        this.render = this.updateAllLayers();
    }

    public void renderArm(EnumHandSideAA handSide) {
        this.renderAllLayers(handSide);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public Class<ArmRenderPipelineForge> targetPipeline() {
        return ArmRenderPipelineForge.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }

    /*----------------------------------------------GOD_ENDS_HERE----------------------------------------------*/
    /*----------------------------------------MINECRAFT_SHIT_CODE_START----------------------------------------*/

    public void renderHand(RenderHandEvent e) {
        if (this.entityRenderer.cameraZoom == 1.0D) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            this.renderHand(e, e.partialTicks, e.renderPass);
            e.setCanceled(true);
        }
    }

    protected void renderHand(RenderHandEvent e, float partialTicks, int renderPass)
    {
        if (this.entityRenderer.debugViewDirection <= 0)
        {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            float f1 = 0.07F;

            if (this.mc.gameSettings.anaglyph)
            {
                GL11.glTranslatef((float)(-(renderPass * 2 - 1)) * f1, 0.0F, 0.0F);
            }

            if (this.entityRenderer.cameraZoom != 1.0D)
            {
                GL11.glTranslatef((float)this.entityRenderer.cameraYaw, (float)(-this.entityRenderer.cameraPitch), 0.0F);
                GL11.glScaled(this.entityRenderer.cameraZoom, this.entityRenderer.cameraZoom, 1.0D);
            }

            Project.gluPerspective(this.entityRenderer.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.entityRenderer.farPlaneDistance * 2.0F);

            if (this.mc.playerController.enableEverythingIsScrewedUpMode())
            {
                float f2 = 0.6666667F;
                GL11.glScalef(1.0F, f2, 1.0F);
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            if (this.mc.gameSettings.anaglyph)
            {
                GL11.glTranslatef((float)(renderPass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            this.entityRenderer.hurtCameraEffect(partialTicks);

            if (this.mc.gameSettings.viewBobbing)
            {
                this.entityRenderer.setupViewBobbing(partialTicks);
            }

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode())
            {
                this.entityRenderer.enableLightmap(partialTicks);
                this.renderItemInFirstPerson(EnumHandSideAA.RIGHT, e.partialTicks, this.mc.thePlayer.getSwingProgress(e.partialTicks), this.itemRenderer.itemToRender);
                this.entityRenderer.disableLightmap(partialTicks);
            }

            GL11.glPopMatrix();

            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping())
            {
                this.itemRenderer.renderOverlays(partialTicks);
                this.entityRenderer.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.entityRenderer.setupViewBobbing(partialTicks);
            }
        }
    }

    public void renderItemInFirstPerson(EnumHandSideAA side, float partialTicks, float swingProgress, ItemStack itemstack) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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

            for(EnumHandSideAA sideH : EnumHandSideAA.values()) {
                int i1 = sideH == EnumHandSideAA.RIGHT ? 1 : 0;
                GL11.glPushMatrix();
                GL11.glRotatef(92.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-41.0F * sideH.delta(), 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.3F * sideH.delta(), -1.1F, 0.45F);
                this.renderArm(sideH);
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
            this.renderArm(side);
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
}
