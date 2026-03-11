package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.IItemStack;
import com.artur114.armoredarms.core.util.Immutable;
import com.artur114.armoredarms.core.util.Int2ObjBoundedCache;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;


@Immutable
public class AAItemStack implements IItemStack {
    private static final Int2ObjBoundedCache<AAItemStack> cache = new Int2ObjBoundedCache<>(512);
    public static final AAItemStack EMPTY = new AAItemStack(ItemStack.EMPTY);

    public static synchronized AAItemStack from(ItemStack mcStack) {
        AAItemStack stack = cache.get(mcStack.hashCode());

        if (stack == null) {
            stack = new AAItemStack(mcStack);
            cache.add(stack);
        }

        return stack;
    }

    private final ItemStack stack;
    private final boolean isEmpty;

    public AAItemStack(ItemStack stack) {
        this.stack = stack;

        this.isEmpty = stack.isEmpty() || !(stack.getItem() instanceof ItemArmor);
    }

    public ItemArmor item() {
        if (this.isEmpty) return null;
        return (ItemArmor) this.stack.getItem();
    }

    public ItemStack stack() {
        return this.stack;
    }

    @Override
    public boolean isNew(IItemStack stack) {
        return this.stack != ((AAItemStack) stack).stack;
    }

    @Override
    public ShapelessLocation location() {
        return AAUtils.fromMc(this.stack.getItem().getRegistryName());
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty ;
    }

    @Override
    public int hashCode() {
        if (this.isEmpty) {
            return 0;
        }
        return this.stack.getItem().hashCode();
    }
}
