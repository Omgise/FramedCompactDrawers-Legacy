package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;

public class ItemFramedSlave extends ItemCustomDrawers {

    public ItemFramedSlave(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
        float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.canBlockBePlaced(world.getBlockState(pos), pos, side, null)) {
            return false;
        }

        int meta = 0; // Slave blocks don't have direction metadata in vanilla StorageDrawers
        Block block = this.block;

        if (!world.setBlock(pos.getX(), pos.getY(), pos.getZ(), block, meta, 3)) {
            return false;
        }

        block.onBlockPlacedBy(world, pos.getX(), pos.getY(), pos.getZ(), player, stack);

        // Apply material NBT from placed stack to tile entity
        TileFramedSlave te = (TileFramedSlave) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (te != null && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("MatS")) {
                te.setMaterialSide(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("MatS")));
            }
            if (nbt.hasKey("MatT")) {
                te.setMaterialTrim(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("MatT")));
            }
            if (nbt.hasKey("MatF")) {
                te.setMaterialFront(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("MatF")));
            }
        }

        return true;
    }
}
