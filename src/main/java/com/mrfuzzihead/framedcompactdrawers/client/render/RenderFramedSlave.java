package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.client.ClientProxy;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFramedSlave implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderer.renderStandardBlock(block, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        TileFramedSlave tile = (TileFramedSlave) world.getTileEntity(x, y, z);

        // Resolve icons from material stacks if available
        IIcon iconSide = null;
        IIcon iconTopBottom = null;
        IIcon iconTrim = null;

        if (tile != null && block instanceof com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave) {
            Block framedSlaveBlock = (com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave) block;

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
                    iconSide = matBlock.getIcon(2, frontStack.getItemDamage());
                }
            }

            ItemStack trimStack = tile.getMaterialTrim();
            if (trimStack != null && trimStack.getItem() != null) {
                Block matBlock = net.minecraft.block.Block.getBlockFromItem(trimStack.getItem());
                if (matBlock != null) {
                    iconTrim = matBlock.getIcon(2, trimStack.getItemDamage());
                }
            }

            // Use block's default icons as fallback for side and top/bottom
            if (iconSide == null) {
                iconSide = framedSlaveBlock.iconSide;
            }
            if (iconTopBottom == null) {
                iconTopBottom = framedSlaveBlock.iconTopBottom;
            }

            // Render trim overlay on sides
            if (iconTrim != null && iconSide != null) {
                float trimThickness = 0.01f;
                net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.instance;

                // Top trim face
                renderer.setRenderBounds(0.0f, 1.0f - trimThickness, 0.0f, 1.0f, 1.0f, 1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0f, 1.0f, 0.0f);
                renderer.renderFaceYPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Bottom trim face
                renderer.setRenderBounds(0.0f, 0.0f, 0.0f, 1.0f, trimThickness, 1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0f, -1.0f, 0.0f);
                renderer.renderFaceYPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Front trim face
                float frontZ = 1.0f - trimThickness;
                renderer.setRenderBounds(0.0f, 0.0f, frontZ, 1.0f, 1.0f, 1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0f, 0.0f, 1.0f);
                renderer.renderFaceZPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Back trim face
                float backZ = 0.0f;
                renderer.setRenderBounds(0.0f, 0.0f, backZ, 1.0f, 1.0f, backZ + trimThickness);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0f, 0.0f, -1.0f);
                renderer.renderFaceZPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Right trim face
                float rightX = 1.0f - trimThickness;
                renderer.setRenderBounds(rightX, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0f, 0.0f, 0.0f);
                renderer.renderFaceXPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Left trim face
                renderer.setRenderBounds(0.0f, 0.0f, 0.0f, trimThickness, 1.0f, 1.0f);
                tessellator.startDrawingQuads();
                tessellator.setNormal(-1.0f, 0.0f, 0.0f);
                renderer.renderFaceXPos(block, x, y, z, iconTrim);
                tessellator.draw();

                // Clear to full block bounds for main body rendering
                renderer.setRenderBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            }
        } else {
            iconSide = block.getIcon(2, world.getBlockMetadata(x, y, z));
            iconTopBottom = block.getIcon(0, world.getBlockMetadata(x, y, z));
        }

        // Render the main body: top/bottom faces use iconTopBottom, sides use iconSide
        renderer.setRenderBounds(0.0f, 1.0f - 1.0f / 16.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (iconTopBottom != null) {
            renderer.renderFaceYPos(block, x, y, z, iconTopBottom);
        }

        renderer.setRenderBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f / 16.0f, 1.0f);
        if (iconTopBottom != null) {
            renderer.renderFaceYPos(block, x, y, z, iconTopBottom);
        }

        // Side faces use iconSide
        renderer.setRenderBounds(0.0f, 0.0f, 1.0f - 1.0f / 16.0f, 1.0f, 1.0f, 1.0f);
        if (iconSide != null) {
            renderer.renderFaceZPos(block, x, y, z, iconSide);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.framedSlaveRenderId;
    }
}
