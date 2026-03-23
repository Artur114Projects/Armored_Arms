package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.aalegacy.main.AAConfig;
import com.artur114.armoredarms.core.util.IItemStack;
import com.artur114.armoredarms.core.util.Int2ObjBoundedCache;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemStackAA implements IItemStack {
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
        return from(player.getItemBySlot(EquipmentSlot.CHEST));
    }

    private final ItemStack stack;
    private final boolean isArmor;

    public ItemStackAA(ItemStack stack) {
        this.stack = stack;

        this.isArmor = stack.getItem() instanceof ArmorItem;
    }

    public ArmorItem item() {
        if (!this.isArmor) return null;
        return (ArmorItem) this.stack.getItem();
    }

    public ItemStack stack() {
        return this.stack;
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
        return AAUtils.fromMc(ForgeRegistries.ITEMS.getKey(this.stack.getItem()));
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