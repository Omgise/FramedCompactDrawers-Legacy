package com.mrfuzzihead.framedcompactdrawers.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.block.EnumKeyType;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedSlave extends BlockContainer {

    public IIcon iconSide;
    public IIcon iconTopBottom;

    public BlockFramedSlave() {
        super(Material.rock);
        this.setBlockName("framedcompactdrawers.framed_slave");
        this.func_149647_a(FCDCreativeTab.TAB);
        this.setHardness(2.0f);
        this.setStepSound(Block.soundTypeStone);
        this.setLightOpacity(15);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFramedSlave();
    }

    protected TileFramedSlave getTrueTileEntitySafe(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileFramedSlave) {
            return (TileFramedSlave) tile;
        } else {
            TileFramedSlave slave = new TileFramedSlave();
            slave.setWorldAndCoordinates(world, x, y, z);
            world.setBlock(x, y, z, this);
            return slave;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        TileFramedSlave te = getTrueTileEntitySafe(world, x, y, z);
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
        TileFramedSlave te = getTrueTileEntitySafe(world, x, y, z);
        if (te == null || world.isRemote) return false;

        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem != null) {
            // Look up the slave tile's bound controller position and delegate key-toggle to it.
            TileEntitySlave slaveTile = te.getBoundController(world, x, y, z);
            if (slaveTile == null) {
                return true;
            }

            int cx = slaveTile.xCoord;
            int cy = slaveTile.yCoord;
            int cz = slaveTile.zCoord;
            net.minecraft.block.Block controllerBlock = world.getBlock(cx, cy, cz);

            if (controllerBlock instanceof BlockFramedController) {
                this.delegateToggleToController(
                    (BlockFramedController) controllerBlock,
                    world,
                    cx,
                    cy,
                    cz,
                    player,
                    heldItem);
                return true;
            } else if (controllerBlock instanceof com.jaquadro.minecraft.storagedrawers.block.BlockController) {
                // If the bound controller is a vanilla one, delegate to it directly.
                com.jaquadro.minecraft.storagedrawers.block.BlockController vanilla = (com.jaquadro.minecraft.storagedrawers.block.BlockController) controllerBlock;
                this.delegateToggleToVanillaController(vanilla, world, cx, cy, cz, player, heldItem);
                return true;
            }
        }
        return false;
    }

    private void delegateToggleToController(
        com.jaquadro.minecraft.storagedrawers.block.BlockFramedController controller, World world, int x, int y, int z,
        EntityPlayer player, ItemStack heldItem) {
        if (heldItem.getItem() == ModItems.drawerKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.DRAWER);
        } else if (heldItem.getItem() == ModItems.shroudKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.CONCEALMENT);
        } else if (heldItem.getItem() == ModItems.quantifyKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.QUANTIFY);
        } else if (heldItem.getItem() == ModItems.personalKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.PERSONAL);
        }
    }

    private void delegateToggleToVanillaController(
        com.jaquadro.minecraft.storagedrawers.block.BlockController controller, World world, int x, int y, int z,
        EntityPlayer player, ItemStack heldItem) {
        if (heldItem.getItem() == ModItems.drawerKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.DRAWER);
        } else if (heldItem.getItem() == ModItems.shroudKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.CONCEALMENT);
        } else if (heldItem.getItem() == ModItems.quantifyKey) {
            controller.toggle(world, x, y, z, player, EnumKeyType.QUANTIFY);
        } else if (heldItem.getItem() == ModItems.personalKey) {
            String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
            ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
            controller.toggleProtection(world, x, y, z, player, provider);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        if (!world.isRemote) {
            TileFramedSlave te = getTrueTileEntitySafe(world, x, y, z);
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
        this.iconTopBottom = register.registerIcon(prefix + "slave_raw_top_bottom");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            return iconTopBottom;
        }
        return iconSide;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom rendering via ISimpleBlockRenderingHandler
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
}
