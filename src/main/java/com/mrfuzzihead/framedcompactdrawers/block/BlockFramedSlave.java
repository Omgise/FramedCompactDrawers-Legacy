package com.mrfuzzihead.framedcompactdrawers.block;

import net.minecraft.block.Block;
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
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.FCDCreativeTab;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.client.ClientProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFramedSlave extends BlockContainer {

    public IIcon iconSide;
    public IIcon iconTopBottom;

    public BlockFramedSlave() {
        super(Material.rock);
        this.setBlockName("framedcompactdrawers.framed_slave");
        this.setCreativeTab(FCDCreativeTab.TAB);
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
            com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController controller = te.getController();
            if (controller == null) {
                return true;
            }

            if (heldItem.getItem() == ModItems.shroudKey) {
                controller.toggleShroud(player.getGameProfile());
            } else if (heldItem.getItem() == ModItems.quantifyKey) {
                controller.toggleQuantify(player.getGameProfile());
            } else if (heldItem.getItem() == ModItems.personalKey) {
                String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);
                controller.toggleProtection(player.getGameProfile(), provider);
            }
            return true;
        }
        return false;
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

                world.spawnEntityInWorld(
                    new net.minecraft.entity.item.EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop));
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
        return ClientProxy.framedSlaveRenderId;
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
    public void getSubBlocks(net.minecraft.item.Item item, CreativeTabs creativeTabs, java.util.List itemList) {
        itemList.add(new ItemStack(item));
    }
}
