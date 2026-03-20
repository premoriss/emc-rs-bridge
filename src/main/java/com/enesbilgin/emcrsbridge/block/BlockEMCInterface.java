package com.enesbilgin.emcrsbridge.block;

import com.enesbilgin.emcrsbridge.EMCRSBridgeMod;
import com.enesbilgin.emcrsbridge.tile.TileEMCInterface;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockEMCInterface extends Block {
    public BlockEMCInterface() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(8.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(EMCRSBridgeMod.TAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEMCInterface();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEMCInterface)) {
            return false;
        }

        TileEMCInterface tile = (TileEMCInterface) te;
        ItemStack held = player.getHeldItem(hand);

        if (!tile.hasOwner()) {
            tile.setOwner(player);
            player.sendMessage(new TextComponentString("EMC Interface owner set: " + player.getName()));
            return true;
        }

        if (!tile.isOwner(player)) {
            player.sendMessage(new TextComponentString("Only the owner can configure this block."));
            return true;
        }

        if (player.isSneaking()) {
            if (!held.isEmpty()) {
                boolean added = tile.addTemplate(held);
                player.sendMessage(new TextComponentString(added ? "Template added." : "Template already exists or list is full."));
            } else {
                boolean removed = tile.removeLastTemplate();
                player.sendMessage(new TextComponentString(removed ? "Last template removed." : "No template to remove."));
            }
            return true;
        }

        player.sendMessage(new TextComponentString(tile.describeState()));
        return true;
    }
}
