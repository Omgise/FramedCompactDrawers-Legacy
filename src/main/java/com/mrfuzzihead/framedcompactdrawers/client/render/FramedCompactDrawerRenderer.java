package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FramedCompactDrawerRenderer extends DrawersRenderer {

    private final PanelBoxRenderer panelRenderer = new PanelBoxRenderer();
    private final ModularBoxRenderer invBoxRenderer = new ModularBoxRenderer();
    private double trimWidth;
    private double trimDepth;
    private static final double lessThanHalf = 0.4375;
    private static final double moreThanHalf = 0.5625;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockFramedCompactDrawer)) return;
        BlockFramedCompactDrawer framed = (BlockFramedCompactDrawer) block;

        IIcon icon = framed.getDefaultFaceIcon();

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        invBoxRenderer.setUnit(0.0625);
        invBoxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        invBoxRenderer.setIcon(icon);

        invBoxRenderer.renderSolidBox(null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);

        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
    }

    @Override
    protected void renderBaseBlock(IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block,
        RenderBlocks renderer) {
        BlockFramedCompactDrawer framed = (BlockFramedCompactDrawer) block;

        ItemStack matSide = tile.getMaterialSide();
        if (matSide == null) matSide = new ItemStack(block);
        ItemStack matFront = tile.getMaterialFront();
        if (matFront == null) matFront = matSide;
        ItemStack matTrim = tile.getMaterialTrim();
        if (matTrim == null) matTrim = matSide;

        IIcon sideIcon = resolveIcon(matSide, framed.getDefaultFaceIcon());
        IIcon trimIcon = resolveIcon(matTrim, framed.getDefaultTrimIcon());
        IIcon frontIcon = resolveIcon(matFront, framed.getDefaultFaceIcon());

        int dir = tile.getDirection();
        trimWidth = framed.getTrimWidth();
        trimDepth = framed.getTrimDepth();

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        RenderHelper rh = RenderHelper.instances.get();
        rh.setColorAndBrightness(world, block, x, y, z);
        rh.state.setRotateTransform(RenderHelper.ZNEG, dir);
        rh.state.setUVRotation(RenderHelper.YPOS, RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][dir]);

        int pass = ForgeHooksClient.getWorldRenderPass();
        if (pass == 0) {
            panelRenderer.setTrimIcon(trimIcon);
            panelRenderer.setPanelIcon(sideIcon);
            for (int i = 0; i < 6; i++) {
                if (i != RenderHelper.ZNEG) panelRenderer.renderFacePanel(i, world, framed, x, y, z, 0, 0, 0, 1, 1, 1);
                panelRenderer.renderFaceTrim(i, world, framed, x, y, z, 0, 0, 0, 1, 1, 1);
            }
            panelRenderer.setTrimDepth(trimDepth);
            panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, world, framed, x, y, z, 0, 0, 0, 1, 1, 1);

            rh.state.flipTexture = true;

            // Top slot (slot 0)
            rh.setRenderBounds(trimWidth, trimWidth, trimDepth, lessThanHalf, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, frontIcon);

            // Bottom-left slot (slot 1)
            rh.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, frontIcon);

            // Bottom-right slot (slot 2)
            rh.setRenderBounds(moreThanHalf, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, frontIcon);

            // Slot dividers
            // Horizontal divider between top slot and bottom half (left side)
            rh.setRenderBounds(trimWidth, lessThanHalf, trimDepth, lessThanHalf, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimIcon);
            // Horizontal divider between top slot and bottom half (right side)
            rh.setRenderBounds(moreThanHalf, lessThanHalf, trimDepth, 1 - trimWidth, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimIcon);
            // Vertical divider between top-left and top-right
            rh.setRenderBounds(lessThanHalf, trimWidth, trimDepth, moreThanHalf, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimIcon);
            // Center cross
            rh.setRenderBounds(lessThanHalf, lessThanHalf, trimDepth, moreThanHalf, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimIcon);

            rh.state.flipTexture = false;
        } else if (pass == 1) {
            IIcon trimShadow = framed.getTrimShadowOverlay(false);
            IIcon handle = framed.getHandleOverlay();
            IIcon faceShadow = framed.getFaceShadowOverlay();
            panelRenderer.setTrimIcon(trimShadow);
            panelRenderer.renderFaceTrim(RenderHelper.ZNEG, world, framed, x, y, z, 0, 0, 0, 1, 1, 1);

            // Per-slot overlays: handle + face shadow
            // Top slot (slot 0)
            rh.setRenderBounds(trimWidth, trimWidth, trimDepth, lessThanHalf, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, handle);
            if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, faceShadow);

            // Bottom-left slot (slot 1)
            rh.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, handle);
            if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, faceShadow);

            // Bottom-right slot (slot 2)
            rh.setRenderBounds(moreThanHalf, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, handle);
            if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, faceShadow);

            // Slot divider shadows
            // Horizontal divider (left)
            rh.setRenderBounds(trimWidth, lessThanHalf, trimDepth, lessThanHalf, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimShadow);
            // Horizontal divider (right)
            rh.setRenderBounds(moreThanHalf, lessThanHalf, trimDepth, 1 - trimWidth, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimShadow);
            // Vertical divider
            rh.setRenderBounds(lessThanHalf, trimWidth, trimDepth, moreThanHalf, lessThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimShadow);
            // Center cross
            rh.setRenderBounds(lessThanHalf, lessThanHalf, trimDepth, moreThanHalf, moreThanHalf, 1);
            rh.renderFace(RenderHelper.ZNEG, world, framed, x, y, z, trimShadow);
        }

        rh.state.clearRotateTransform();
        rh.state.clearUVRotation(RenderHelper.YPOS);
    }

    private IIcon resolveIcon(ItemStack stack, IIcon fallback) {
        if (stack == null || stack.getItem() == null) return fallback;
        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return fallback;
        IIcon icon = b.getIcon(4, stack.getItemDamage());
        return icon != null ? icon : fallback;
    }

    @Override
    public int getRenderId() {
        return FramedCompactDrawers.proxy.framedCompactDrawerRenderID;
    }
}
