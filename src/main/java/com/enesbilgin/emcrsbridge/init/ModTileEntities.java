package com.enesbilgin.emcrsbridge.init;

import com.enesbilgin.emcrsbridge.EMCRSBridgeMod;
import com.enesbilgin.emcrsbridge.tile.TileEMCInterface;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {
    private ModTileEntities() {
    }

    public static void init() {
        GameRegistry.registerTileEntity(TileEMCInterface.class, new ResourceLocation(EMCRSBridgeMod.MODID, "emc_interface"));
    }
}
