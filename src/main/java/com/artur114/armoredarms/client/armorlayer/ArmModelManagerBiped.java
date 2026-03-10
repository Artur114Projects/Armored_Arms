package com.artur114.armoredarms.client.armorlayer;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.client.util.TextureEnchant;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.client.util.TextureRLRGB;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.armorlayer.IArmModelManager;
import com.artur114.armoredarms.core.api.armorlayer.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.armorlayer.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ArmModelManagerBiped implements IArmModelManager<ArmModelManagerBiped, ArmRenderLayerArmor> {
    private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
    private double modelSize = AAConfig.vanillaArmorModelSize;


    public AbstractClientPlayer player = null;
    public LayerBipedArmor armorLayer = null;
    public IMultiTexture texture = null;
    public AAItemStack stack = null;
    public ModelBiped model = null;

    @Override
    public void render(ArmRenderLayerArmor layer, IArmModelRenderer<ArmModelManagerBiped> renderer, EnumHandSideAA side) {
        this.armorLayer = layer.armorLayer;
        this.stack = layer.chestPlate;
        this.player = layer.player;

        renderer.renderArm(this, side);

        this.armorLayer = null;
        this.player = null;
        this.stack = null;
    }

    @Override
    public IArmModelRenderer<ArmModelManagerBiped> cacheRenderer(ArmRenderLayerArmor layer, IArmModelRenderContainer<ArmModelManagerBiped> container) {
        this.armorLayer = layer.armorLayer;
        this.stack = layer.chestPlate;
        this.player = layer.player;
        this.texture = this.textures(this.player, this.armorLayer, this.stack);
        this.model = this.model(this.player, this.stack);

        IArmModelRenderer<ArmModelManagerBiped> model = container.create(this);

        this.armorLayer = null;
        this.texture = null;
        this.player = null;
        this.stack = null;
        this.model = null;

        return model;
    }


    public IMultiTexture textures(AbstractClientPlayer player, LayerBipedArmor armorLayer, AAItemStack stack) {
        ResourceLocation armor = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, null);
        ResourceLocation overlay = null;
        if (stack.item().hasOverlay(stack.stack())) overlay = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, "overlay");


        List<ITexture> textures = new ArrayList<>(2);
        if (overlay != null) {
            textures.add(new TextureRL(overlay, Priority.HIGH));
            textures.add(new TextureRLRGB(armor, stack.item().getColor(stack.stack())));
        } else {
            textures.add(new TextureRL(armor));
        }

        if (stack.item().hasEffect(stack.stack())) {
            textures.add(TextureEnchant.FIRST);
            textures.add(TextureEnchant.SECOND);
        }

        return new MultiTexture(textures);
    }

    public ModelBiped model(AbstractClientPlayer player, AAItemStack stack) {
        if (this.modelSize != AAConfig.vanillaArmorModelSize) {
            this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
            this.modelSize = AAConfig.vanillaArmorModelSize;
        }

        ModelBiped mb = stack.item().getArmorModel(player, stack.stack(), EntityEquipmentSlot.CHEST, this.defaultModel);

        if (mb == null) {
            mb = this.defaultModel;
        }

        return mb;
    }

    @Override
    public Class<ArmRenderLayerArmor> targetLayer() {
        return ArmRenderLayerArmor.class;
    }

    @Override
    public Class<ArmModelManagerBiped> clazz() {
        return ArmModelManagerBiped.class;
    }
}
