package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;

public class ItemFramedSlave extends ItemCustomDrawers {

    private final Block block;

    public ItemFramedSlave(Block block) {
        super(block);
        this.block = block;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int meta) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        }

        meta = 0; // Slave blocks don't have direction metadata in vanilla StorageDrawers
        Block block = this.block;

        if (!world.setBlock(x, y, z, block, meta, 3)) {
            return false;
        }

        block.onBlockPlacedBy(world, x, y, z, player, stack);

        // Apply material NBT from placed stack to tile entity
        TileFramedSlave te = (TileFramedSlave) world.getTileEntity(x, y, z);
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
