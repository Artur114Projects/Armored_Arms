package com.artur114.armoredarms.api;

import com.artur114.armoredarms.api.override.IOverrider;
import com.artur114.armoredarms.api.override.IOverriderGetModel;
import com.artur114.armoredarms.api.override.IOverriderGetTex;
import com.artur114.armoredarms.api.override.IOverriderRender;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.ArmoredArms;

public class ArmoredArmsApi {
    /**
     * If modid and itemName values are equals("*"), then all armor will be blacklisted.
     * @param modid Identifier of the mod, the armor from which will be in the blacklist, if the value is equals("*") then the armor from all mods with the itemName specified below will be in the blacklist.
     * @param itemName Identifier of armor that will be in the blacklist, if the value is equals("*") then all armor with the modid specified earlier will be added to the blacklist.
     */
    public static void addArmorToBlackList(String modid, String itemName) {
        ArmoredArms.RENDER_ARM_MANAGER.addToBlackList(new ShapelessRL(modid, itemName));
    }

    /**
     * If mod and itemName values are equals("*"), then overrider applies to all armor types.
     * Direct link has priority! If there are 2 overriders, one with the link "minecraft:*" and the other with "minecraft:any_armor", then the second one will be chosen with priority if the item identifier is equal to "minecraft:any_armor".
     * @param modid Mod identifier for armor from which overrider will be applied, if the value is equals("*") overrider will be applied to armor from all mods with itemName specified below.
     * @param itemName Identifier of the armor which the overrider will be applied to, if the value is equals("*") the overrider will be applied to all armor with the modid specified earlier.
     * @param overrider A descendant of IOverrider must inherit from {@link IOverriderRender} or {@link IOverriderGetTex} or {@link IOverriderGetModel} otherwise it will not be applied.
     * @param replaceIfHas If true, then if there is an existing overrider with the same ResourceLocation, it will be replaced by the current overrider.
     */
    public static void addOverrider(String modid, String itemName, IOverrider overrider, boolean replaceIfHas) {
        ArmoredArms.RENDER_ARM_MANAGER.addOverrider(new ShapelessRL(modid, itemName), overrider, replaceIfHas);
    }
}
