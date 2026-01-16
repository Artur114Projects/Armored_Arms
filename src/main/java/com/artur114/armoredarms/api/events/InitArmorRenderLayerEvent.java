package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.api.IOverrider;
import com.artur114.armoredarms.api.IOverriderGetModel;
import com.artur114.armoredarms.api.IOverriderGetTex;
import com.artur114.armoredarms.api.IOverriderRender;
import com.artur114.armoredarms.client.integration.Overriders;
import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraftforge.eventbus.api.Event;

import java.util.*;

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
 * This event can't be canceled. {@link net.minecraftforge.eventbus.api.Cancelable}. <br>
 * <br>
 * This event has no result. {@link Event.HasResult}<br>
 * <br>
 * This event uses {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 * <br>
 * @see Overriders#initArmorRenderLayer(InitArmorRenderLayerEvent)
 */
public class InitArmorRenderLayerEvent extends Event {
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
