package com.mrfuzzihead.framedcompactdrawers.registry;

import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.item.ItemFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.item.ItemFramedController;
import com.mrfuzzihead.framedcompactdrawers.item.ItemFramedSlave;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks {

    public static Block blockFramedCompactDrawer = null;
    public static Block blockFramedController = null;
    public static Block blockFramedSlave = null;

    public static void register() {
        // Phase 2: Framed Compact Drawer
        blockFramedCompactDrawer = new BlockFramedCompactDrawer();
        GameRegistry.registerBlock(
            blockFramedCompactDrawer,
            ItemFramedCompactDrawer.class,
            FramedCompactDrawers.MODID + ".framed_compact_drawer");
        GameRegistry
            .registerTileEntity(TileFramedCompactDrawer.class, FramedCompactDrawers.MODID + ".framed_compact_drawer");

        // Phase 3: Framed Controller
        blockFramedController = new BlockFramedController();
        GameRegistry.registerBlock(
            blockFramedController,
            ItemFramedController.class,
            FramedCompactDrawers.MODID + ".framed_drawer_controller");
        GameRegistry.registerTileEntity(TileFramedController.class, FramedCompactDrawers.MODID + ".framed_controller");

        // Phase 4: Framed Slave
        blockFramedSlave = new BlockFramedSlave();
        GameRegistry
            .registerBlock(blockFramedSlave, ItemFramedSlave.class, FramedCompactDrawers.MODID + ".framed_slave");
        GameRegistry.registerTileEntity(TileFramedSlave.class, FramedCompactDrawers.MODID + ".framed_slave");
    }
}
