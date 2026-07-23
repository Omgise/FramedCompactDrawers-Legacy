package com.mrfuzzihead.framedcompactdrawers.mixins.late.storagedrawers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersItemRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(DrawersItemRenderer.class)
public class MixinDrawersItemRenderer {

    private static final double lessThanHalf = 0.4375;
    private static final double moreThanHalf = 0.5625;

    @Inject(method = "renderDrawer", at = @At("HEAD"), cancellable = true, remap = false)
    private void framedCompactDrawers$onRenderDrawer(BlockDrawers block, ItemStack item, RenderBlocks renderer,
        net.minecraftforge.client.IItemRenderer.ItemRenderType renderType, CallbackInfo ci) {
        if (!(block instanceof BlockFramedCompactDrawer)) return;
        renderFramedCompactDrawerItem((BlockFramedCompactDrawer) block, item);
        ci.cancel();
    }

    private void renderFramedCompactDrawerItem(BlockFramedCompactDrawer block, ItemStack item) {
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

        for (int i = 0; i < 6; i++) {
            if (i != RenderHelper.ZNEG) panelRenderer.renderFacePanel(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);
        }

        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, null, block, 0, 0, 0, 0, 0, 0, 1, 1, 1);

        RenderHelper rh = RenderHelper.instances.get();
        rh.setRenderBounds(trimWidth, trimWidth, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, sideIcon);

        rh.setRenderBounds(0, 0, trimDepth, trimWidth, 1, 1);
        rh.renderFace(RenderHelper.XNEG, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.XPOS, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.YPOS, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.YNEG, null, block, 0, 0, 0, sideIcon);

        rh.setRenderBounds(1 - trimWidth, 0, trimDepth, 1, 1, 1);
        rh.renderFace(RenderHelper.XNEG, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.XPOS, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.YPOS, null, block, 0, 0, 0, sideIcon);
        rh.renderFace(RenderHelper.YNEG, null, block, 0, 0, 0, sideIcon);

        // Front face panel with 3-slot cutout
        rh.setRenderBounds(trimWidth, trimWidth, trimDepth, lessThanHalf, 0.5, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);
        rh.setRenderBounds(moreThanHalf, trimWidth, trimDepth, 1 - trimWidth, 0.5, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);
        rh.setRenderBounds(trimWidth, 0.5, trimDepth, lessThanHalf, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);
        rh.setRenderBounds(moreThanHalf, 0.5, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, frontIcon);

        // Slot divider lines
        rh.setRenderBounds(trimWidth, 0.5, trimDepth, lessThanHalf, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(moreThanHalf, 0.5, trimDepth, 1 - trimWidth, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(lessThanHalf, trimWidth, trimDepth, moreThanHalf, 0.5, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);
        rh.setRenderBounds(lessThanHalf, lessThanHalf, trimDepth, moreThanHalf, moreThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, trimIcon);

        // Handle and shadow overlays
        IIcon handleOverlay = block.getHandleOverlay();
        IIcon faceShadow = block.getFaceShadowOverlay();

        rh.setRenderBounds(trimWidth, trimWidth, trimDepth, lessThanHalf, 0.5, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, handleOverlay);
        if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, faceShadow);

        rh.setRenderBounds(trimWidth, moreThanHalf, trimDepth, 1 - trimWidth, 1 - trimWidth, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, handleOverlay);
        if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, faceShadow);

        rh.setRenderBounds(moreThanHalf, trimWidth, trimDepth, 1 - trimWidth, lessThanHalf, 1);
        rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, handleOverlay);
        if (faceShadow != null) rh.renderFace(RenderHelper.ZNEG, null, block, 0, 0, 0, faceShadow);

        // Packing tape overlay — matches DrawersItemRenderer.renderDrawer() behavior
        if (item.hasTagCompound() && item.getTagCompound()
            .hasKey("tile")) {
            double depth = block.halfDepth ? .5 : 1;
            rh.setRenderBounds(1 - depth - .005, 0, 0, 1, 1, 1);
            rh.renderFace(RenderHelper.XNEG, null, block, block.getTapeIcon(), 1, 1, 1);
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
