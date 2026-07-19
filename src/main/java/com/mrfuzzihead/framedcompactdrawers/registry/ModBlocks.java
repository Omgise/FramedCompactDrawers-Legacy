package com.mrfuzzihead.framedcompactdrawers.registry;

import net.minecraft.block.Block;

import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block blockFramedCompactDrawer = null;
    public static Block blockFramedController = null;
    public static Block blockFramedSlave = null;

    public static void register() {
        // Phase 2: Framed Compact Drawer
        blockFramedCompactDrawer = new BlockFramedCompactDrawer();
        GameRegistry.registerBlock(blockFramedCompactDrawer, FramedCompactDrawers.MODID + ".framed_compact_drawer");
        GameRegistry
            .registerTileEntity(TileFramedCompactDrawer.class, FramedCompactDrawers.MODID + ".framed_compact_drawer");

        // Phase 3: Framed Controller
        blockFramedController = new BlockFramedController();
        GameRegistry.registerBlock(blockFramedController, FramedCompactDrawers.MODID + ".framed_drawer_controller");
        GameRegistry.registerTileEntity(TileFramedController.class, FramedCompactDrawers.MODID + ".framed_controller");
    }
}
