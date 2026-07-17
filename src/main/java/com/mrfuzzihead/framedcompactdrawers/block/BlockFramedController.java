package com.mrfuzzihead.framedcompactdrawers.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;
import com.mrfuzzihead.framedcompactdrawers.client.ClientProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedController extends BlockContainer {

    private static final ThreadLocal<Boolean> inTileLookup = ThreadLocal.withInitial(() -> false);

    public IIcon iconSide;

    public BlockFramedController() {
        super(Material.rock);
        this.setBlockName("framedcompactdrawers.framed_drawer_controller");
        this.setCreativeTab(FCDCreativeTab.TAB);
        this.setHardness(2.0f);
        this.setStepSound(Block.soundTypeStone);
        this.setLightOpacity(15);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFramedController();
    }

    /**
     * Get the framed controller tile entity safely with recursion guard.
     */
    protected TileFramedController getTrueTileEntity(TileEntity tile) {
        if (inTileLookup.get()) {
            return null;
        }
        inTileLookup.set(true);
        inTileLookup.set(false);
        return tile instanceof TileFramedController ? (TileFramedController) tile : null;
    }

    protected TileFramedController getTrueTileEntitySafe(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        TileFramedController tileCustom = this.getTrueTileEntity(tile);
        if (tileCustom == null) {
            tileCustom = new TileFramedController();
            world.setBlock(x, y, z, this);
        }
        return tileCustom;
    }

    /**
     * Ported from 1.12 AbstractBlockCustomNonDrawer.func_176213_c: correct the facing when placed
     * adjacent to solid blocks on one side but not the other.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            int facing = this.getDirectionFromMeta(meta);

            boolean northSolid = world.getBlock(x, y, z - 1)
                .isSideSolid(world, x, y, z - 1, ForgeDirection.SOUTH);
            boolean southSolid = world.getBlock(x, y, z + 1)
                .isSideSolid(world, x, y, z + 1, ForgeDirection.NORTH);
            boolean westSolid = world.getBlock(x - 1, y, z)
                .isSideSolid(world, x - 1, y, z, ForgeDirection.EAST);
            boolean eastSolid = world.getBlock(x + 1, y, z)
                .isSideSolid(world, x + 1, y, z, ForgeDirection.WEST);

            if (facing == 3 && !northSolid && southSolid) {
                facing = 2;
            } else if (facing == 2 && !southSolid && northSolid) {
                facing = 3;
            } else if (facing == 4 && !westSolid && eastSolid) {
                facing = 5;
            } else if (facing == 5 && !eastSolid && westSolid) {
                facing = 4;
            }

            world.setBlockMetadataWithNotify(x, y, z, this.getMetaFromDirection(facing), 2);
        }
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Get direction from player rotation, map to SD 1.7.10 meta convention: 2=SOUTH, 3=NORTH, 4=WEST, 5=EAST
        float yaw = placer.rotationYaw * 4 / 360 + 0.5f;
        int dir = (int) yaw;
        if (dir < 0) dir += 4;
        dir = dir % 4;
        int meta;
        switch (dir) {
            case 0:
                meta = 3;
                break; // NORTH
            case 1:
                meta = 2;
                break; // SOUTH
            case 2:
                meta = 5;
                break; // EAST
            case 3:
                meta = 4;
                break; // WEST
            default:
                meta = 3;
                break;
        }

        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te != null) {
            te.setDirection(meta);
        }

        // Apply material NBT from placed stack to tile entity
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

        super.onBlockPlacedBy(world, x, y, z, placer, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te == null) return false;

        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem != null) {
            // drawerKey doesn't exist in 1.7.10 SD - deferred
            if (heldItem.getItem() == ModItems.shroudKey) {
                te.toggleShroud(player.getGameProfile());
            } else if (heldItem.getItem() == ModItems.quantifyKey) {
                te.toggleQuantify(player.getGameProfile());
            } else if (heldItem.getItem() == ModItems.personalKey) {
                String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
                te.toggleProtection(player.getGameProfile(), provider);
            }
            return true;
        }

        // Put items into controller inventory when interacting
        te.interactPutItemsIntoInventory(player);
        return true;
    }

    /**
     * Ported from 1.12 func_180650_b: drop the item with material NBT when broken.
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
            if (te != null) {
                ItemStack drop = ItemCustomDrawers
                    .makeItemStack(this, 1, te.getMaterialSide(), te.getMaterialTrim(), te.getMaterialFront());

                NBTTagCompound data = new NBTTagCompound();
                te.writeToNBT(data);
                drop.setTagCompound(data);

                world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        String prefix = "framedcompactdrawers:textures/blocks/";

        this.iconSide = register.registerIcon(prefix + "raw_side");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        // Use the raw side icon for all sides except front
        if (side == 0 || side == 1) {
            return this.iconSide;
        }

        // Front face - we don't have a specific "front" block icon since the controller's appearance
        // is rendered via ISimpleBlockRenderingHandler. Return the side icon as fallback.
        return this.iconSide;
    }

    @Override
    public int getRenderType() {
        return ClientProxy.framedDrawerControllerRenderId;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * Only add a single plain stack to creative tab sub-items.
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List itemList) {
        itemList.add(new ItemStack(item));
    }

    /**
     * Convert stored meta back to integer direction (stored as: 2=SOUTH, 3=NORTH, 4=WEST, 5=EAST).
     */
    public static int getDirectionFromMeta(int meta) {
        switch (meta) {
            case 2:
                return 1; // SOUTH
            case 3:
                return 0; // NORTH
            case 4:
                return 3; // WEST
            case 5:
                return 2; // EAST
            default:
                return 0; // NORTH
        }
    }

    /**
     * Convert integer direction to stored meta value (2=SOUTH, 3=NORTH, 4=WEST, 5=EAST).
     */
    public static int getMetaFromDirection(int direction) {
        switch (direction) {
            case 0:
                return 3; // NORTH
            case 1:
                return 2; // SOUTH
            case 2:
                return 5; // EAST
            case 3:
                return 4; // WEST
            default:
                return 3; // NORTH
        }
    }

    /**
     * Delegate method for slave blocks to toggle the shroud on this controller.
     */
    public void toggleShroud(World world, int x, int y, int z, EntityPlayer player) {
        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te != null) {
            te.toggleShroud(player.getGameProfile());
        }
    }

    /**
     * Delegate method for slave blocks to toggle the quantify on this controller.
     */
    public void toggleQuantify(World world, int x, int y, int z, EntityPlayer player) {
        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te != null) {
            te.toggleQuantify(player.getGameProfile());
        }
    }

    /**
     * Delegate method for slave blocks to toggle the personal protection on this controller.
     */
    public void togglePersonal(World world, int x, int y, int z, EntityPlayer player) {
        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te != null) {
            String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
            ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
            te.toggleProtection(player.getGameProfile(), provider);
        }
    }
}
