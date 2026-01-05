package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IArmRenderLayer;
import com.artur114.armoredarms.client.integration.Overriders;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * InitRenderLayersEvent fires when {@link com.artur114.armoredarms.client.core.RenderArmManager} is initialized.
 * During this event, you can register your own IArmRenderLayer implementations.
 * {@link #addLayer(Class)} and {@link #addLayerIfModLoad(Class, String)}, or remove a registered one {@link #removeLayer(Class)}.<br>
 * <br>
 * {@link #renderLayers} Map of registered layers.<br>
 * <br>
 * This event can't be canceled. {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
 * <br>
 * This event has no result. {@link Event.HasResult}<br>
 * <br>
 * This event uses {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 * <br>
 */
@SideOnly(Side.CLIENT)
public class InitRenderLayersEvent extends Event {
    private final Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = new HashMap<>();

    /**
     * @param renderLayer The class of the rendering layer to be removed.
     * @return Is having class in map.
     */
    public boolean removeLayer(Class<? extends IArmRenderLayer> renderLayer) {
        return this.renderLayers.remove(renderLayer) != null;
    }


    /**
     * @param renderLayer The rendering layer class to register.
     * @param modId The mod ID for which the rendering layer is intended; if the mod is not loaded, the rendering layer will not be created.
     */
    public void addLayerIfModLoad(Class<? extends IArmRenderLayer> renderLayer, String modId) {
        if (Loader.isModLoaded(modId)) {
            this.addLayer(renderLayer);
        }
    }

    /**
     * @param renderLayer The rendering layer class to register.<br>
     * <br>
     * If the passed IArmRenderLayer implementation class does not contain an empty constructor, the layer will not be registered.<br>
     * <br>
     * @see #addLayerIfModLoad(Class, String)
     */
    public void addLayer(Class<? extends IArmRenderLayer> renderLayer) {
        try {
            System.out.println("Registered render layer: " + renderLayer.getName());
            this.renderLayers.put(renderLayer, renderLayer.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            new RuntimeException("Failed to create an object", e).printStackTrace(System.err);
        }
    }

    /**
     * @return Copy of the list of values from the map.
     */
    public Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers() {
        return new HashMap<>(this.renderLayers);
    }
}