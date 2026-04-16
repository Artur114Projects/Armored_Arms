package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.*;
import com.artur114.armoredarms.main.AAConfig;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

@Immutable
public class ItemStackAA implements IItemStack {
    private static final ModelBiped defaultModel = new ModelBiped(1.0F);
    private static final IIntBoundedCache<ItemStackAA> cache = IIntBoundedCache.createBestInstance(512);

    public static final ItemStackAA EMPTY = new ItemStackAA(null);
    public static final int CHEST_PLATE_ID = 1;

    public static synchronized ItemStackAA from(ItemStack mcStack) {
        if (mcStack == null) {
            return EMPTY;
        }

        ItemStackAA stack = cache.get(mcStack.hashCode());

        if (stack == null) {
            stack = new ItemStackAA(mcStack);
            cache.add(stack);
        }

        return stack;
    }

    public static ItemStackAA chestPlate(AbstractClientPlayer player) {
        int chestId = CHEST_PLATE_ID + 1;

        if (EnumMods.COSMETIC_ARMOR.isLoaded()) {
            InventoryCosArmor stacks = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID());

            if (stacks.isSkinArmor(chestId)) {
                return null;
            }

            ItemStack stack = stacks.getStackInSlot(chestId);

            if (stack != null) {
                return ItemStackAA.from(stack);
            }
        }

        return ItemStackAA.from(player.getCurrentArmor(chestId));
    }

    private final ItemStack stack;
    private final boolean isArmor;

    public ItemStackAA(ItemStack stack) {
        this.stack = stack;

        if (stack != null) {
            this.isArmor = stack.getItem() instanceof ItemArmor;
        } else {
            this.isArmor = false;
        }
    }

    public ItemArmor item() {
        if (!this.isArmor) return null;
        return (ItemArmor) this.stack.getItem();
    }

    public ItemStack stack() {
        return this.stack;
    }

    public boolean isBiped() {
        if (this.isEmpty()) {
            return false;
        }
        ModelBiped armor = ForgeHooksClient.getArmorModel(Minecraft.getMinecraft().thePlayer, this.stack, CHEST_PLATE_ID, defaultModel);
        return armor == null || armor.getClass() == ModelBiped.class;
    }

    @Override
    public boolean isNew(IItemStack stack) {
        if (AAConfig.useCheckByItem) {
            return this.stack.getItem() != ((ItemStackAA) stack).stack.getItem();
        } else {
            return this.stack != ((ItemStackAA) stack).stack;
        }
    }

    @Override
    public ShapelessLocation location() {
        if (this.isEmpty()) {
            return ShapelessLocation.location("minecraft:air");
        }
        return AAUtils.fromMc(Item.itemRegistry.getNameForObject(this.stack.getItem()));
    }

    @Override
    public boolean isEmpty() {
        return !this.isArmor || this.stack == null;
    }

    @Override
    public int hashCode() {
        if (this.isEmpty()) return 0;
        return this.stack.getItem().hashCode();
    }
}