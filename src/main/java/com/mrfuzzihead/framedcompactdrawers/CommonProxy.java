package com.mrfuzzihead.framedcompactdrawers;

import com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks;
import com.mrfuzzihead.framedcompactdrawers.registry.ModItems;
import com.mrfuzzihead.framedcompactdrawers.registry.ModRecipes;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public int framedCompactDrawerRenderID = -1;
    public int framedControllerRenderID = -1;
    public int framedSlaveRenderID = -1;

    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.register();
        ModItems.register();
    }

    public void init(FMLInitializationEvent event) {
        ModRecipes.init();
        registerRenderers();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void registerRenderers() {}
}
