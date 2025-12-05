package com.artur114.armoredarms.api;

import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

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

    /**
     * InitRenderLayersEvent fires when {@link com.artur114.armoredarms.client.core.RenderArmManager} is initialized.
     * During this event, you can register your own IArmRenderLayer implementations.
     * {@link #addLayer(Class)} and {@link #addLayerIfModLoad(Class, String)}, or remove a registered one {@link #removeLayer(Class)}.<br>
     * <br>
     * {@link #renderLayers} Map of registered layers.<br>
     * <br>
     * This event can't be canceled. {@link net.minecraftforge.fml.common.eventhandler.Cancelable}.<br>
     * <br>
     * This event has no result. {@link HasResult}<br>
     * <br>
     * This event uses {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * @see com.artur114.armoredarms.client.util.Overriders#initRenderLayers(InitRenderLayersEvent)
     */
    @SideOnly(Side.CLIENT)
    public static class InitRenderLayersEvent extends Event {
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

    /**
     * InitRenderLayersEvent fires when {@link com.artur114.armoredarms.client.core.ArmRenderLayerArmor} are initialized.
     * During this event, you can register your own IOverrider implementation {@link #registerOverrider(String, String, IOverrider, boolean)},
     * add armor IDs to the blacklist {@link #addArmorToBlackList(String, String)}, {@link #addArmorToBlackList(String[])},
     * and also remove a registered IOverrider or remove an armor ID from the blacklist {@link #removeOverrider(String, String)}, {@link #removeFromBlackList(String, String)}.<br>
     * <br>
     * {@link #modelOverriders} Map of registered IOverriderGetModel.<br>
     * {@link #textureOverriders} Map of registered IOverriderGetTex.<br>
     * {@link #renderOverriders} Map of registered IOverriderRender.<br>
     * {@link #renderBlackList} Blacklist.<br>
     * <br>
     * This event can't be canceled. {@link net.minecraftforge.fml.common.eventhandler.Cancelable}. <br>
     * <br>
     * This event has no result. {@link HasResult}<br>
     * <br>
     * This event uses {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * @see com.artur114.armoredarms.client.util.Overriders#initArmorRenderLayer(InitArmorRenderLayerEvent)
     */
    public static class InitArmorRenderLayerEvent extends Event {
        private final Map<ShapelessRL, IOverriderGetModel> modelOverriders = new HashMap<>();
        private final Map<ShapelessRL, IOverriderGetTex> textureOverriders = new HashMap<>();
        private final Map<ShapelessRL, IOverriderRender> renderOverriders = new HashMap<>();
        private final Set<ShapelessRL> renderBlackList = new HashSet<>();

        /**
         * @param modid Mod identifier for armor from which overrider will be applied, if the value is equals("*") overrider will be applied to armor from all mods with itemName specified below.<br>
         * @param itemName Identifier of the armor which the overrider will be applied to, if the value is equals("*") the overrider will be applied to all armor with the modid specified earlier.<br>
         * @param overrider A descendant of IOverrider must inherit from {@link IOverriderRender} or {@link IOverriderGetTex} or {@link IOverriderGetModel} otherwise it will not be applied.<br>
         * @param replaceIfHas If true, then if there is an existing overrider with the same ResourceLocation, it will be replaced by the current overrider.<br>
         * <br><br>
         * If mod and itemName values are equals("*"), then overrider applies to all armor types.<br>
         * <br>
         * Direct link has priority! If there are 2 overriders, one with the link "minecraft:*" and the other with "minecraft:any_armor", then the second one will be chosen with priority if the item identifier is equal to "minecraft:any_armor".
         */
        public void registerOverrider(String modid, String itemName, IOverrider overrider, boolean replaceIfHas) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);

            if (rl.isEmpty()) {
                return;
            }

            if (overrider instanceof IOverriderRender && (!this.renderOverriders.containsKey(rl) || replaceIfHas)) {
                this.renderOverriders.put(rl, (IOverriderRender) overrider);
                System.out.println("Added overrider render! for " + "[" + rl + "]");
            }
            if (overrider instanceof IOverriderGetTex && (!this.textureOverriders.containsKey(rl) || replaceIfHas)) {
                this.textureOverriders.put(rl, (IOverriderGetTex) overrider);
                System.out.println("Added overrider get texture! for " + "[" + rl + "]");
            }
            if (overrider instanceof IOverriderGetModel && (!this.modelOverriders.containsKey(rl) || replaceIfHas)) {
                this.modelOverriders.put(rl, (IOverriderGetModel) overrider);
                System.out.println("Added overrider get model! for " + "[" + rl + "]");
            }
        }

        /**
         * @param modid Identifier of the mod, the armor from which will be in the blacklist, if the value is equals("*") then the armor from all mods with the itemName specified below will be in the blacklist.<br>
         * @param itemName Identifier of armor that will be in the blacklist, if the value is equals("*") then all armor with the modid specified earlier will be added to the blacklist.<br>
         * <br>
         * If modid and itemName values are equals("*"), then all armor will be blacklisted. :(
         */
        public void addArmorToBlackList(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            if (!rl.isEmpty()) this.renderBlackList.add(rl);
        }

        /**
         * Adds all specified strings to the blacklist.
         * @param rls An array of strings containing armor IDs in the format "modid:armor"
         * @see #addArmorToBlackList(String, String)
         */
        public void addArmorToBlackList(String[] rls) {
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
                    this.renderBlackList.add(rl);
                }
            }
        }

        /**
         * @param modid Resource domain.
         * @param itemName Resource path.
         * @return Removal success.
         * <br><br>
         * Formlessness doesn't work in this case.
         * If you pass "minecraft:*", then only the Overrider with the identifier "minecraft:*" will be deleted,
         * and the Overrider with the identifier "minecraft:armor" will not be affected.
         */
        public boolean removeOverrider(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            boolean flag = false;

            flag |= this.textureOverriders.remove(rl) != null;
            flag |= this.renderOverriders.remove(rl) != null;
            flag |= this.modelOverriders.remove(rl) != null;

            return flag;
        }

        /**
         * @param modid Resource domain.
         * @param itemName Resource path.
         * @return Removal success.
         */
        public boolean removeFromBlackList(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            return this.renderBlackList.remove(rl);
        }

        /**
         * @return Copy of the black list.
         */
        public List<ShapelessRL> renderBlackList() {
            return new ArrayList<>(this.renderBlackList);
        }

        /**
         * @return Copy of the list of values from the IOverriderGetModel map.
         */
        public Map<ShapelessRL, IOverriderGetModel> modelOverriders() {
            return new HashMap<>(this.modelOverriders);
        }

        /**
         * @return Copy of the list of values from the IOverriderGetTex map.
         */
        public Map<ShapelessRL, IOverriderGetTex> textureOverriders() {
            return new HashMap<>(this.textureOverriders);
        }

        /**
         * @return Copy of the list of values from the IOverriderRender map.
         */
        public Map<ShapelessRL, IOverriderRender> renderOverriders() {
            return new HashMap<>(this.renderOverriders);
        }
    }
}
