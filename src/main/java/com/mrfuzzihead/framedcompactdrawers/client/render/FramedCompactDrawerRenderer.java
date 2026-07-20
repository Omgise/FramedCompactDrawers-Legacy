package com.mrfuzzihead.framedcompactdrawers.client.render;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;
import com.mrfuzzihead.framedcompactdrawers.FramedCompactDrawers;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

@SideOnly(Side.CLIENT)
public class FramedCompactDrawerRenderer extends DrawersRenderer {

    private final PanelBoxRenderer panelRenderer = new PanelBoxRenderer();
    private double trimWidth;
    private double trimDepth;

    @Override
    protected void renderBaseBlock(IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block,
        RenderBlocks renderer) {
        BlockDrawersCustom custom = (BlockDrawersCustom) block;

        ItemStack matSide = tile.getMaterialSide();
        if (matSide == null) matSide = new ItemStack(block);
        ItemStack matFront = tile.getMaterialFront();
        if (matFront == null) matFront = matSide;
        ItemStack matTrim = tile.getMaterialTrim();
        if (matTrim == null) matTrim = matSide;

        IIcon sideIcon = resolveIcon(matSide, custom.getDefaultFaceIcon());
        IIcon trimIcon = resolveIcon(matTrim, custom.getDefaultTrimIcon());
        IIcon frontIcon = resolveIcon(matFront, custom.getDefaultFaceIcon());

        int dir = tile.getDirection();
        trimWidth = custom.getTrimWidth();
        trimDepth = custom.getTrimDepth();

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
                if (i != RenderHelper.ZNEG) panelRenderer.renderFacePanel(i, world, custom, x, y, z, 0, 0, 0, 1, 1, 1);
                panelRenderer.renderFaceTrim(i, world, custom, x, y, z, 0, 0, 0, 1, 1, 1);
            }
            panelRenderer.setTrimDepth(trimDepth);
            panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, world, custom, x, y, z, 0, 0, 0, 1, 1, 1);
            rh.state.flipTexture = true;
            rh.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            rh.renderFace(RenderHelper.ZNEG, world, custom, x, y, z, frontIcon);
            rh.state.flipTexture = false;
        } else if (pass == 1) {
            IIcon trimShadow = custom.getTrimShadowOverlay(false);
            IIcon handle = custom.getHandleOverlay();
            IIcon faceShadow = custom.getFaceShadowOverlay();
            panelRenderer.setTrimIcon(trimShadow);
            panelRenderer.renderFaceTrim(RenderHelper.ZNEG, world, custom, x, y, z, 0, 0, 0, 1, 1, 1);
            rh.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
            rh.renderFace(RenderHelper.ZNEG, world, custom, x, y, z, handle);
            if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, world, custom, x, y, z, faceShadow);
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
