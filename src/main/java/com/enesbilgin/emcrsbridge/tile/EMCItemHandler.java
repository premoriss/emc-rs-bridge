package com.enesbilgin.emcrsbridge.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Refined Storage External Storage bu capability'i okuyarak itemleri ağa ekler.
 * Handler dinamik stok gösterir: template itemler EMC yettiği kadar mevcut görünür.
 */
public class EMCItemHandler implements IItemHandler {
    private final TileEMCInterface tile;

    public EMCItemHandler(TileEMCInterface tile) {
        this.tile = tile;
    }

    @Override
    public int getSlots() {
        return tile.getTemplates().size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        List<ItemStack> templates = tile.getTemplates();
        if (slot < 0 || slot >= templates.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack template = templates.get(slot);
        long emc = tile.getEmcCost(template);
        if (emc <= 0) {
            return ItemStack.EMPTY;
        }

        long available = tile.getAvailableEmcForOwner();
        int count = (int) Math.min(template.getMaxStackSize(), available / emc);
        if (count <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack out = template.copy();
        out.setCount(count);
        return out;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        // Bu blok depolama görevi görmez; sadece EMC'den üretim yapar.
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        List<ItemStack> templates = tile.getTemplates();
        if (slot < 0 || slot >= templates.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return tile.createItem(templates.get(slot), amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        List<ItemStack> templates = tile.getTemplates();
        if (slot < 0 || slot >= templates.size()) {
            return 0;
        }
        return templates.get(slot).getMaxStackSize();
    }
}
