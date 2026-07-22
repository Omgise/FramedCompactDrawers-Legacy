package com.mrfuzzihead.framedcompactdrawers.client.render;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaFramedDrawerProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();

        if (tile instanceof TileEntityDrawers) {
            TileEntityDrawers drawers = (TileEntityDrawers) tile;

            if (config.getConfig("display.content")) {
                for (int i = 0; i < drawers.getDrawerCount(); i++) {
                    if (!drawers.isDrawerEnabled(i)) {
                        continue;
                    }

                    IDrawer drawer = drawers.getDrawer(i);
                    ItemStack storedItem = drawer.getStoredItemPrototype();

                    if (storedItem != null && storedItem.getItem() != null) {
                        String itemName = storedItem.getDisplayName();
                        int count = drawer.getStoredItemCount();
                        currenttip.add(itemName + ": " + count);
                    } else {
                        currenttip.add("Drawer " + (i + 1) + ": Empty");
                    }
                }
            }

            if (config.getConfig("display.stacklimit")) {
                int capacity = drawers.getDrawerCapacity();
                int multiplier = drawers.getEffectiveStorageMultiplier();
                int totalLimit = capacity * multiplier;
                currenttip.add("Storage: " + totalLimit + " per drawer");
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }
}
