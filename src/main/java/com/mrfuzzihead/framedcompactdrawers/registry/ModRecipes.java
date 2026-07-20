package com.mrfuzzihead.framedcompactdrawers.registry;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModRecipes {

    public static void init() {
        // Framed Compacting Drawer — wraps SD compDrawers in stickWood
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks.blockFramedCompactDrawer),
                "///",
                "/X/",
                "///",
                '/',
                "stickWood",
                'X',
                new ItemStack(ModBlocks.compDrawers)));

        // Framed Drawer Controller — wraps SD controller in stickWood
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks.blockFramedController),
                "///",
                "/X/",
                "///",
                '/',
                "stickWood",
                'X',
                new ItemStack(ModBlocks.controller)));

        // Framed Slave — wraps SD controllerSlave in stickWood
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks.blockFramedSlave),
                "///",
                "/X/",
                "///",
                '/',
                "stickWood",
                'X',
                new ItemStack(ModBlocks.controllerSlave)));
    }
}
