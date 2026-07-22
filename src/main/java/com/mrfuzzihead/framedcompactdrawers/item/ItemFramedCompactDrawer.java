package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;

public class ItemFramedCompactDrawer extends ItemCustomDrawers {

    public ItemFramedCompactDrawer(Block block) {
        super(block);
    }

    /**
     * Returns the base stack capacity used when placing the drawer and rendering its tooltip.
     *
     * <p>
     * Storage Drawers 1.7.10 resolves the compacting-drawer config section in
     * ItemDrawers.getCapacityForBlock() with the key "compDrawers", but the section is registered under
     * the lowercase name "compdrawers", so the lookup always returns 0. A tile placed with capacity 0 can
     * never accept items: right-clicking still populates the tier prototypes (so the items render on the
     * drawer's face), but every insert adds 0 items. Storage Drawers works around this for its own
     * compacting drawer in ItemCompDrawers.placeBlockAt(); override the lookup here so both the placed
     * tile's capacity and the item tooltip use the correctly-cased key.
     */
    @Override
    protected int getCapacityForBlock(Block block) {
        if (block instanceof BlockFramedCompactDrawer) return StorageDrawers.config.getBlockBaseStorage("compdrawers");

        return super.getCapacityForBlock(block);
    }
}
