package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.client.armorlayer.ArmModelManagerBiped;
import com.artur114.armoredarms.client.armorlayer.ArmModelRenderContainerDef;
import com.artur114.armoredarms.client.armorlayer.ArmModelRendererBiped;
import com.artur114.armoredarms.client.engines.ArmRenderEngineForge;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.armorlayer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.armorlayer.IArmModelManager;
import com.artur114.armoredarms.core.api.armorlayer.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.core.util.ShapelessLocationMap;
//import lain.mods.cos.api.CosArmorAPI;
//import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ArmRenderLayerArmor extends AbstractArmorRenderLayer<ArmRenderLayerArmor, AAItemStack, ArmRenderEngineForge> {
    public List<LayerRenderer<AbstractClientPlayer>> layerRenderers = null;
    public AbstractClientPlayer player = null;
    public LayerBipedArmor armorLayer = null;
    public RenderPlayer renderPlayer = null;


    @Override
    public AAItemStack currentChestPlate() {
        return this.itemStackArmor(this.player);
    }

    @Override
    public AAItemStack emptyStack() {
        return AAItemStack.EMPTY;
    }

    @Override
    public List<ShapelessLocation> initBlackList() {
        return new ArrayList<>();
    }

    @Override
    public void init(ArmRenderEngineForge engine, IAAModContainer mod) {
        super.init(engine, mod);

        this.player = engine.mc.player;
        this.renderPlayer = this.initRenderPlayer(this.player);
        this.layerRenderers = this.initLayerRenderers();
        this.armorLayer = this.findArmorLayer();
    }

    @Override
    public ShapelessLocationMap<IArmModelManager<?, ArmRenderLayerArmor>> initModelManagers() {
        ShapelessLocationMap<IArmModelManager<?, ArmRenderLayerArmor>> map = new ShapelessLocationMap<>();
        map.put(ShapelessLocation.location("*", "*"), new ArmModelManagerBiped());
        return map;
    }

    @Override
    public ShapelessLocationMap<IArmModelRenderContainer<? extends IArmModelManager<?, ArmRenderLayerArmor>>> initRenderContainers() {
        ShapelessLocationMap<IArmModelRenderContainer<? extends IArmModelManager<?, ArmRenderLayerArmor>>> map = new ShapelessLocationMap<>();
        map.put(ShapelessLocation.location("*", "*"), new ArmModelRenderContainerDef(ArmModelRendererBiped.class));
        return map;
    }

    public AAItemStack itemStackArmor(AbstractClientPlayer player) {
//        if (EnumMods.COSMETIC_ARMOR.isLoaded()) {
//            CAStacksBase stacks = CosArmorAPI.getCAStacksClient(player.getUniqueID());
//            int chestId = EntityEquipmentSlot.CHEST.getIndex();
//
//            if (stacks.isSkinArmor(chestId)) {
//                return AAItemStack.EMPTY;
//            }
//
//            ItemStack stack = stacks.getStackInSlot(chestId);
//
//            if (!stack.isEmpty()) {
//                return AAItemStack.from(stack);
//            }
//        }

        return AAItemStack.from(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
    }

    @SuppressWarnings("unchecked")
    public List<LayerRenderer<AbstractClientPlayer>> initLayerRenderers() {
        try {
            Field field = null;
            Field[] fields = RenderLivingBase.class.getDeclaredFields();

            for (Field rField : fields) {
                boolean isAcc = rField.isAccessible();
                rField.setAccessible(true);
                if (rField.get(this.renderPlayer) instanceof List) {
                    field = rField;
                }
                rField.setAccessible(isAcc);
            }

            if (field == null) {
                throw new RenderException("layerRenderers is not find!").setComponent(this);
            }

            boolean isAcc = field.isAccessible();
            field.setAccessible(true);
            List<LayerRenderer<AbstractClientPlayer>> layers = (List<LayerRenderer<AbstractClientPlayer>>) field.get(this.renderPlayer);
            field.setAccessible(isAcc);
            return layers;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LayerBipedArmor findArmorLayer() {
        for (LayerRenderer<?> layerRenderer : this.layerRenderers) {
            if (layerRenderer instanceof LayerBipedArmor) {
                return (LayerBipedArmor) layerRenderer;
            }
        }
        throw new IllegalStateException();
    }

    public RenderPlayer initRenderPlayer(AbstractClientPlayer player) {
        return (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
    }

    @Override
    public Class<ArmRenderEngineForge> targetEngine() {
        return ArmRenderEngineForge.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
