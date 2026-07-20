package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.block.tile.TileFramedSlave;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FramedSlaveRenderer implements ISimpleBlockRenderingHandler {

    private final PanelBoxRenderer panelRenderer = new PanelBoxRenderer();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        if (!(block instanceof BlockFramedSlave)) return false;
        BlockFramedSlave slave = (BlockFramedSlave) block;
        com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave te = slave.getTileEntity(world, x, y, z);
        if (!(te instanceof TileFramedSlave)) return false;
        TileFramedSlave tile = (TileFramedSlave) te;

        ItemStack matSide = tile.getMaterialSide();
        ItemStack matTrim = tile.getMaterialTrim();

        IIcon sideIcon = resolveIcon(matSide, slave.getDefaultFaceIcon());
        IIcon trimIcon = resolveIcon(matTrim, slave.getDefaultTrimIcon());

        double trimWidth = 0.0625;
        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        RenderHelper rh = RenderHelper.instances.get();
        rh.setColorAndBrightness(world, block, x, y, z);

        panelRenderer.setTrimIcon(trimIcon);
        panelRenderer.setPanelIcon(sideIcon);
        for (int i = 0; i < 6; i++) {
            panelRenderer.renderFacePanel(i, world, slave, x, y, z, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, world, slave, x, y, z, 0, 0, 0, 1, 1, 1);
        }

        rh.state.clearRotateTransform();
        rh.state.clearUVRotation(RenderHelper.YPOS);
        return true;
    }

    private IIcon resolveIcon(ItemStack stack, IIcon fallback) {
        if (stack == null || stack.getItem() == null) return fallback;
        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return fallback;
        IIcon icon = b.getIcon(4, stack.getItemDamage());
        return icon != null ? icon : fallback;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return FramedCompactDrawers.proxy.framedSlaveRenderID;
    }
}
