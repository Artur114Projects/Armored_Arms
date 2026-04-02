package com.artur114.armoredarms.client.modelrender.armor;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.TextureEnchant;
import com.artur114.armoredarms.client.util.TextureRL;
import com.artur114.armoredarms.client.util.TextureRLRGB;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.ITexture;
import com.artur114.armoredarms.core.util.MultiTexture;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmModelManagerArmor implements IArmModelManager<ArmModelManagerArmor, ArmRenderLayerArmor> {
    private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
    private double modelSize = AAConfig.vanillaArmorModelSize;
    private boolean deactivated = false;


    public AbstractClientPlayer player = null;
    public LayerBipedArmor armorLayer = null;
    public IMultiTexture texture = null;
    public ItemStackAA stack = null;
    public ModelBiped model = null;

    @Override
    public void update(ArmRenderLayerArmor layer) {}

    @Override
    public void render(ArmRenderLayerArmor layer, IArmModelRenderer<ArmModelManagerArmor> renderer, EnumHandSideAA side) {
        if (this.deactivated) {
            return;
        }
        this.armorLayer = layer.armorLayer;
        this.stack = layer.chestPlate;
        this.player = layer.mc.player;

        renderer.renderArm(this, side);

        this.armorLayer = null;
        this.player = null;
        this.stack = null;
    }

    @Override
    public void load(ArmRenderLayerArmor layer) {}

    @Override
    public void unload(ArmRenderLayerArmor layer) {}

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> cacheRenderer(ArmRenderLayerArmor layer, IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> container) {
        if (this.deactivated) {
            return null;
        }
        this.armorLayer = layer.armorLayer;
        this.stack = layer.chestPlate;
        this.player = layer.mc.player;
        this.texture = this.textures(this.player, this.armorLayer, this.stack);
        this.model = this.model(this.player, this.stack);

        IArmModelRenderer<ArmModelManagerArmor> model = container.create(this);

        this.armorLayer = null;
        this.texture = null;
        this.player = null;
        this.stack = null;
        this.model = null;

        return model;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    public List<ITexture> newTextureList() {
        if (stack.item().hasEffect(stack.stack())) {
            return new ArrayList<>(Arrays.asList(TextureEnchant.FIRST, TextureEnchant.SECOND));
        } else {
            return new ArrayList<>();
        }
    }

    public IMultiTexture textures(AbstractClientPlayer player, LayerBipedArmor armorLayer, ItemStackAA stack) {
        ResourceLocation armor = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, null);
        ResourceLocation overlay = null;
        if (stack.item().hasOverlay(stack.stack())) overlay = armorLayer.getArmorResource(player, stack.stack(), EntityEquipmentSlot.CHEST, "overlay");

        List<ITexture> textures = this.newTextureList();
        if (overlay != null) {
            textures.add(new TextureRL(overlay, Priority.HIGH));
            textures.add(new TextureRLRGB(armor, stack.item().getColor(stack.stack())));
        } else {
            textures.add(new TextureRL(armor));
        }

        return new MultiTexture(textures);
    }

    public ModelBiped model(AbstractClientPlayer player, ItemStackAA stack) {
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
    public Class<ArmModelManagerArmor> clazz() {
        return ArmModelManagerArmor.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
