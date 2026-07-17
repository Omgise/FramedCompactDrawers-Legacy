package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedController;
import com.mrfuzzihead.framedcompactdrawers.client.ClientProxy;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFramedController implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderer.renderStandardBlock(block, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        TileFramedController tile = (TileFramedController) world.getTileEntity(x, y, z);

        // Resolve icons from material stacks if available
        IIcon iconSide = null;
        IIcon iconFront = null;
        IIcon iconTrim = null;

        if (tile != null) {
            ItemStack sideStack = tile.getMaterialSide();
            if (sideStack != null && sideStack.getItem() != null) {
                Block matBlock = net.minecraft.block.Block.getBlockFromItem(sideStack.getItem());
                if (matBlock != null) {
                    iconSide = matBlock.getIcon(2, sideStack.getItemDamage());
                }
            }

            ItemStack frontStack = tile.getMaterialFront();
            if (frontStack != null && frontStack.getItem() != null) {
                Block matBlock = net.minecraft.block.Block.getBlockFromItem(frontStack.getItem());
                if (matBlock != null) {
                    iconFront = matBlock.getIcon(2, frontStack.getItemDamage());
                }
            }

            ItemStack trimStack = tile.getMaterialTrim();
            if (trimStack != null && trimStack.getItem() != null) {
                Block matBlock = net.minecraft.block.Block.getBlockFromItem(trimStack.getItem());
                if (matBlock != null) {
                    iconTrim = matBlock.getIcon(2, trimStack.getItemDamage());
                }
            }

            if (iconSide == null && block instanceof BlockFramedController) {
                iconSide = ((BlockFramedController) block).iconSide;
            } else if (iconSide == null) {
                iconSide = block.getIcon(2, world.getBlockMetadata(x, y, z));
            }
        } else {
            iconSide = block.getIcon(2, world.getBlockMetadata(x, y, z));
        }

        // Render the 6-face box with side icons (controller is a full cube)
        renderer.setRenderBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (iconSide != null) {
            renderer.renderStandardBlock(block, x, y, z);
        }

        // Render front face with material overlay if available
        if (iconFront != null) {
            float frontZ = 0.99f;
            renderer.setRenderBounds(0.0f, 0.0f, frontZ, 1.0f, 1.0f, 1.0f);
            net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, x, y, z, iconFront);
            tessellator.draw();
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.framedDrawerControllerRenderId;
    }
}
