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
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FramedControllerItemRenderer implements IItemRenderer {

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
        if (!(block instanceof BlockFramedController)) return;
        BlockFramedController ctrl = (BlockFramedController) block;

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPushMatrix();
        if (type == ItemRenderType.INVENTORY) GL11.glRotatef(90, 0, 1, 0);
        if (type == ItemRenderType.ENTITY) GL11.glRotatef(180, 0, 1, 0);
        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        renderWithMaterials(ctrl, item);

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glPopMatrix();
    }

    private void renderWithMaterials(BlockFramedController block, ItemStack item) {
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

        double trimWidth = 0.0625;
        double trimDepth = 0.0625;

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
        rh.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);
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
