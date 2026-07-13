package com.mrfuzzihead.framedcompactdrawers.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static Block framedCompactDrawer = null;
    public static Block framedDrawerController = null;
    public static Block framedSlave = null;

    public static void register() {
        framedCompactDrawer = new com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer();
        GameRegistry.registerBlock(framedCompactDrawer, Item.class, "framed_compact_drawer");

        framedDrawerController = new com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController();
        GameRegistry.registerBlock(framedDrawerController, Item.class, "framed_drawer_controller");

        framedSlave = new com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave();
        GameRegistry.registerBlock(framedSlave, Item.class, "framed_slave");

        registerTileEntities();
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(
            com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer.class,
            "framed_compact_drawer");
        GameRegistry.registerTileEntity(
            com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController.class,
            "framed_drawer_controller");
        GameRegistry
            .registerTileEntity(com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave.class, "framed_slave");
    }
}
