package com.mrfuzzihead.framedcompactdrawers.registry;

import net.minecraft.item.Item;

public class ModItems {

    public static Item framedCompactDrawerItem = null;
    public static Item framedDrawerControllerItem = null;
    public static Item framedSlaveItem = null;

    public static void register() {
        framedCompactDrawerItem = Item.getItemFromBlock(ModBlocks.framedCompactDrawer);
        framedDrawerControllerItem = Item.getItemFromBlock(ModBlocks.framedDrawerController);
        framedSlaveItem = Item.getItemFromBlock(ModBlocks.framedSlave);
    }
}
