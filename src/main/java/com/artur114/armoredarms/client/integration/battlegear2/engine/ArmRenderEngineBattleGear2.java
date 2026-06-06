package com.artur114.armoredarms.client.integration.battlegear2.engine;

import com.artur114.armoredarms.client.integration.backhand.engine.ArmRenderEngineBackHand;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderHandEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xonin.backhand.api.core.BackhandUtils;
import xonin.backhand.api.core.IBackhandPlayer;
import xonin.backhand.client.hooks.ItemRendererHooks;
import xonin.backhand.utils.BackhandConfigClient;

public class ArmRenderEngineBattleGear2 extends ArmRenderEngineBackHand {

    @Override
    protected void renderHand(RenderHandEvent e, float partialTicks, int renderPass) {
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


    @Override
    public void renderItemInFirstPerson(EnumHandSideAA side, float partialTicks, float swingProgress, ItemStack itemstack) {
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
        if (itemstack != null) {
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        int i = this.mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (itemstack != null) {
            int l = itemstack.getItem().getColorFromItemStack(itemstack, 0);
            float f5 = (float)(l >> 16 & 255) / 255.0F;
            float f6 = (float)(l >> 8 & 255) / 255.0F;
            float f7 = (float)(l & 255) / 255.0F;
            GL11.glColor4f(f5, f6, f7, 1.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (itemstack != null && itemstack.getItem() instanceof ItemMap) {
            GL11.glPushMatrix();
            float f13 = 0.8F;
            float f5 = entityclientplayermp.getSwingProgress(partialTicks);
            float f6 = MathHelper.sin(f5 * (float)Math.PI);
            float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
            GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI * 2.0F) * 0.2F, -f6 * 0.2F);
            f5 = 1.0F - f2 / 45.0F + 0.1F;
            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            f5 = -MathHelper.cos(f5 * (float)Math.PI) * 0.5F + 0.5F;
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
                this.renderArm(side);
                GL11.glPopMatrix();
            }

            f6 = entityclientplayermp.getSwingProgress(partialTicks);
            f7 = MathHelper.sin(f6 * f6 * (float)Math.PI);
            float f8 = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI);
            GL11.glRotatef(-f7 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f8 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f8 * 80.0F, 1.0F, 0.0F, 0.0F);
            float f9 = 0.38F;
            GL11.glScalef(f9, f9, f9);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
            float f10 = 0.015625F;
            GL11.glScalef(f10, f10, f10);
            this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
            Tessellator tessellator = TessellatorManager.get();
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            tessellator.startDrawingQuads();
            byte b0 = 7;
            tessellator.addVertexWithUV((double)(0 - b0), (double)(128 + b0), (double)0.0F, (double)0.0F, (double)1.0F);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(128 + b0), (double)0.0F, (double)1.0F, (double)1.0F);
            tessellator.addVertexWithUV((double)(128 + b0), (double)(0 - b0), (double)0.0F, (double)1.0F, (double)0.0F);
            tessellator.addVertexWithUV((double)(0 - b0), (double)(0 - b0), (double)0.0F, (double)0.0F, (double)0.0F);
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
            float f13 = 0.8F;
            if (this.modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(entityclientplayermp.getItemInUseCount()) > 0) {
                EnumAction enumaction = itemstack.getItemUseAction();
                if (enumaction == EnumAction.eat || enumaction == EnumAction.drink) {
                    float f6 = (float)this.modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(entityclientplayermp.getItemInUseCount()) - partialTicks + 1.0F;
                    float f7 = 1.0F - f6 / (float)itemstack.getMaxItemUseDuration();
                    float f8 = 1.0F - f7;
                    f8 = f8 * f8 * f8;
                    f8 = f8 * f8 * f8;
                    f8 = f8 * f8 * f8;
                    float f9 = 1.0F - f8;
                    GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f6 / 4.0F * (float)Math.PI) * 0.1F) * (float)((double)f7 > 0.2 ? 1 : 0), 0.0F);
                    GL11.glTranslatef(f9 * 0.6F, -f9 * 0.5F, 0.0F);
                    GL11.glRotatef(f9 * 90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(f9 * 10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f9 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                float f5 = entityclientplayermp.getSwingProgress(partialTicks);
                float f6 = MathHelper.sin(f5 * (float)Math.PI);
                float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
                GL11.glTranslatef(-f7 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI * 2.0F) * 0.2F, -f6 * 0.2F);
            }

            GL11.glTranslatef(0.7F * f13, -0.65F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            float f5 = entityclientplayermp.getSwingProgress(partialTicks);
            float f6 = MathHelper.sin(f5 * f5 * (float)Math.PI);
            float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
            float f8 = 0.4F;
            GL11.glScalef(f8, f8, f8);
            if (this.modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(entityclientplayermp.getItemInUseCount()) > 0) {
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
                    float f10 = (float)itemstack.getMaxItemUseDuration() - ((float)this.modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(entityclientplayermp.getItemInUseCount()) - partialTicks + 1.0F);
                    float f11 = f10 / 20.0F;
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
                    float f12 = 1.0F + f11 * 0.2F;
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
                    float f10 = (float)(k1 >> 16 & 255) / 255.0F;
                    float f11 = (float)(k1 >> 8 & 255) / 255.0F;
                    float f12 = (float)(k1 & 255) / 255.0F;
                    GL11.glColor4f(1.0F * f10, 1.0F * f11, 1.0F * f12, 1.0F);
                    this.renderItem(entityclientplayermp, itemstack, x, IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
                }
            } else {
                this.renderItem(entityclientplayermp, itemstack, 0, IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
            }

            GL11.glPopMatrix();
        } else if (!this.modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(entityclientplayermp.isInvisible())) {
            GL11.glPushMatrix();
            float f13 = 0.8F;
            float f5 = entityclientplayermp.getSwingProgress(partialTicks);
            float f6 = MathHelper.sin(f5 * (float)Math.PI);
            float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
            GL11.glTranslatef(-f7 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI * 2.0F) * 0.4F, -f6 * 0.4F);
            GL11.glTranslatef(0.8F * f13, -0.75F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            f5 = entityclientplayermp.getSwingProgress(partialTicks);
            f6 = MathHelper.sin(f5 * f5 * (float)Math.PI);
            f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float)Math.PI);
            GL11.glRotatef(f7 * 70.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
            GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
            GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(5.6F, 0.0F, 0.0F);
            this.renderArm(side);
            GL11.glPopMatrix();
        }

        if (itemstack != null) {
            GL11.glDisable(3042);
        }

        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        this.handler$zbc000$backhand$renderItemInFirstPerson(partialTicks, null);
        this.handler$zbo001$battlegear2$renderItemInFirstPerson(partialTicks, null);
    }

    private boolean modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(boolean original) {
        if (BackhandConfigClient.RenderEmptyOffhandAtRest) {
            return original;
        } else {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            return BackhandUtils.isUsingOffhand(player) ? true : original;
        }
    }

    private int modifyExpressionValue$zbc000$backhand$renderItemInFirstPerson(int original) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        ItemStack offhand = BackhandUtils.getOffhandItem(player);
        if (offhand == null) {
            return original;
        } else if (BackhandUtils.isUsingOffhand(player)) {
            return ((IBackhandPlayer)player).isOffhandItemInUse() ? original : 0;
        } else {
            return ((IBackhandPlayer)player).isOffhandItemInUse() ? 0 : original;
        }
    }

    private void handler$zbo001$battlegear2$renderItemInFirstPerson(float p_78440_1_, CallbackInfo ci) {
        BattlegearRenderHelper.renderItemInFirstPerson(p_78440_1_, this.mc, this.itemRenderer);
    }

    private void handler$zbc000$backhand$renderItemInFirstPerson(float frame, CallbackInfo ci) {
        ItemRendererHooks.renderOffhandReturn(frame);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGHEST;
    }
}
