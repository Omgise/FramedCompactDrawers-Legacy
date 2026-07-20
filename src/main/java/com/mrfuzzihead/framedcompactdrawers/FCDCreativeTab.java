package com.mrfuzzihead.framedcompactdrawers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

@SideOnly(Side.CLIENT)
public class FCDCreativeTab extends CreativeTabs {

    public static final FCDCreativeTab tab = new FCDCreativeTab("framed_compacting_drawers");

    private static Item tabIconItem;

    public FCDCreativeTab(String name) {
        super(name);
    }

    @Override
    public Item getTabIconItem() {
        return tabIconItem;
    }
}
