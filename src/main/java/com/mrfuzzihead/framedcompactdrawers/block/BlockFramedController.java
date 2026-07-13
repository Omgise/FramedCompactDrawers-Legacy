package com.mrfuzzihead.framedcompactdrawers.block;

import java.util.EnumSet;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedController extends BlockContainer {

    private static final ThreadLocal<Boolean> inTileLookup = ThreadLocal.withInitial(() -> false);

    public IIcon iconSide;

    public BlockFramedController() {
        super(Material.rock);
        this.setBlockName("framedcompactdrawers.framed_drawer_controller");
        this.func_149647_a(FCDCreativeTab.TAB);
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
            tileCustom.setWorldAndCoordinates(world, x, y, z);
            world.setBlock(x, y, z, this);
        }
        return tileCustom;
    }

    /**
     * Ported from 1.12 AbstractBlockCustomNonDrawer.func_176213_c: correct the facing when placed
     * adjacent to solid blocks on one side but not the other.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block neighbor) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            EnumFacing facing = this.getDirectionFromMeta(meta);

            boolean northSolid = world.getBlock(x, y, z - 1)
                .isSideSolid(world, x, y, z - 1, EnumFacing.SOUTH);
            boolean southSolid = world.getBlock(x, y, z + 1)
                .isSideSolid(world, x, y, z + 1, EnumFacing.NORTH);
            boolean westSolid = world.getBlock(x - 1, y, z)
                .isSideSolid(world, x - 1, y, z, EnumFacing.EAST);
            boolean eastSolid = world.getBlock(x + 1, y, z)
                .isSideSolid(world, x + 1, y, z, EnumFacing.WEST);

            if (facing == EnumFacing.NORTH && !northSolid && southSolid) {
                facing = EnumFacing.SOUTH;
            } else if (facing == EnumFacing.SOUTH && !southSolid && northSolid) {
                facing = EnumFacing.NORTH;
            } else if (facing == EnumFacing.WEST && !westSolid && eastSolid) {
                facing = EnumFacing.EAST;
            } else if (facing == EnumFacing.EAST && !eastSolid && westSolid) {
                facing = EnumFacing.WEST;
            }

            world.setBlockMetadataWithNotify(x, y, z, this.getMetaFromDirection(facing), 2);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = EnumFacing.getDirectionFromEntityRotation(x, z, placer.rotationYaw);
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        // Store direction in metadata for later retrieval
        int meta = this.getMetaFromDirection(facing);
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

        // Check if a key item is being used on the controller first
        ItemStack heldItem = player.getCurrentEquippedItem();
        boolean toggled = false;
        if (heldItem != null) {
            if (heldItem.getItem() == ModItems.drawerKey) {
                this.toggle(world, x, y, z, player, EnumKeyType.DRAWER);
                toggled = true;
            } else if (heldItem.getItem() == ModItems.shroudKey) {
                this.toggle(world, x, y, z, player, EnumKeyType.CONCEALMENT);
                toggled = true;
            } else if (heldItem.getItem() == ModItems.quantifyKey) {
                this.toggle(world, x, y, z, player, EnumKeyType.QUANTIFY);
                toggled = true;
            } else if (heldItem.getItem() == ModItems.personalKey) {
                this.toggle(world, x, y, z, player, EnumKeyType.PERSONAL);
                toggled = true;
            }
        }

        // Get the direction that was stored in the tile entity
        int meta = world.getBlockMetadata(x, y, z);
        EnumFacing blockDir = this.getDirectionFromMeta(meta);

        if (!toggled && te != null) {
            // Put items into controller inventory when interacting with front face
            if (heldItem == null && side == 0) {
                te.interactPutItemsIntoInventory(player);
                return true;
            }
            return true;
        }

        return toggled;
    }

    /**
     * Toggle controller features via key items.
     */
    public void toggle(World world, int x, int y, int z, EntityPlayer player, EnumKeyType keyType) {
        if (world.isRemote) return;

        TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
        if (te == null) return;

        switch (keyType) {
            case DRAWER:
                te.toggleLock(
                    EnumSet.allOf(LockAttribute.class),
                    LockAttribute.LOCK_POPULATED,
                    player.getGameProfile()
                        .getName());
                break;
            case CONCEALMENT:
                te.toggleShroud(player.getGameProfile());
                break;
            case QUANTIFY:
                te.toggleQuantify(player.getGameProfile());
                break;
            case PERSONAL:
                String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
                te.toggleProtection(player.getGameProfile(), provider);
                break;
        }
    }

    /**
     * Ported from 1.12 func_180650_b: drop the item with material NBT when broken.
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        if (!world.isRemote) {
            TileFramedController te = this.getTrueTileEntitySafe(world, x, y, z);
            if (te != null) {
                ItemStack drop = ItemCustomDrawers
                    .makeItemStack(this, 1, te.getMaterialSide(), te.getMaterialTrim(), te.getMaterialFront());

                NBTTagCompound data = new NBTTagCompound();
                te.writeToNBT(data);
                drop.setTagCompound(data);

                world.spawnBlockInEntity(x + 0.5, y + 0.5, z + 0.5, drop);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
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
        return -1; // Custom rendering, not vanilla model
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
    public void getSubBlocks(net.minecraft.block.Block block, CreativeTabs creativeTabs, java.util.List itemList) {
        itemList.add(new ItemStack(block));
    }

    /**
     * Convert stored meta back to EnumFacing direction (stored as: 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST).
     */
    public static EnumFacing getDirectionFromMeta(int meta) {
        switch (meta) {
            case 0:
                return EnumFacing.NORTH; // default
            case 1:
                return EnumFacing.SOUTH;
            case 2:
                return EnumFacing.WEST;
            case 3:
                return EnumFacing.EAST;
            default:
                return EnumFacing.NORTH;
        }
    }

    /**
     * Convert EnumFacing to stored meta value.
     */
    public static int getMetaFromDirection(EnumFacing direction) {
        if (direction == EnumFacing.SOUTH) return 1;
        if (direction == EnumFacing.WEST) return 2;
        if (direction == EnumFacing.EAST) return 3;
        return 0; // NORTH
    }
}
