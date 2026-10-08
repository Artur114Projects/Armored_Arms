package com.artur114.armoredarms.client.modelrender.armor;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextBase;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextGlint;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextTrim;
import com.artur114.armoredarms.client.util.*;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;

public class ArmModelManagerArmor implements IArmModelManager<ArmModelManagerArmor, ArmRenderLayerArmor> {
    public MultiModelRenderContext context = null;
    public ArmRenderContext rawContext = null;
    public ArmRenderLayerArmor layer = null;
    public ItemStackAA chestPlate = null;
    public Model model = null;
    public boolean deactivated = false;

    @Override
    public void update(ArmRenderLayerArmor layer) {}

    @Override
    public void render(ArmRenderLayerArmor layer, IArmModelRenderer<ArmModelManagerArmor> renderer, EnumHandSideAA side) {
        if (this.deactivated) {
            return;
        }

        this.context.prepare(layer.context.multiBufferSource, layer.context.poseStack, layer.context.packedLight);
        this.chestPlate = layer.chestPlate;
        this.rawContext = layer.context;

        renderer.renderArm(this, side);

        this.chestPlate = null;
        this.rawContext = null;
    }

    @Override
    public void load(ArmRenderLayerArmor layer) {
        this.context = this.compileContext(layer);
        this.model = this.initModel(layer);
        this.layer = layer;
    }

    @Override
    public void unload(ArmRenderLayerArmor layer) {}

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> cacheRenderer(ArmRenderLayerArmor layer, IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> container) {
        this.chestPlate = layer.chestPlate;
        this.rawContext = layer.context;

        IArmModelRenderer<ArmModelManagerArmor> model = container.create(this);

        this.rawContext = null;
        this.chestPlate = null;

        return model;
    }

    public Model initModel(ArmRenderLayerArmor layer) {
        return ClientHooks.getArmorModel(layer.mc.player, layer.chestPlate.stack(), EquipmentSlot.CHEST, layer.engine.actualHumanoidModel());
    }

    public MultiModelRenderContext compileContext(ArmRenderLayerArmor layer) {
        ArrayList<IModelRenderContext> context = new ArrayList<>();

        if (layer.mc.player != null) {
            ArmorMaterial armormaterial = layer.chestPlate.item().getMaterial().value();
            IClientItemExtensions extensions = IClientItemExtensions.of(layer.chestPlate.stack());
            int fallbackColor = extensions.getDefaultDyeColor(layer.chestPlate.stack());

            for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                ArmorMaterial.Layer aLayer = armormaterial.layers().get(layerIdx);
                int j = extensions.getArmorLayerTintColor(layer.chestPlate.stack(), layer.mc.player, aLayer, layerIdx, fallbackColor);
                if (j != 0) {
                    ResourceLocation texture = ClientHooks.getArmorTexture(layer.mc.player, layer.chestPlate.stack(), aLayer, false, EquipmentSlot.CHEST);
                    context.add(new ModelRenderContextBase(texture, j, new GenericPriority((armormaterial.layers().size() - layerIdx) + 2)));
                }
            }
        }
        if (layer.mc.player != null && layer.chestPlate.stack().has(DataComponents.TRIM)) {
            context.add(new ModelRenderContextTrim(layer.chestPlate, Priority.LOW));
        }
        if (layer.chestPlate.stack().hasFoil()) {
            context.add(new ModelRenderContextGlint(Priority.LOWEST));
        }

        return new MultiModelRenderContext(context);
    }

    @Override
    public ArmRenderLayerArmor layer() {
        return this.layer;
    }

    @Override
    public Bone bone(EnumHandSideAA side) {
        return this.layer.engine.mainBones().bySide(side);
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
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
