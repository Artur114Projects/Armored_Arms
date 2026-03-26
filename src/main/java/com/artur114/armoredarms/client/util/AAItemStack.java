package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.util.IItemStack;
import com.artur114.armoredarms.core.util.Immutable;
import com.artur114.armoredarms.core.util.Int2ObjBoundedCache;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@Immutable
public class AAItemStack implements IItemStack {
    private static final ModelBiped defaultModel = new ModelBiped(1.0F);
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

    public static AAItemStack chestPlate(AbstractClientPlayer player) {
        if (EnumMods.COSMETIC_ARMOR.isLoaded()) {
            CAStacksBase stacks = CosArmorAPI.getCAStacksClient(player.getUniqueID());
            int chestId = EntityEquipmentSlot.CHEST.getIndex();

            if (stacks.isSkinArmor(chestId)) {
                return AAItemStack.EMPTY;
            }

            ItemStack stack = stacks.getStackInSlot(chestId);

            if (!stack.isEmpty()) {
                return AAItemStack.from(stack);
            }
        }

        return AAItemStack.from(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
    }

    private final ItemStack stack;
    private final boolean isArmor;

    public AAItemStack(ItemStack stack) {
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
        return this.stack != ((AAItemStack) stack).stack; //TODO: Сделать проверку по айтему
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
