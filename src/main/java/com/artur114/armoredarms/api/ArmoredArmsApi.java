package com.artur114.armoredarms.api;

import com.artur114.armoredarms.client.util.Api;
import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@Api.Unstable
public class ArmoredArmsApi {
    @SideOnly(Side.CLIENT)
    public static class InitRenderLayersEvent extends Event {
        private final Map<Class<? extends IArmRenderLayer>, IArmRenderLayer> renderLayers = new HashMap<>();

        public void removeLayer(Class<? extends IArmRenderLayer> renderLayer) {
            this.renderLayers.remove(renderLayer);
        }

        public void addLayerIfModLoad(Class<? extends IArmRenderLayer> renderLayer, String modId) {
            if (Loader.isModLoaded(modId)) {
                this.addLayer(renderLayer);
            }
        }

        public void addLayer(Class<? extends IArmRenderLayer> renderLayer) {
            try {
                this.renderLayers.put(renderLayer, renderLayer.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                new RuntimeException("Failed to create an object", e).printStackTrace(System.err);
            }
        }

        public Set<IArmRenderLayer> renderLayers() {
            return new HashSet<>(this.renderLayers.values());
        }
    }

    public static class InitArmorRenderLayerEvent extends Event {
        private final Map<ShapelessRL, IOverriderGetModel> modelOverriders = new HashMap<>();
        private final Map<ShapelessRL, IOverriderGetTex> textureOverriders = new HashMap<>();
        private final Map<ShapelessRL, IOverriderRender> renderOverriders = new HashMap<>();
        private final List<ShapelessRL> renderBlackList = new ArrayList<>();

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
         * If modid and itemName values are equals("*"), then all armor will be blacklisted.
         * @param modid Identifier of the mod, the armor from which will be in the blacklist, if the value is equals("*") then the armor from all mods with the itemName specified below will be in the blacklist.
         * @param itemName Identifier of armor that will be in the blacklist, if the value is equals("*") then all armor with the modid specified earlier will be added to the blacklist.
         */
        public void addArmorToBlackList(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            if (!rl.isEmpty()) this.renderBlackList.add(rl);
        }

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

        public boolean removeOverrider(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            boolean flag = false;

            flag |= this.textureOverriders.remove(rl) != null;
            flag |= this.renderOverriders.remove(rl) != null;
            flag |= this.modelOverriders.remove(rl) != null;

            return flag;
        }

        public boolean removeFromBlackList(String modid, String itemName) {
            ShapelessRL rl = new ShapelessRL(modid, itemName);
            return this.renderBlackList.remove(rl);
        }

        public List<ShapelessRL> renderBlackList() {
            return new ArrayList<>(this.renderBlackList);
        }

        public Map<ShapelessRL, IOverriderGetModel> modelOverriders() {
            return new HashMap<>(this.modelOverriders);
        }

        public Map<ShapelessRL, IOverriderGetTex> textureOverriders() {
            return new HashMap<>(this.textureOverriders);
        }

        public Map<ShapelessRL, IOverriderRender> renderOverriders() {
            return new HashMap<>(this.renderOverriders);
        }
    }
}
