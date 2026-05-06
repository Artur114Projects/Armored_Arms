package com.artur114.armoredarms.client.modelrender.armor;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextBase;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextGlint;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextOverlay;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextTrim;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import com.google.common.collect.Maps;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

public class ArmModelManagerArmor implements IArmModelManager<ArmModelManagerArmor, ArmRenderLayerArmor> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
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
        return ForgeHooksClient.getArmorModel(layer.mc.player, layer.chestPlate.stack(), EquipmentSlot.CHEST, layer.engine.actualHumanoidModel());
    }

    public MultiModelRenderContext compileContext(ArmRenderLayerArmor layer) {
        ArrayList<IModelRenderContext> context = new ArrayList<>();

        context.add(new ModelRenderContextBase(this.fmlGetArmorResource(layer.mc.player, layer.chestPlate.stack(), EquipmentSlot.CHEST, null), layer.chestPlate, Priority.HIGH));

        if (layer.chestPlate.item() instanceof DyeableLeatherItem) {
            context.add(new ModelRenderContextOverlay(this.fmlGetArmorResource(layer.mc.player, layer.chestPlate.stack(), EquipmentSlot.CHEST, "overlay")));
        }
        if (layer.mc.player != null && ArmorTrim.getTrim(layer.mc.player.level().registryAccess(), layer.chestPlate.stack()).isPresent()) {
            context.add(new ModelRenderContextTrim(layer.chestPlate, Priority.LOW));
        }
        if (layer.chestPlate.stack().hasFoil()) {
            context.add(new ModelRenderContextGlint(Priority.LOWEST));
        }

        return new MultiModelRenderContext(context);
    }

    public ResourceLocation fmlGetArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
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
