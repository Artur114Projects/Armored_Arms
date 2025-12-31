package com.artur114.armoredarms.api;

import com.artur114.armoredarms.api.events.*;

public class ArmoredArmsApi {

    /**
     * Not event realisation of {@link InitRenderLayersEvent#removeLayer}
     * @see InitRenderLayersEvent#removeLayer
     */
    public static boolean aaNonEventRemoveLayer(Class<? extends IArmRenderLayer> renderLayer) {
        return AANonEventsApiProcessor.aaNonEventRemoveLayer(renderLayer);
    }

    /**
     * Not event realisation of {@link InitArmorRenderLayerEvent#removeOverrider}
     * @see InitArmorRenderLayerEvent#removeOverrider
     */
    public static boolean aaNonEventRemoveOverrider(String modid, String itemName) {
        return AANonEventsApiProcessor.aaNonEventRemoveOverrider(modid, itemName);
    }

    /**
     * Not event realisation of {@link InitArmorRenderLayerEvent#removeFromBlackList}
     * @see InitArmorRenderLayerEvent#removeFromBlackList
     */
    public static boolean aaNonEventRemoveFromBlackList(String modid, String itemName) {
        return AANonEventsApiProcessor.aaNonEventRemoveFromBlackList(modid, itemName);
    }

    /**
     * Not event realisation of {@link InitRenderLayersEvent#addLayerIfModLoad}
     * @see InitRenderLayersEvent#addLayerIfModLoad
     */
    public static void aaNonEventAddLayerIfModLoad(Class<? extends IArmRenderLayer> renderLayer, String modId) {
        AANonEventsApiProcessor.aaNonEventAddLayerIfModLoad(renderLayer, modId);
    }

    /**
     * Not event realisation of {@link InitRenderLayersEvent#addLayer}
     * @see InitRenderLayersEvent#addLayer
     */
    public static void aaNonEventAddLayer(Class<? extends IArmRenderLayer> renderLayer) {
        AANonEventsApiProcessor.aaNonEventAddLayer(renderLayer);
    }

    /**
     * Not event realisation of {@link InitArmorRenderLayerEvent#registerOverrider}
     * @see InitArmorRenderLayerEvent#registerOverrider
     */
    public static void aaNonEventRegisterOverrider(String modid, String itemName, IOverrider overrider, boolean replaceIfHas) {
        AANonEventsApiProcessor.aaNonEventRegisterOverrider(modid, itemName, overrider, replaceIfHas);
    }

    /**
     * Not event realisation of {@link InitArmorRenderLayerEvent#addArmorToBlackList(String, String)}
     * @see InitArmorRenderLayerEvent#removeOverrider(String, String)
     */
    public static void aaNonEventAddArmorToBlackList(String modid, String itemName) {
        AANonEventsApiProcessor.aaNonEventAddArmorToBlackList(modid, itemName);
    }

    /**
     * Not event realisation of {@link InitArmorRenderLayerEvent#addArmorToBlackList(String[])}
     * @see InitArmorRenderLayerEvent#addArmorToBlackList(String[])
     */
    public static void aaNonEventAddArmorToBlackList(String[] rls) {
        AANonEventsApiProcessor.aaNonEventAddArmorToBlackList(rls);
    }
}
