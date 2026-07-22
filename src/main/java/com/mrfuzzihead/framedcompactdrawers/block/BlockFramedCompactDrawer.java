package com.mrfuzzihead.framedcompactdrawers.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedCompactDrawer extends BlockCompDrawers {

    private IIcon overlayHandle;
    private IIcon overlayFaceShadow;
    private IIcon overlayTrimShadow;
    private IIcon overlayTrimFace;
    private IIcon defaultFace;
    private IIcon defaultTrim;

    public BlockFramedCompactDrawer() {
        super(FramedCompactDrawers.MODID + ".framed_compact_drawer");
        setStepSound(Block.soundTypeWood);
        setHarvestLevel("axe", 0);
    }

    @Override
    public int getRenderType() {
        return FramedCompactDrawers.proxy.framedCompactDrawerRenderID;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public TileEntityDrawers createNewTileEntity(World world, int meta) {
        return new TileFramedCompactDrawer();
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        if (StorageDrawers.config.cache.addonShowVanilla) {
            list.add(new ItemStack(item, 1, 0));
        }
    }

    @Override
    protected ItemStack getMainDrop(World world, int x, int y, int z, int metadata) {
        TileEntityDrawers tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            return ItemCustomDrawers.makeItemStack(this, 1, null, null, null);
        }
        return ItemCustomDrawers
            .makeItemStack(this, 1, tile.getMaterialSide(), tile.getMaterialTrim(), tile.getMaterialFront());
    }

    public IIcon getOverlayIcon(int side, int maxStorageLevel) {
        if (side == 0 || side == 1) {
            return overlayHandle;
        }

        if (side == 2 || side == 3 || side == 4) {
            return overlayFaceShadow;
        }

        if (side == 5) {
            return overlayTrimShadow;
        }

        return null;
    }

    public IIcon getOverlayIconTrim(int maxStorageLevel) {
        return getOverlayIconTrim();
    }

    public IIcon getOverlayIconTrim() {
        return overlayTrimShadow;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getHandleOverlay() {
        return overlayHandle;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFaceShadowOverlay() {
        return overlayFaceShadow;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getTrimShadowOverlay(boolean strong) {
        if (strong) {
            return overlayTrimFace;
        } else {
            return overlayTrimShadow;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultFaceIcon() {
        return defaultFace;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultTrimIcon() {
        return defaultTrim;
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);

        overlayHandle = register.registerIcon(FramedCompactDrawers.MODID + ":overlay/handle");
        overlayFaceShadow = register.registerIcon(FramedCompactDrawers.MODID + ":overlay/shading_face");
        overlayTrimShadow = register.registerIcon(FramedCompactDrawers.MODID + ":overlay/shading_trim");
        overlayTrimFace = register.registerIcon(FramedCompactDrawers.MODID + ":overlay/shading_bold_trim");

        defaultFace = register.registerIcon(FramedCompactDrawers.MODID + ":raw_side");
        defaultTrim = register.registerIcon(FramedCompactDrawers.MODID + ":raw_side");
    }
}
