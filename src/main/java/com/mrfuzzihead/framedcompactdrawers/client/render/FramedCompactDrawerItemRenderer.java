package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FramedCompactDrawerItemRenderer implements IItemRenderer {

    private static final double lessThanHalf = 0.4375;
    private static final double moreThanHalf = 0.5625;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Block block = Block.getBlockFromItem(item.getItem());
        if (!(block instanceof BlockFramedCompactDrawer)) return;
        BlockFramedCompactDrawer framed = (BlockFramedCompactDrawer) block;

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPushMatrix();
        if (type == ItemRenderType.INVENTORY) GL11.glRotatef(90, 0, 1, 0);
        if (type == ItemRenderType.ENTITY) GL11.glRotatef(180, 0, 1, 0);
        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        renderWithMaterials(framed, item);

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPopMatrix();
    }

    private void renderWithMaterials(BlockFramedCompactDrawer block, ItemStack item) {
        NBTTagCompound tag = item.stackTagCompound;
        ItemStack matSide = null, matTrim = null, matFront = null;
        if (tag != null) {
            if (tag.hasKey("MatS")) matSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
            if (tag.hasKey("MatT")) matTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
            if (tag.hasKey("MatF")) matFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));
        }
        if (matSide == null) matSide = matFront;
        if (matFront == null) matFront = matSide;
        if (matTrim == null) matTrim = matSide;

        IIcon sideIcon = resolveIcon(matSide, block.getDefaultFaceIcon());
        IIcon trimIcon = resolveIcon(matTrim, block.getDefaultTrimIcon());
        IIcon frontIcon = resolveIcon(matFront, block.getDefaultFaceIcon());

        double trimWidth = block.getTrimWidth();
        double trimDepth = block.getTrimDepth();

        PanelBoxRenderer panelRenderer = new PanelBoxRenderer();
        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setTrimIcon(trimIcon);
        panelRenderer.setPanelIcon(sideIcon);

        RenderHelper rh = RenderHelper.instances.get();
        rh.state.setRotateTransform(RenderHelper.ZNEG, RenderHelper.XNEG);
        rh.state.setUVRotation(
            RenderHelper.YPOS,
            RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][RenderHelper.XNEG]);

        for (int i = 0; i < 6; i++) {
            if (i != RenderHelper.ZNEG) panelRenderer.renderFacePanel(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
        }
        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);

        rh.state.flipTexture = true;

        // Top slot (slot 0)
        rh.setRenderBounds(trimWidth, trimWidth, trimDepth, lessThanHalf, lessThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);

        // Bottom-left slot (slot 1)
        rh.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);

        // Bottom-right slot (slot 2)
        rh.setRenderBounds(moreThanHalf, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);

        // Slot dividers
        rh.setRenderBounds(trimWidth, lessThanHalf, trimDepth, lessThanHalf, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(moreThanHalf, lessThanHalf, trimDepth, 1 - trimWidth, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(lessThanHalf, trimWidth, trimDepth, moreThanHalf, lessThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(lessThanHalf, lessThanHalf, trimDepth, moreThanHalf, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);

        rh.state.flipTexture = false;

        rh.state.clearRotateTransform();
        rh.state.clearUVRotation(RenderHelper.YPOS);
    }

    private static IIcon resolveIcon(ItemStack stack, IIcon fallback) {
        if (stack == null || stack.getItem() == null) return fallback;
        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return fallback;
        IIcon icon = b.getIcon(4, stack.getItemDamage());
        return icon != null ? icon : fallback;
    }
}
