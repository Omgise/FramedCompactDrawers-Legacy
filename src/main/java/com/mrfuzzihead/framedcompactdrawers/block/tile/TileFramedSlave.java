package com.mrfuzzihead.framedcompactdrawers.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;

public class TileFramedSlave extends TileEntitySlave {

    private ItemStack matSide = null;
    private ItemStack matFront = null;
    private ItemStack matTrim = null;

    public ItemStack getMaterialSide() {
        return matSide;
    }

    public void setMaterialSide(ItemStack stack) {
        this.matSide = stack;
    }

    public ItemStack getMaterialFront() {
        return matFront;
    }

    public void setMaterialFront(ItemStack stack) {
        this.matFront = stack;
    }

    public ItemStack getMaterialTrim() {
        return matTrim;
    }

    public void setMaterialTrim(ItemStack stack) {
        this.matTrim = stack;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("MatS")) {
            matSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
        } else {
            matSide = null;
        }
        if (tag.hasKey("MatF")) {
            matFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));
        } else {
            matFront = null;
        }
        if (tag.hasKey("MatT")) {
            matTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
        } else {
            matTrim = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (matSide != null) {
            NBTTagCompound matS = new NBTTagCompound();
            matSide.writeToNBT(matS);
            tag.setTag("MatS", matS);
        }
        if (matFront != null) {
            NBTTagCompound matF = new NBTTagCompound();
            matFront.writeToNBT(matF);
            tag.setTag("MatF", matF);
        }
        if (matTrim != null) {
            NBTTagCompound matT = new NBTTagCompound();
            matTrim.writeToNBT(matT);
            tag.setTag("MatT", matT);
        }
    }

    /**
     * Update the slave tile entity.
     */
    public void updateEntity() {
        super.updateEntity();
    }
}
