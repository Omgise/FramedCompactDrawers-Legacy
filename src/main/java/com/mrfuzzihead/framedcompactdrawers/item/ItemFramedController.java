package com.mrfuzzihead.framedcompactdrawers.item;

import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemFramedController extends ItemCustomDrawers {

    public ItemFramedController(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(StatCollector.translateToLocal("storagedrawers.controller.description"));
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) {
            return false;
        }

        if (world.getBlock(x, y, z) == field_150939_a) {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
        }

        TileFramedController tile = (TileFramedController) world.getTileEntity(x, y, z);
        if (tile != null && stack.hasTagCompound()
            && !stack.getTagCompound()
                .hasKey("tile")) {
            if (stack.getTagCompound()
                .hasKey("MatS"))
                tile.setMaterialSide(
                    ItemStack.loadItemStackFromNBT(
                        stack.getTagCompound()
                            .getCompoundTag("MatS")));
            if (stack.getTagCompound()
                .hasKey("MatT"))
                tile.setMaterialTrim(
                    ItemStack.loadItemStackFromNBT(
                        stack.getTagCompound()
                            .getCompoundTag("MatT")));
            if (stack.getTagCompound()
                .hasKey("MatF"))
                tile.setMaterialFront(
                    ItemStack.loadItemStackFromNBT(
                        stack.getTagCompound()
                            .getCompoundTag("MatF")));
        }

        return true;
    }

    public ItemStack getStack(int metadata) {
        return ItemCustomDrawers.makeItemStack(field_150939_a, 1, null, null, null);
    }

    /** Build a drop ItemStack that preserves the tile's material data. */
    public ItemStack makeDropStack(TileFramedController tile) {
        return ItemCustomDrawers
            .makeItemStack(field_150939_a, 1, tile.getMaterialSide(), tile.getMaterialTrim(), tile.getMaterialFront());
    }
}
