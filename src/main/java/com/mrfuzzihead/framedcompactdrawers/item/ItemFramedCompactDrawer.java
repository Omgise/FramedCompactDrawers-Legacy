package com.mrfuzzihead.framedcompactdrawers.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomDrawers;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;

public class ItemFramedCompactDrawer extends ItemCustomDrawers {

    public ItemFramedCompactDrawer(Block block) {
        super(block);
    }

    /**
     * Returns the base stack capacity used when placing the drawer and rendering its tooltip.
     *
     * <p>
     * Storage Drawers 1.7.10 resolves the compacting-drawer config section in
     * ItemDrawers.getCapacityForBlock() with the key "compDrawers", but the section is registered under
     * the lowercase name "compdrawers", so the lookup always returns 0. A tile placed with capacity 0 can
     * never accept items: right-clicking still populates the tier prototypes (so the items render on the
     * drawer's face), but every insert adds 0 items. Storage Drawers works around this for its own
     * compacting drawer in ItemCompDrawers.placeBlockAt(); override the lookup here so both the placed
     * tile's capacity and the item tooltip use the correctly-cased key.
     */
    @Override
    protected int getCapacityForBlock(Block block) {
        if (block instanceof BlockFramedCompactDrawer) return StorageDrawers.config.getBlockBaseStorage("compdrawers");

        return super.getCapacityForBlock(block);
    }

    /**
     * Adds sealed-drawer item-quantity information to the tooltip.
     *
     * <p>
     * Compacting drawers store their item count in a shared pool rather than per-slot "Count" tags. The
     * base {@code ItemDrawers.addDrawersInformation} reads {@code slot.getInteger("Count")}, which returns 0
     * for every slot because {@code CompDrawerData.writeToNBT} writes only the item prototype (stackSize 0).
     * The per-slot quantities must be computed from the top-level "Count" (pooled) and "Conv&lt;n&gt;"
     * (conversion rate per slot) tags, mirroring {@code ItemCompDrawers.addDrawersInformation}.
     */
    @Override
    protected void addDrawersInformation(NBTTagCompound tag, List list) {
        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        int totalCount = tag.getInteger("Count");

        list.add(
            EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.compDrawerList"));

        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            ItemStack stack = this.getItemStackFromDrawer(slot);
            String slotCounter = EnumChatFormatting.YELLOW + " #" + (i + 1) + ": ";
            if (stack != null && tag.hasKey("Conv" + i)) {
                int itemCount = totalCount / tag.getByte("Conv" + i);
                list.add(
                    slotCounter + this.getGoodDisplayName(stack)
                        + " "
                        + this.getItemCountDisplay(stack.getMaxStackSize(), itemCount));
            } else {
                list.add(
                    slotCounter + EnumChatFormatting.DARK_GRAY
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerEmpty"));
            }
        }
    }
}
