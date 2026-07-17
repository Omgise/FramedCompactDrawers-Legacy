package com.mrfuzzihead.framedcompactdrawers.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.client.ClientProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedCompactDrawer extends BlockDrawersCustom {

    public BlockFramedCompactDrawer() {
        super("framedcompactdrawers.framed_compact_drawer", 3, false);
        this.setCreativeTab(FCDCreativeTab.TAB);
    }

    @Override
    public com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers createNewTileEntity(World world,
        int meta) {
        return new TileFramedCompactDrawer();
    }

    /**
     * Ported from 1.12 BlockDrawersCustomComp.getDrawerSlot: top hit -> slot 0, else left/right via hitLeft/hitTop
     * helpers.
     */
    public int getDrawerSlot(int side, float hitX, float hitY, float hitZ) {
        if (this.hitTop(hitY)) {
            return 0;
        } else {
            return this.hitLeft(side, hitX, hitZ) ? 1 : 2;
        }
    }

    protected int getDrawerCount() {
        return 3;
    }

    /**
     * Only add a single plain stack to creative tab sub-items (mirrors BlockDrawersCustom.getSubBlocks behavior).
     */
    @Override
    public void getSubBlocks(net.minecraft.item.Item item, CreativeTabs creativeTabs, List itemList) {
        if (StorageDrawers.config.cache.addonShowVanilla) {
            itemList.add(new ItemStack(item));
        }
    }

    @Override
    public int getRenderType() {
        return ClientProxy.framedCompactDrawerRenderId;
    }

    /**
     * Ported from 1.12 BlockDrawersCustomComp.getMainDrop: serialize material + sealed tile NBT.
     */
    @Override
    protected ItemStack getMainDrop(World world, int x, int y, int z, int meta) {
        TileEntityDrawers tile = this.getTileEntity(world, x, y, z);
        if (tile == null) {
            return ItemCustomDrawers.makeItemStack(this, 1, (ItemStack) null, (ItemStack) null, (ItemStack) null);
        } else {
            ItemStack drop = ItemCustomDrawers
                .makeItemStack(this, 1, tile.getMaterialSide(), tile.getMaterialTrim(), tile.getMaterialFront());

            if (drop == null || drop.getItem() == null) {
                return null;
            }

            NBTTagCompound data = new NBTTagCompound();
            boolean hasContents = false;
            for (int i = 0; i < tile.getDrawerCount(); i++) {
                if (!tile.getDrawer(i)
                    .isEmpty()) {
                    hasContents = true;
                    break;
                }
            }

            if (hasContents || tile.isSealed()) {
                NBTTagCompound tiledata = new NBTTagCompound();
                tile.writeToNBT(tiledata);
                data.setTag("tile", tiledata);
            }

            drop.setTagCompound(data);
            return drop;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        super.registerBlockIcons(register);

        // Register the 1 front texture for the compact drawer (iconFront1 is a 1-element array)
        this.iconFront1[0] = register.registerIcon("framedcompactdrawers:textures/blocks/drawers_comp_raw_open_1");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Let the base BlockDrawersCustom handle normal drawer interaction (open/close, put items in, etc.)
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        // For bottom/top faces use iconSideV (same as base BlockDrawersCustom)
        if (side == 0 || side == 1) {
            return this.iconSideV[0];
        }
        // For the front face, delegate to getDrawerIcon logic from BlockDrawersCustom which picks the correct slot's
        // icon
        return super.getIcon(side, meta);
    }
}
