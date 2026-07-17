package com.mrfuzzihead.framedcompactdrawers.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;

public class ItemFramedController extends ItemCustomDrawers {

    public ItemFramedController(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int meta) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        }

        // Get direction from player rotation
        int dirMeta = getDirectionFromPlayerRotationFCD(player);

        if (!world.setBlock(x, y, z, this.field_150939_a, dirMeta, 3)) {
            return false;
        }

        this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);

        // Apply material NBT from placed stack to tile entity
        TileFramedController te = (TileFramedController) world.getTileEntity(x, y, z);
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

    private int getDirectionFromPlayerRotationFCD(EntityPlayer player) {
        float yaw = player.rotationYaw * 4 / 360 + 0.5f;
        int dir = (int) yaw;
        if (dir < 0) dir += 4;
        return dir % 4; // 0=NORTH, 1=SOUTH, 2=WEST, 3=EAST
    }
}
