package com.mrfuzzihead.framedcompactdrawers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.mrfuzzihead.framedcompactdrawers.registry.ModItems;

public class FCDCreativeTab extends CreativeTabs {

    public static final FCDCreativeTab TAB = new FCDCreativeTab();

    protected FCDCreativeTab() {
        super("framed_compacting_drawers");
    }

    @Override
    public Item getTabIconItem() {
        return ModItems.framedCompactDrawerItem;
    }
}
