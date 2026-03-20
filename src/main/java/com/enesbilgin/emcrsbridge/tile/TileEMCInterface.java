package com.enesbilgin.emcrsbridge.tile;

import com.enesbilgin.emcrsbridge.util.ProjectEReflection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileEMCInterface extends TileEntity {
    private static final int MAX_TEMPLATES = 27;

    private UUID ownerUuid;
    private String ownerName = "";
    private final List<ItemStack> templates = new ArrayList<>();
    private final EMCItemHandler itemHandler = new EMCItemHandler(this);

    public boolean hasOwner() {
        return ownerUuid != null;
    }

    public void setOwner(EntityPlayer player) {
        this.ownerUuid = player.getUniqueID();
        this.ownerName = player.getName();
        markDirty();
    }

    public boolean isOwner(EntityPlayer player) {
        return ownerUuid != null && ownerUuid.equals(player.getUniqueID());
    }

    @Nullable
    public EntityPlayer getOwnerPlayer() {
        if (world == null || world.isRemote || ownerUuid == null) {
            return null;
        }
        return world.getMinecraftServer() == null ? null : world.getMinecraftServer().getPlayerList().getPlayerByUUID(ownerUuid);
    }

    public boolean addTemplate(ItemStack stack) {
        if (stack.isEmpty() || templates.size() >= MAX_TEMPLATES) {
            return false;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);

        for (ItemStack existing : templates) {
            if (ItemStack.areItemsEqual(existing, copy) && ItemStack.areItemStackTagsEqual(existing, copy)) {
                return false;
            }
        }

        templates.add(copy);
        markDirty();
        return true;
    }

    public boolean removeLastTemplate() {
        if (templates.isEmpty()) {
            return false;
        }
        templates.remove(templates.size() - 1);
        markDirty();
        return true;
    }

    public List<ItemStack> getTemplates() {
        return templates;
    }

    public long getAvailableEmcForOwner() {
        EntityPlayer owner = getOwnerPlayer();
        return owner == null ? 0L : ProjectEReflection.getPlayerEmc(owner);
    }

    public long getEmcCost(ItemStack stack) {
        return ProjectEReflection.getItemEmc(stack);
    }

    public boolean canCreate(ItemStack request, int amount) {
        EntityPlayer owner = getOwnerPlayer();
        if (owner == null || request.isEmpty() || amount <= 0) {
            return false;
        }

        if (!ProjectEReflection.playerKnows(owner, request)) {
            return false;
        }

        long emc = getEmcCost(request);
        if (emc <= 0) {
            return false;
        }

        long needed = emc * amount;
        return ProjectEReflection.getPlayerEmc(owner) >= needed;
    }

    public ItemStack createItem(ItemStack request, int amount, boolean simulate) {
        EntityPlayer owner = getOwnerPlayer();
        if (owner == null || request.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack single = request.copy();
        single.setCount(1);

        if (!ProjectEReflection.playerKnows(owner, single)) {
            return ItemStack.EMPTY;
        }

        long emc = getEmcCost(single);
        if (emc <= 0) {
            return ItemStack.EMPTY;
        }

        long available = ProjectEReflection.getPlayerEmc(owner);
        int maxByEmc = (int) Math.min(Integer.MAX_VALUE, available / emc);
        int crafted = Math.min(amount, maxByEmc);
        crafted = Math.min(crafted, single.getMaxStackSize());

        if (crafted <= 0) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            long cost = emc * crafted;
            if (!ProjectEReflection.consumePlayerEmc(owner, cost)) {
                return ItemStack.EMPTY;
            }
        }

        ItemStack out = single.copy();
        out.setCount(crafted);
        return out;
    }

    public String describeState() {
        return "Owner=" + ownerName + ", templates=" + templates.size() + ", EMC=" + getAvailableEmcForOwner();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) itemHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (ownerUuid != null) {
            compound.setString("OwnerUUID", ownerUuid.toString());
        }
        compound.setString("OwnerName", ownerName);

        NBTTagList list = new NBTTagList();
        for (ItemStack template : templates) {
            NBTTagCompound tag = new NBTTagCompound();
            template.writeToNBT(tag);
            list.appendTag(tag);
        }
        compound.setTag("Templates", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        ownerUuid = compound.hasKey("OwnerUUID") ? UUID.fromString(compound.getString("OwnerUUID")) : null;
        ownerName = compound.getString("OwnerName");

        templates.clear();
        NBTTagList list = compound.getTagList("Templates", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            templates.add(new ItemStack(list.getCompoundTagAt(i)));
        }
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
}
