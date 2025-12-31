package com.artur114.armoredarms.client.integration;

import com.artur114.armoredarms.api.events.AARenderLayerRenderingEvent;
import com.artur114.armoredarms.client.core.ArmRenderLayerVanilla;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.client.render.ModelClaws;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemCyberlimb;
import flaxbeard.cyberware.common.item.ItemHandUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventsRetranslatorCyberware {
    private static final ResourceLocation robo = new ResourceLocation("cyberware", "textures/models/player_robot.png");
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static ModelClaws claws = new ModelClaws(0.0F);
    private final Minecraft mc = Minecraft.getMinecraft();
    private RenderPlayer renderPlayer = null;
    private static boolean missingArm = false;
    private static boolean missingSecondArm = false;
    private static boolean hasRoboLeft = false;
    private static boolean hasRoboRight = false;
    private static EnumHandSide oldHand;
    private boolean died = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleMissingEssentials(LivingEvent.LivingUpdateEvent event) {
        if (this.died) {
            return;
        }

        try {
            EntityLivingBase entityLivingBase = event.getEntityLiving();
            if (entityLivingBase == this.mc.player) {
                ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(entityLivingBase);
                if (cyberwareUserData != null) {
                    GameSettings settings = Minecraft.getMinecraft().gameSettings;
                    boolean stillMissingArm = false;
                    boolean stillMissingSecondArm = false;
                    boolean leftUnpowered = false;
                    ItemStack armLeft = cyberwareUserData.getCyberware(CyberwareContent.cyberlimbs.getCachedStack(0));
                    if (!armLeft.isEmpty() && !ItemCyberlimb.isPowered(armLeft)) {
                        leftUnpowered = true;
                    }

                    boolean rightUnpowered = false;
                    ItemStack armRight = cyberwareUserData.getCyberware(CyberwareContent.cyberlimbs.getCachedStack(1));
                    if (!armRight.isEmpty() && !ItemCyberlimb.isPowered(armRight)) {
                        rightUnpowered = true;
                    }

                    boolean hasSkin = cyberwareUserData.isCyberwareInstalled(CyberwareContent.skinUpgrades.getCachedStack(2));
                    hasRoboLeft = !armLeft.isEmpty() && !hasSkin;
                    hasRoboRight = !armRight.isEmpty() && !hasSkin;
                    boolean hasRightArm = cyberwareUserData.hasEssential(ICyberware.EnumSlot.ARM, ICyberware.ISidedLimb.EnumSide.RIGHT) && !rightUnpowered;
                    boolean hasLeftArm = cyberwareUserData.hasEssential(ICyberware.EnumSlot.ARM, ICyberware.ISidedLimb.EnumSide.LEFT) && !leftUnpowered;
                    if (!hasRightArm) {
                        if (settings.mainHand != EnumHandSide.LEFT) {
                            oldHand = settings.mainHand;
                            settings.mainHand = EnumHandSide.LEFT;
                            settings.sendSettingsToServer();
                        }

                        missingArm = true;
                        stillMissingArm = true;
                        if (!hasLeftArm) {
                            missingSecondArm = true;
                            stillMissingSecondArm = true;
                        }
                    } else if (!hasLeftArm) {
                        if (settings.mainHand != EnumHandSide.RIGHT) {
                            oldHand = settings.mainHand;
                            settings.mainHand = EnumHandSide.RIGHT;
                            settings.sendSettingsToServer();
                        }

                        missingArm = true;
                        stillMissingArm = true;
                    }

                    if (!stillMissingArm) {
                        missingArm = false;
                        if (oldHand != null) {
                            settings.mainHand = oldHand;
                            settings.sendSettingsToServer();
                            oldHand = null;
                        }
                    }

                    if (!stillMissingSecondArm) {
                        missingSecondArm = false;
                    }
                }

            }
        } catch (Exception exp) {
            this.died = true;
            exp.printStackTrace(System.err);
        }
    }

    @SubscribeEvent
    public void renderSpecificHandEvent(RenderSpecificHandEvent e) {
        if (this.died) {
            return;
        }

        try {
            if ((hasRoboLeft || hasRoboRight) && e.getItemStack().isEmpty() || e.getItemStack().getItem() instanceof ItemMap) {
                if (e.getHand() == EnumHand.MAIN_HAND) {
                    if (this.renderPlayer == null) {
                        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(this.mc.player);
                    }
                    this.renderItemInFirstPerson(e);
                    e.setCanceled(true);
                }
            }
        } catch (Exception exp) {
            this.died = true;
            exp.printStackTrace(System.err);
        }
    }

    @SubscribeEvent
    public void aaRenderLayerRenderingEvent(AARenderLayerRenderingEvent e) {
        if (this.died) {
            return;
        }

        try {
            if (e.getHandSide() == EnumHandSide.RIGHT && missingArm) {
                e.setCanceled(true);
            }
            if (e.getHandSide() == EnumHandSide.LEFT && missingSecondArm) {
                e.setCanceled(true);
            }
        } catch (Exception exp) {
            this.died = true;
            exp.printStackTrace(System.err);
        }
    }

    public void renderItemInFirstPerson(RenderSpecificHandEvent e) {
        EntityPlayerSP player = this.mc.player;
        EnumHandSide enumhandside = e.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float interpPitch = e.getInterpolatedPitch();
        float swingProgress = e.getSwingProgress();
        float equipProgress = e.getEquipProgress();
        ItemStack stack = e.getItemStack();
        EnumHand hand = e.getHand();

        GlStateManager.pushMatrix();
        boolean flag = false;

        if (stack.isEmpty()) {
            if (hand == EnumHand.MAIN_HAND) {
                this.renderArmFirstPerson(equipProgress, swingProgress, enumhandside);

                flag = true;
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (hand == EnumHand.MAIN_HAND && player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
                this.renderMapFirstPerson(interpPitch, equipProgress, swingProgress, stack);
            } else {
                this.renderMapFirstPersonSide(equipProgress, enumhandside, swingProgress, stack);
            }

            flag = true;
        }

        if (flag) {
            e.setCanceled(true);
        }

        GlStateManager.popMatrix();
    }

    private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        boolean flag = p_187456_3_ != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(p_187456_2_);
        float f2 = -0.3F * MathHelper.sin(f1 * 3.1415927F);
        float f3 = 0.4F * MathHelper.sin(f1 * 6.2831855F);
        float f4 = -0.4F * MathHelper.sin(p_187456_2_ * 3.1415927F);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * 3.1415927F);
        float f6 = MathHelper.sin(f1 * 3.1415927F);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        AbstractClientPlayer abstractclientplayer = this.mc.player;
        this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        GlStateManager.disableCull();
        this.renderArm(this.mc.player, p_187456_3_);
        GlStateManager.enableCull();
    }

    private void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_) {
        float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);
        if (!this.mc.player.isInvisible()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
            this.renderArmFirstPerson(p_187465_1_, p_187465_3_, p_187465_2_);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
        float f1 = MathHelper.sqrt(p_187465_3_);
        float f2 = MathHelper.sin(f1 * 3.1415927F);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * 6.2831855F);
        float f5 = -0.3F * MathHelper.sin(p_187465_3_ * 3.1415927F);
        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
        this.renderMapFirstPerson(p_187465_4_);
        GlStateManager.popMatrix();
    }

    private void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_, ItemStack stack) {
        float f = MathHelper.sqrt(p_187463_3_);
        float f1 = -0.2F * MathHelper.sin(p_187463_3_ * 3.1415927F);
        float f2 = -0.4F * MathHelper.sin(f * 3.1415927F);
        GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.getMapAngleFromPitch(p_187463_1_);
        GlStateManager.translate(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
        this.renderArms();
        float f4 = MathHelper.sin(f * 3.1415927F);
        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.renderMapFirstPerson(stack);
    }

    private void renderMapFirstPerson(ItemStack stack) {
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
        vertexbuffer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
        vertexbuffer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
        vertexbuffer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        if (!stack.isEmpty()) {
            MapData mapdata = Items.FILLED_MAP.getMapData(stack, Minecraft.getMinecraft().world);
            if (mapdata != null) {
                this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }
        }

        GlStateManager.enableLighting();
    }

    private void renderArms() {
        if (!this.mc.player.isInvisible()) {
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            this.renderArm(EnumHandSide.RIGHT);
            this.renderArm(EnumHandSide.LEFT);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
        }
    }

    private void renderArm(EnumHandSide p_187455_1_) {
        this.mc.getTextureManager().bindTexture(this.mc.player.getLocationSkin());
        GlStateManager.pushMatrix();
        float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
        this.renderArm(this.mc.player, p_187455_1_);
        GlStateManager.popMatrix();
    }

    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * 3.1415927F) * 0.5F + 0.5F;
        return f;
    }

    public void renderArm(AbstractClientPlayer player, EnumHandSide side) {
        if (side == EnumHandSide.RIGHT && missingArm) {
            return;
        }
        if (side == EnumHandSide.LEFT && missingSecondArm) {
            return;
        }
        boolean flag = ArmoredArms.RENDER_ARM_MANAGER.getLayer(ArmRenderLayerVanilla.class).currentArmorModelBiped;
        switch (side) {
            case RIGHT:
                this.renderRightArm(player, !AAConfig.disableArmWear || player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() || (AAConfig.enableArmWearWithVanillaM && flag));
                break;
            case LEFT:
                this.renderLeftArm(player, !AAConfig.disableArmWear || player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() || (AAConfig.enableArmWearWithVanillaM && flag));
                break;
        }
    }

    public void renderRightArm(AbstractClientPlayer clientPlayer, boolean renderWear) {
        if (hasRoboRight) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
            this.renderRightArmMC(clientPlayer, renderWear);
        } else {
            this.renderRightArmMC(clientPlayer, renderWear);
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
        if (Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.RIGHT && clientPlayer.getHeldItemMainhand().isEmpty()) {
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
            if (cyberwareUserData != null) {
                ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(1));
                if (!itemStackClaws.isEmpty() && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(1)) && EnableDisableHelper.isEnabled(itemStackClaws)) {
                    GlStateManager.pushMatrix();
                    float percent = ((float)Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4.0F;
                    percent = Math.min(1.0F, percent);
                    percent = Math.max(0.0F, percent);
                    percent = (float)Math.sin((double)percent * Math.PI / 2.0);
                    claws.claw1.rotateAngleY = 0.0F;
                    claws.claw1.rotateAngleZ = this.renderPlayer.getMainModel().bipedRightArm.rotateAngleZ;
                    claws.claw1.rotateAngleX = 0.0F;
                    claws.claw1.setRotationPoint(-5.0F, -5.0F + 7.0F * percent, 0.0F);
                    claws.claw1.render(0.0625F);
                    GlStateManager.popMatrix();
                }

            }
        }
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer, boolean renderWear) {
        if (hasRoboLeft) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
            this.renderLeftArmMC(clientPlayer, renderWear);
        } else {
            this.renderLeftArmMC(clientPlayer, renderWear);
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(robo);
        if (Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.LEFT && clientPlayer.getHeldItemMainhand().isEmpty()) {
            ICyberwareUserData cyberwareUserData = CyberwareAPI.getCapabilityOrNull(clientPlayer);
            if (cyberwareUserData != null) {
                ItemStack itemStackClaws = cyberwareUserData.getCyberware(CyberwareContent.handUpgrades.getCachedStack(1));
                if (!itemStackClaws.isEmpty() && cyberwareUserData.isCyberwareInstalled(CyberwareContent.cyberlimbs.getCachedStack(0)) && EnableDisableHelper.isEnabled(itemStackClaws)) {
                    GlStateManager.pushMatrix();
                    float percent = ((float)Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks() - ItemHandUpgrade.clawsTime) / 4.0F;
                    percent = Math.min(1.0F, percent);
                    percent = Math.max(0.0F, percent);
                    percent = (float)Math.sin((double)percent * Math.PI / 2.0);
                    claws.claw1.rotateAngleY = 0.0F;
                    claws.claw1.rotateAngleZ = this.renderPlayer.getMainModel().bipedLeftArm.rotateAngleZ;
                    claws.claw1.rotateAngleX = 0.0F;
                    claws.claw1.setRotationPoint(8.0F, -5.0F + 7.0F * percent, 0.0F);
                    claws.claw1.render(0.0625F);
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
