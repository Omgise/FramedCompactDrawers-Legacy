package com.mrfuzzihead.framedcompactdrawers.mixins.late.storagedrawers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave;

@Mixin(TileEntityFramingTable.class)
public class MixinTileEntityFramingTable {

    @Inject(
        method = "isItemValidDrawer(Lnet/minecraft/item/ItemStack;)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    private static void framedCompactDrawers$acceptFramedBlocks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || stack.getItem() == null) return;

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof BlockFramedController || block instanceof BlockFramedSlave) {
            cir.setReturnValue(true);
        }
    }
}
