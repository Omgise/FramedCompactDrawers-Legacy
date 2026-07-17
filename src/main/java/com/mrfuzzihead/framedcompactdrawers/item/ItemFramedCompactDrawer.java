package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;

public class ItemFramedCompactDrawer extends ItemCustomDrawers {

    public ItemFramedCompactDrawer(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int meta) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, meta)) {
            return false;
        } else {
            TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
            if (tile != null) {
                if (stack.hasTagCompound() && stack.getTagCompound()
                    .hasKey("tile")) {
                    NBTTagCompound tiledata = stack.getTagCompound()
                        .getCompoundTag("tile");
                    tile.readFromPortableNBT(tiledata);
                }
                tile.setIsSealed(false);
            }
            return true;
        }
    }
}
