package com.mrfuzzihead.framedcompactdrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class BlockFramedCompactDrawer extends BlockDrawersCustom {

    private IIcon overlayHandle;
    private IIcon overlayFaceShadow;
    private IIcon overlayTrimShadow;
    private IIcon overlayTrimFace;
    private IIcon defaultFace;
    private IIcon defaultTrim;

    public BlockFramedCompactDrawer() {
        super(FramedCompactDrawers.MODID + ".framed_compact_drawer", 3, false);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public int getDrawerSlot(int side, float hitX, float hitY, float hitZ) {
        if (hitTop(hitY)) {
            return 0;
        }

        return hitLeft(side, hitX, hitZ) ? 1 : 2;
    }

    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        if (StorageDrawers.config.cache.addonShowVanilla) {
            list.add(new ItemStack(item, 1, 0));
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntityDrawers tile = getTileEntity(world, x, y, z);
        if (tile != null && tile.getMaterialSide() == null) {
            return false;
        }

        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public TileEntityDrawers createNewTileEntity(World world, int meta) {
        return new TileFramedCompactDrawer();
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

        overlayHandle = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/handle_3");
        overlayFaceShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_face_3");
        overlayTrimShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_trim_3");
        overlayTrimFace = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_boldtrim_3");

        defaultFace = register.registerIcon(StorageDrawers.MOD_ID + ":base/base_default");
        defaultTrim = register.registerIcon(StorageDrawers.MOD_ID + ":base/trim_default");

        iconSide[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconSideV[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconSideH[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconTrim[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconFront1[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_1");
        iconFront2[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_2");
        iconFront4[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_4");
    }
}
