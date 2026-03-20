package com.enesbilgin.emcrsbridge.init;

import com.enesbilgin.emcrsbridge.EMCRSBridgeMod;
import com.enesbilgin.emcrsbridge.block.BlockEMCInterface;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ModBlocks {
    public static Block EMC_INTERFACE;

    private ModBlocks() {
    }

    public static void init() {
        EMC_INTERFACE = new BlockEMCInterface();
        registerBlock(EMC_INTERFACE, "emc_interface");
    }

    private static void registerBlock(Block block, String name) {
        block.setRegistryName(EMCRSBridgeMod.MODID, name);
        block.setUnlocalizedName(EMCRSBridgeMod.MODID + "." + name);
        ForgeRegistries.BLOCKS.register(block);

        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);
    }
}
