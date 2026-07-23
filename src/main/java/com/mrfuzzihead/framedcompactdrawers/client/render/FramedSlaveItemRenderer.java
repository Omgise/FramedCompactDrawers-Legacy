package com.mrfuzzihead.framedcompactdrawers.client.render;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FramedSlaveItemRenderer implements IItemRenderer {

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
        if (!(block instanceof BlockFramedSlave)) return;
        BlockFramedSlave slave = (BlockFramedSlave) block;

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPushMatrix();
        if (type == ItemRenderType.INVENTORY) GL11.glRotatef(90, 0, 1, 0);
        if (type == ItemRenderType.ENTITY) GL11.glRotatef(180, 0, 1, 0);
        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        renderWithMaterials(slave, item);

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPopMatrix();
    }

    private void renderWithMaterials(BlockFramedSlave block, ItemStack item) {
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
        IIcon topBottomIcon = resolveIcon(matFront, block.getDefaultTopBottomIcon());
        IIcon sideShadow = block.getOverlaySideShadow();

        double trimWidth = 0.0625;

        PanelBoxRenderer panelRenderer = new PanelBoxRenderer();
        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        // Pass 0: Render base panels and trims
        // Face 0 = bottom (Y-), Face 1 = top (Y+), Faces 2-5 = sides
        for (int i = 0; i < 6; i++) {
            panelRenderer.setTrimIcon(trimIcon);
            panelRenderer.setPanelIcon(i == 0 || i == 1 ? topBottomIcon : sideIcon);
            panelRenderer.renderFacePanel(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
        }

        // Pass 1: Render overlay shadow on side faces (requires alpha blending for transparency)
        if (sideShadow != null) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            panelRenderer.setTrimIcon(sideShadow);
            for (int i = 2; i < 6; i++) {
                panelRenderer.setPanelIcon(sideShadow);
                panelRenderer.renderFacePanel(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
                panelRenderer.renderFaceTrim(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
            }
            GL11.glDepthMask(true);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private static IIcon resolveIcon(ItemStack stack, IIcon fallback) {
        if (stack == null || stack.getItem() == null) return fallback;
        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return fallback;
        IIcon icon = b.getIcon(4, stack.getItemDamage());
        return icon != null ? icon : fallback;
    }
}
