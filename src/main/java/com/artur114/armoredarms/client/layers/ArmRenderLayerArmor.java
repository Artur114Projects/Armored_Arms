package com.artur114.armoredarms.client.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.*;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import java.lang.reflect.Field;
import java.util.List;

public class ArmRenderLayerArmor extends AbstractArmorRenderLayer<ArmRenderLayerArmor, AAItemStack, AbstractRenderEngineForge<?, ?>> {
    public List<LayerRenderer<AbstractClientPlayer>> layerRenderers = null;
    public final Minecraft mc = Minecraft.getMinecraft();
    public LayerBipedArmor armorLayer = null;
    public RenderPlayer renderPlayer = null;


    @Override
    public AAItemStack currentChestPlate() {
        return AAItemStack.chestPlate(this.mc.player);
    }

    @Override
    public String messageForPlayer(String type) {
        return "armoredarms.error.layer.armor." + type;
    }

    @Override
    public AAItemStack emptyStack() {
        return AAItemStack.EMPTY;
    }

    @Override
    public List<ShapelessLocation> initBlackList() {
        return AAConfig.Baked.renderArmorBlackList;
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        super.init(engine, mod);

        this.renderPlayer = this.initRenderPlayer(this.mc.player);
        this.layerRenderers = this.initLayerRenderers();
        this.armorLayer = this.findArmorLayer();
    }

    @Override
    public List<SLContainer<IArmModelManager<?, ?>>> initModelManagers() {
        InitModelManagersEvent event = new InitModelManagersEvent(this.getClass(), this.mod, true);
        event.registerManager(new ArmModelManagerArmor(), ShapelessLocation.location("*", "*"));
        this.mod.post(event);
        return event.managersSL();
    }

    @Override
    public List<SLContainer<IArmModelRenderContainer<?, ?>>> initRenderContainers() {
        InitRenderContainersEvent event = new InitRenderContainersEvent(this.getClass(), this.mod, true);
        event.registerContainer(new ArmModelContainerArmor(ArmModelRendererArmor.class, Priority.LOWEST), ShapelessLocation.location("*", "*"));
        this.mod.post(event);
        return event.containersSL();
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
    @SuppressWarnings("unchecked")
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return (Class<AbstractRenderEngineForge<?,?>>) (Class<?>) AbstractRenderEngineForge.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
