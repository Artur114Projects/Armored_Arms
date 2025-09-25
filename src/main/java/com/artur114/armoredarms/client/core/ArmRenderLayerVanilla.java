package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.RenderArmManager;
import com.artur114.armoredarms.client.util.Api;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
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

@Api.Unstable
@SideOnly(Side.CLIENT)
public class ArmRenderLayerVanilla implements IArmRenderLayer {
    public ResourceLocation currentPlayerTex = null;
    public boolean currentArmorModelBiped = false;
    public ModelPlayer currentPlayerModel = null;
    public ModelBiped baseArmorModel = null;
    public RenderPlayer renderPlayer = null;
    public ItemStack chestPlate = null;

    @Override
    public void update(AbstractClientPlayer player) {
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (this.chestPlate != chestPlate) {
            this.chestPlate = chestPlate;

            this.currentPlayerTex = this.renderPlayer.getEntityTexture(player);
            this.currentPlayerModel = this.renderPlayer.getMainModel();

            if (chestPlate.getItem() instanceof ItemArmor) {
                ModelBiped armor = chestPlate.getItem().getArmorModel(player, chestPlate, EntityEquipmentSlot.CHEST, this.baseArmorModel);
                if (armor != null) this.currentArmorModelBiped = armor.getClass() == ModelBiped.class;
            }
        }
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide handSide) {
        if (player.isInvisible()) {
            return;
        }

        ModelRenderer armWear = this.handFromModelPlayer(this.currentPlayerModel, handSide, true);
        ModelRenderer arm = this.handFromModelPlayer(this.currentPlayerModel, handSide, false);

        this.renderPlayer.bindTexture(this.currentPlayerTex);
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
    }

    public ModelRenderer handFromModelPlayer(ModelPlayer mb, EnumHandSide handSide, boolean wear) {
        if (wear && AAConfig.disableArmWear && !(AAConfig.enableArmWearWithVanillaM && this.currentArmorModelBiped)) {
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