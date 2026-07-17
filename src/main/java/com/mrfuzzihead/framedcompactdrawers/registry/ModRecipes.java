package com.mrfuzzihead.framedcompactdrawers.registry;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void register() {
        // Get all ore dictionary entries for "stickWood" (1.7.10 FML does not accept List<ItemStack>
        // directly in addShapedRecipe — must register one recipe per entry).
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");

        // Framed Compacting Drawer recipe: 3x3 border of sticks, center = compdrawers
        ItemStack framedCompactDrawer = new ItemStack(ModBlocks.framedCompactDrawer);
        for (ItemStack stick : sticks) {
            GameRegistry.addShapedRecipe(framedCompactDrawer, "SSS", " S ", "SSS", 'S', stick);
        }

        // Framed Drawer Controller recipe: 3x3 border of sticks, center = controller
        ItemStack framedController = new ItemStack(ModBlocks.framedDrawerController);
        for (ItemStack stick : sticks) {
            GameRegistry.addShapedRecipe(framedController, "SSS", " S ", "SSS", 'S', stick);
        }

        // Framed Slave recipe: 3x3 border of sticks, center = controllerslave
        ItemStack framedSlave = new ItemStack(ModBlocks.framedSlave);
        for (ItemStack stick : sticks) {
            GameRegistry.addShapedRecipe(framedSlave, "SSS", " S ", "SSS", 'S', stick);
        }
    }
}
