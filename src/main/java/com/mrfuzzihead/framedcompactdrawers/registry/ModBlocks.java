package com.mrfuzzihead.framedcompactdrawers.registry;

import net.minecraft.block.Block;

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

public class ModBlocks {

    public static Block framedCompactDrawer = null;
    public static Block framedDrawerController = null;
    public static Block framedSlave = null;

    public static void register() {
        framedCompactDrawer = new BlockFramedCompactDrawer();
        GameRegistry.registerBlock(framedCompactDrawer, ItemFramedCompactDrawer.class, "framed_compact_drawer");

        framedDrawerController = new BlockFramedController();
        GameRegistry.registerBlock(framedDrawerController, ItemFramedController.class, "framed_drawer_controller");

        framedSlave = new BlockFramedSlave();
        GameRegistry.registerBlock(framedSlave, ItemFramedSlave.class, "framed_slave");

        registerTileEntities();
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileFramedCompactDrawer.class, "framed_compact_drawer");
        GameRegistry.registerTileEntity(TileFramedController.class, "framed_drawer_controller");
        GameRegistry.registerTileEntity(TileFramedSlave.class, "framed_slave");
    }
}
