package com.mrfuzzihead.framedcompactdrawers.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void register() {
        // Framed Compacting Drawer recipe: 3x3 border of sticks, center = compdrawers
        ItemStack framedCompactDrawer = new ItemStack(ModBlocks.framedCompactDrawer);
        GameRegistry.addShapedRecipe(framedCompactDrawer, "SSS", " S ", "SSS", 'S', OreDictionary.getOres("stickWood"));

        // Framed Drawer Controller recipe: 3x3 border of sticks, center = controller
        ItemStack framedController = new ItemStack(ModBlocks.framedDrawerController);
        GameRegistry.addShapedRecipe(framedController, "SSS", " S ", "SSS", 'S', OreDictionary.getOres("stickWood"));

        // Framed Slave recipe: 3x3 border of sticks, center = controllerslave
        ItemStack framedSlave = new ItemStack(ModBlocks.framedSlave);
        GameRegistry.addShapedRecipe(framedSlave, "SSS", " S ", "SSS", 'S', OreDictionary.getOres("stickWood"));
    }
}
