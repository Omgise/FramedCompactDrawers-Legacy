package com.mrfuzzihead.framedcompactdrawers.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockSlave;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemPersonalKey;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedSlave extends BlockSlave {

    @SideOnly(Side.CLIENT)
    private IIcon overlayHandle;

    @SideOnly(Side.CLIENT)
    private IIcon overlayFaceShadow;

    @SideOnly(Side.CLIENT)
    private IIcon overlayTrimShadow;

    @SideOnly(Side.CLIENT)
    private IIcon overlayTrimFace;

    @SideOnly(Side.CLIENT)
    private IIcon defaultFace;

    @SideOnly(Side.CLIENT)
    private IIcon defaultTrim;

    public BlockFramedSlave() {
        super(FramedCompactDrawers.MODID + ".framed_slave");
    }

    @Override
    public int getRenderType() {
        return FramedCompactDrawers.proxy.framedSlaveRenderID;
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileFramedSlave slave = getTileEntitySafe(world, x, y, z);
        if (slave == null) return false;

        ItemStack item = player.inventory.getCurrentItem();
        if (item != null && item.getItem() != null) {
            if (item.getItem() == ModItems.upgradeLock) {
                if (!world.isRemote) {
                    TileEntityController controller = slave.getController();
                    if (controller != null) {
                        controller.toggleLock(
                            java.util.EnumSet.allOf(LockAttribute.class),
                            LockAttribute.LOCK_POPULATED,
                            player.getGameProfile());
                    }
                }
                return true;
            } else if (item.getItem() == ModItems.shroudKey) {
                if (!world.isRemote) {
                    TileEntityController controller = slave.getController();
                    if (controller != null) controller.toggleShroud(player.getGameProfile());
                }
                return true;
            } else if (item.getItem() == ModItems.quantifyKey) {
                if (!world.isRemote) {
                    TileEntityController controller = slave.getController();
                    if (controller != null) controller.toggleQuantify(player.getGameProfile());
                }
                return true;
            } else if (item.getItem() instanceof ItemPersonalKey) {
                if (!world.isRemote) {
                    TileEntityController controller = slave.getController();
                    if (controller != null) {
                        String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
                        ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
                        controller.toggleProtection(player.getGameProfile(), provider);
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public TileFramedSlave createNewTileEntity(World world, int meta) {
        return new TileFramedSlave();
    }

    public TileFramedSlave getTileEntityFramed(World world, int x, int y, int z) {
        TileFramedSlave tile = (TileFramedSlave) world.getTileEntity(x, y, z);
        return tile;
    }

    public TileFramedSlave getTileEntitySafe(World world, int x, int y, int z) {
        TileFramedSlave tile = getTileEntityFramed(world, x, y, z);
        if (tile == null) {
            tile = new TileFramedSlave();
            tile.xCoord = x;
            tile.yCoord = y;
            tile.zCoord = z;
            tile.setWorldObj(world);
            world.setTileEntity(x, y, z, tile);
        }
        return tile;
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);

        overlayHandle = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/handle_2");
        overlayFaceShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_face_2");
        overlayTrimShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_trim_2");
        overlayTrimFace = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_boldtrim_2");

        defaultFace = register.registerIcon(StorageDrawers.MOD_ID + ":base/base_default");
        defaultTrim = register.registerIcon(StorageDrawers.MOD_ID + ":base/trim_default");
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
        if (strong) return overlayTrimFace;
        else return overlayTrimShadow;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultFaceIcon() {
        return defaultFace;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultTrimIcon() {
        return defaultTrim;
    }
}
