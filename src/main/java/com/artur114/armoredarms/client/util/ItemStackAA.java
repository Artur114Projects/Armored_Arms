package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.IItemStack;
import com.artur114.armoredarms.core.util.Immutable;
import com.artur114.armoredarms.core.util.Int2ObjBoundedCache;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@Immutable
public class ItemStackAA implements IItemStack {
    private static final ModelBiped defaultModel = new ModelBiped(1.0F);
    private static final Int2ObjBoundedCache<ItemStackAA> cache = new Int2ObjBoundedCache<>(512);
    public static final ItemStackAA EMPTY = new ItemStackAA(ItemStack.EMPTY);

    public static synchronized ItemStackAA from(ItemStack mcStack) {
        ItemStackAA stack = cache.get(mcStack.hashCode());

        if (stack == null) {
            stack = new ItemStackAA(mcStack);
            cache.add(stack);
        }

        return stack;
    }

    public static ItemStackAA chestPlate(AbstractClientPlayer player) {
        if (EnumMods.COSMETIC_ARMOR.isLoaded()) {
            CAStacksBase stacks = CosArmorAPI.getCAStacksClient(player.getUniqueID());
            int chestId = EntityEquipmentSlot.CHEST.getIndex();

            if (stacks.isSkinArmor(chestId)) {
                return ItemStackAA.EMPTY;
            }

            ItemStack stack = stacks.getStackInSlot(chestId);

            if (!stack.isEmpty()) {
                return ItemStackAA.from(stack);
            }
        }

        return ItemStackAA.from(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
    }

    private final ItemStack stack;
    private final boolean isArmor;

    public ItemStackAA(ItemStack stack) {
        this.stack = stack;

        this.isArmor = stack.getItem() instanceof ItemArmor;
    }

    public ItemArmor item() {
        if (!this.isArmor) return null;
        return (ItemArmor) this.stack.getItem();
    }

    public ItemStack stack() {
        return this.stack;
    }

    public boolean isBiped() {
        ModelBiped armor = this.stack.getItem().getArmorModel(Minecraft.getMinecraft().player, this.stack, EntityEquipmentSlot.CHEST, defaultModel);
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
        return AAUtils.fromMc(this.stack.getItem().getRegistryName());
    }

    @Override
    public boolean isEmpty() {
        return !this.isArmor || this.stack.isEmpty();
    }

    @Override
    public int hashCode() {
        if (this.isArmor) {
            return 0;
        }
        return this.stack.getItem().hashCode();
    }
}
