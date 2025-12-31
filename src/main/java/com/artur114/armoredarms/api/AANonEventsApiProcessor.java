package com.artur114.armoredarms.api;

import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Better use events :)
 * <br>
 * ps: I hope there isn't some stupid mistake here that will cause nothing to work.
 */
@Mod.EventBusSubscriber(modid = ArmoredArms.MODID)
public class AANonEventsApiProcessor {
    private static final Map<ShapelessRL, Tuple<IOverrider, Boolean>> overriders = new HashMap<>();
    private static final Set<Class<? extends IArmRenderLayer>> renderLayers = new HashSet<>();
    private static final Set<ShapelessRL> renderBlackList = new HashSet<>();

    protected static boolean aaNonEventRemoveLayer(Class<? extends IArmRenderLayer> renderLayer) {
        return renderLayers.remove(renderLayer);
    }

    protected static void aaNonEventAddLayerIfModLoad(Class<? extends IArmRenderLayer> renderLayer, String modId) {
        if (Loader.isModLoaded(modId)) {
            renderLayers.add(renderLayer);
        }
    }

    protected static void aaNonEventAddLayer(Class<? extends IArmRenderLayer> renderLayer) {
        renderLayers.add(renderLayer);
    }

    protected static void aaNonEventRegisterOverrider(String modid, String itemName, IOverrider overrider, boolean replaceIfHas) {
        ShapelessRL rl = new ShapelessRL(modid, itemName);

        if (rl.isEmpty()) {
            return;
        }

        overriders.put(rl, new Tuple<>(overrider, replaceIfHas));
    }

    protected static void aaNonEventAddArmorToBlackList(String modid, String itemName) {
        ShapelessRL rl = new ShapelessRL(modid, itemName);
        if (!rl.isEmpty()) renderBlackList.add(rl);
    }

    protected static void aaNonEventAddArmorToBlackList(String[] rls) {
        if (rls == null) {
            return;
        }
        for (String str : rls) {
            ShapelessRL rl = null;
            try {
                rl = new ShapelessRL(str);
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
            if (rl != null && !rl.isEmpty()) {
                renderBlackList.add(rl);
            }
        }
    }

    protected static boolean aaNonEventRemoveOverrider(String modid, String itemName) {
        ShapelessRL rl = new ShapelessRL(modid, itemName);
        return overriders.remove(rl) != null;
    }

    protected static boolean aaNonEventRemoveFromBlackList(String modid, String itemName) {
        ShapelessRL rl = new ShapelessRL(modid, itemName);
        return renderBlackList.remove(rl);
    }

    @SubscribeEvent
    public static void initRenderLayersEvent(InitRenderLayersEvent e) {
        for (Class<? extends IArmRenderLayer> layer : renderLayers) {
            e.addLayer(layer);
        }
    }

    @SubscribeEvent
    public static void initArmorRenderLayerEvent(InitArmorRenderLayerEvent e) {
        for (ShapelessRL black : renderBlackList) {
            e.addArmorToBlackList(black.getResourceDomain(), black.getResourcePath());
        }
        overriders.forEach((rl, overrider) -> {
            e.registerOverrider(rl.getResourceDomain(), rl.getResourcePath(), overrider.getFirst(), overrider.getSecond());
        });
    }
}
