package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedCompactDrawer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFramedCompactDrawer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderer.renderStandardBlock(block, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        TileFramedCompactDrawer tile = (TileFramedCompactDrawer) world.getTileEntity(x, y, z);

        // Resolve icons from material stacks if available, otherwise use defaults
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

            // Use tile's default icons as fallback
            if (iconSide == null) {
                iconSide = tile.getBlockType()
                    .getIcon(2, world.getBlockMetadata(x, y, z));
            }
        } else {
            iconSide = block.getIcon(2, world.getBlockMetadata(x, y, z));
        }

        // Render the main drawer body (6-face box with side icons)
        renderer.setRenderBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (iconSide != null) {
            renderer.renderStandardBlock(block, x, y, z);
        }

        // Render the 4 split-front panels for each of the 3 drawers:
        // Top drawer (slot 0): top third of front face
        // Left drawer (slot 1): left two-thirds of middle area
        // Right drawer (slot 2): right one-third of middle area
        float frontZ = 0.99f;

        // Slot 0 - top drawer (top third)
        float topY = 0.67f;
        renderer.setRenderBounds(0.0f, topY, frontZ, 1.0f, 1.0f, 1.0f);
        IIcon slot0Front = getSlotFrontIcon(tile, 0);
        if (slot0Front != null) {
            renderer.renderFaceYPos(block, x, y, z, side, slot0Front);
        }

        // Slot 1 - left drawer (left portion of middle two-thirds)
        float midTop = 0.34f;
        float midBottom = topY;
        renderer.setRenderBounds(0.0f, midBottom, frontZ, 0.67f, midTop, 1.0f);
        IIcon slot1Front = getSlotFrontIcon(tile, 1);
        if (slot1Front != null) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, x, y, z, slot1Front);
            tessellator.draw();
        }

        // Slot 2 - right drawer (right one-third of middle two-thirds)
        float midLeft = 0.67f;
        renderer.setRenderBounds(midLeft, midBottom, frontZ, 1.0f, midTop, 1.0f);
        IIcon slot2Front = getSlotFrontIcon(tile, 2);
        if (slot2Front != null) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, x, y, z, slot2Front);
            tessellator.draw();
        }

        // Render trim overlay on front face
        if (iconTrim != null) {
            renderer.setRenderBounds(0.0f, midBottom, frontZ - 0.01f, 1.0f, midTop, frontZ);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, x, y, z, iconTrim);
            tessellator.draw();

            // Trim border on sides
            renderer.setRenderBounds(0.0f, midBottom, frontZ - 0.01f, 0.67f, midTop, frontZ);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, x, y, z, iconTrim);
            tessellator.draw();
        }

        return true;
    }

    private IIcon getSlotFrontIcon(TileFramedCompactDrawer tile, int slotIndex) {
        if (tile == null) return null;

        // For each drawer slot, check if it has material and use that icon
        for (int i = 0; i < 3; i++) {
            ItemStack matStack = getMaterialForSlot(tile, i);
            if (matStack != null && matStack.getItem() != null) {
                Block matBlock = net.minecraft.block.Block.getBlockFromItem(matStack.getItem());
                if (matBlock != null) {
                    return matBlock.getIcon(2, matStack.getItemDamage());
                }
            }
        }

        // Fallback: use the drawer's default front icon
        int meta = tile.getWorldObj() != null ? tile.getWorldObj()
            .getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord) : 0;
        return tile.getBlockType()
            .getIcon(2, meta);
    }

    private ItemStack getMaterialForSlot(TileFramedCompactDrawer tile, int slotIndex) {
        // In the actual StorageDrawers 1.7.10 implementation, material is per-drawer-group, not per-slot.
        // For simplicity in this port, we use the side material for all slots.
        return tile.getMaterialSide();
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.framedCompactDrawerRenderId;
    }
}
