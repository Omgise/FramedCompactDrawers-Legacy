package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;

public class ItemFramedCompactDrawer extends ItemCustomDrawers {

    public ItemFramedCompactDrawer(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
        float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            return false;
        } else {
            TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(pos.xCoord, pos.yCoord, pos.zCoord);
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
