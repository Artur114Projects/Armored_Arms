package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.util.Api;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    public List<ShapelessRL> renderArmWearList = null;
    public boolean currentArmorModelBiped = true;
    public ModelBiped baseArmorModel = null;
    public RenderPlayer renderPlayer = null;
    public ItemStack chestPlate = null;

    @Override
    public void update(AbstractClientPlayer player) {
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (this.chestPlate != chestPlate) {
            this.chestPlate = chestPlate;

            if (chestPlate.getItem() instanceof ItemArmor) {
                ModelBiped armor = chestPlate.getItem().getArmorModel(player, chestPlate, EntityEquipmentSlot.CHEST, this.baseArmorModel);
                this.currentArmorModelBiped = this.renderArmWearList.contains(new ShapelessRL(chestPlate.getItem().getRegistryName())) || armor == null || armor.getClass() == ModelBiped.class;
            }
        }
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
        if (player.isInvisible()) {
            return;
        }

        ModelPlayer playerModel = this.renderPlayer.getMainModel();

        ModelRenderer armWear = this.handFromModelPlayer(playerModel, handSide, true);
        ModelRenderer arm = this.handFromModelPlayer(playerModel, handSide, false);

        this.renderPlayer.bindTexture(player.getLocationSkin());
        this.render(armWear, handSide);
        this.render(arm, handSide);
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return renderManagerState;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
        this.baseArmorModel = new ModelBiped(1.0F);
        this.renderArmWearList = this.initRenderArmWearList();
    }

    public List<ShapelessRL> initRenderArmWearList() {
        List<ShapelessRL> ret = new ArrayList<>(AAConfig.renderArmWearList.length);
        for (String id : AAConfig.renderArmWearList) {
            ShapelessRL rl = new ShapelessRL(id);

            if (!rl.isEmpty()) {
                ret.add(rl);
            }
        }
        return ret;
    }

    public ModelRenderer handFromModelPlayer(ModelPlayer mb, EnumHandSide handSide, boolean wear) {
        if (wear && (AAConfig.disableArmWear && (!AAConfig.enableArmWearWithVanillaM || !this.currentArmorModelBiped))) {
            return null;
        }
        switch (handSide) {
            case RIGHT:
                return wear ? mb.bipedRightArmwear : mb.bipedRightArm;
            case LEFT:
                return wear ? mb.bipedLeftArmwear : mb.bipedLeftArm;
            default:
                return null;
        }
    }

    private void render(ModelRenderer arm, EnumHandSide handSide) {
        if (arm == null) {
            return;
        }
        arm.rotateAngleX = 0.0F;
        arm.rotateAngleY = 0.0F;
        arm.rotateAngleZ = 0.1F * MiscUtils.handSideDelta(handSide);
        arm.render(1.0F / 16.0F);
    }
}