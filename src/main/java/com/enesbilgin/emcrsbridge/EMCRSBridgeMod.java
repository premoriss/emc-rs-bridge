package com.enesbilgin.emcrsbridge;

import com.enesbilgin.emcrsbridge.init.ModBlocks;
import com.enesbilgin.emcrsbridge.init.ModTileEntities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
    modid = EMCRSBridgeMod.MODID,
    name = EMCRSBridgeMod.NAME,
    version = EMCRSBridgeMod.VERSION,
    dependencies = "required-after:forge@[14.23.5.2859,);required-after:projecte;required-after:refinedstorage"
)
public class EMCRSBridgeMod {
    public static final String MODID = "emcrsbridge";
    public static final String NAME = "EMC RS Bridge";
    public static final String VERSION = "1.0.0";

    public static final CreativeTabs TAB = new CreativeTabs(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.EMC_INTERFACE);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.init();
        ModTileEntities.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
