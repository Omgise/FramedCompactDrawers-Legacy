package com.mrfuzzihead.framedcompactdrawers.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedCompactDrawer extends BlockDrawersCustom {

    public BlockFramedCompactDrawer() {
        super("framedcompactdrawers.framed_compact_drawer", "framed_compact_drawer", 3, false);
        this.func_149647_a(FCDCreativeTab.TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFramedCompactDrawer();
    }

    /**
     * Ported from 1.12 BlockDrawersCustomComp.getDrawerSlot: top hit -> slot 0, else left/right via hitLeft/hitTop
     * helpers.
     */
    protected int getDrawerSlot(int side, float hitX, float hitY, float hitZ) {
        if (this.hitTop(hitY)) {
            return 0;
        } else {
            return this.hitLeft(side, hitX, hitZ) ? 1 : 2;
        }
    }

    @Override
    protected int getDrawerCount() {
        return 3;
    }

    /**
     * Only add a single plain stack to creative tab sub-items (mirrors BlockDrawersCustom.getSubBlocks behavior).
     */
    @Override
    public void getSubBlocks(Block block, CreativeTabs creativeTabs, List itemList) {
        if (StorageDrawers.config.cache.addonShowVanilla) {
            itemList.add(new ItemStack(block));
        }
    }

    /**
     * Ported from 1.12 BlockDrawersCustomComp.getMainDrop: serialize material + sealed tile NBT when
     * keepContentsOnBreak is on.
     */
    @Override
    protected ItemStack getMainDrop(IBlockAccess world, int x, int y, int z) {
        TileEntityDrawersComp tile = this.getTileEntity(world, x, y, z);
        if (tile == null) {
            return ItemCustomDrawers.makeItemStack(this, 1, (ItemStack) null, (ItemStack) null, (ItemStack) null);
        } else {
            ItemStack drop = ItemCustomDrawers
                .makeItemStack(this, 1, tile.getMaterialSide(), tile.getMaterialTrim(), tile.getMaterialFront());

            if (drop == null || drop.getItem() == null) {
                return ItemStack.EMPTY;
            }

            NBTTagCompound data = new NBTTagCompound();
            boolean hasContents = false;
            if (StorageDrawers.config.cache.keepContentsOnBreak) {
                for (int i = 0; i < tile.getGroup()
                    .getDrawerCount(); i++) {
                    if (!tile.getGroup()
                        .getDrawer(i)
                        .isEmpty()) {
                        hasContents = true;
                        break;
                    }
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

        // Register the 3 per-slot front textures: drawers_comp_raw_open_1, _2, _3
        for (int i = 1; i <= 3; i++) {
            this.iconFront1[i] = register
                .registerIcon("framedcompactdrawers:textures/blocks/drawers_comp_raw_open_" + i);
        }

        // Register disabled-slot overlay icons: open_1, open_2, open_3
        for (int i = 0; i < iconOverlay.length; i++) {
            String suffix = "open_" + (i + 1);
            if (iconOverlay[i] != null) {
                iconOverlay[i] = register.registerIcon("framedcompactdrawers:textures/blocks/overlay/" + suffix);
            } else {
                iconOverlay[i] = register.registerIcon("framedcompactdrawers:textures/blocks/overlay/" + suffix);
            }
        }
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
            return this.iconSideV;
        }
        // For the front face, delegate to getDrawerIcon logic from BlockDrawersCustom which picks the correct slot's
        // icon
        return super.getIcon(side, meta);
    }
}
