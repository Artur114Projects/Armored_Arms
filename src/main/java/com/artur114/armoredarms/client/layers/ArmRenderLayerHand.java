package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

import java.util.ArrayList;
import java.util.List;

public class ArmRenderLayerHand implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    public List<ShapelessLocation> renderArmWearList = null;
    public boolean currentArmorModelBiped = true;
    public ModelBiped baseArmorModel = null;
    public RenderPlayer renderPlayer = null;
    public ItemStack chestPlate = null;
    public boolean active = true;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        AbstractClientPlayer player = engine.mc.player;
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (this.chestPlate != chestPlate) {
            this.chestPlate = chestPlate;

            if (chestPlate.getItem() instanceof ItemArmor) {
                ModelBiped armor = chestPlate.getItem().getArmorModel(player, chestPlate, EntityEquipmentSlot.CHEST, this.baseArmorModel);
                this.currentArmorModelBiped = this.renderArmWearList.contains(AAUtils.fromMc(chestPlate.getItem().getRegistryName())) || armor == null || armor.getClass() == ModelBiped.class;
            }
        }
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        AbstractClientPlayer player = engine.mc.player;
        if (player.isInvisible() || !this.active) {
            return;
        }

        this.renderPlayer.bindTexture(player.getLocationSkin());
        this.renderArm(player, handSide);
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) engine.mc.getRenderManager().<AbstractClientPlayer>getEntityRenderObject(engine.mc.player);
        this.baseArmorModel = new ModelBiped(1.0F);
        this.renderArmWearList = this.initRenderArmWearList();
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        return renderEngineState;
    }

    public List<ShapelessLocation> initRenderArmWearList() {
        List<ShapelessLocation> ret = new ArrayList<>(AAConfig.renderArmWearList.length);
        for (String id : AAConfig.renderArmWearList) {
            ShapelessLocation rl = ShapelessLocation.location(id);

            if (!rl.isEmpty()) {
                ret.add(rl);
            }
        }
        return ret;
    }

    public void renderArm(AbstractClientPlayer player, EnumHandSideAA side) {
        switch (side) {
            case RIGHT:
                this.renderRightArmMC(player, !AAConfig.disableArmWear || (AAConfig.enableArmWearWithVanillaM && this.currentArmorModelBiped));
            break;
            case LEFT:
                this.renderLeftArmMC(player, !AAConfig.disableArmWear || (AAConfig.enableArmWearWithVanillaM && this.currentArmorModelBiped));
            break;
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

    @Override
    @SuppressWarnings("unchecked")
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return (Class<AbstractRenderEngineForge<?,?>>) (Class<?>) AbstractRenderEngineForge.class;
    }

    @Override
    public void deactivate() {
        this.active = false;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
