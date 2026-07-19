package com.mrfuzzihead.framedcompactdrawers;

import com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks;
import com.mrfuzzihead.framedcompactdrawers.registry.ModItems;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public int framedCompactDrawerRenderID = -1;
    public int framedControllerRenderID = -1;
    public int framedSlaveRenderID = -1;

    public void preInit(FMLPreInitializationEvent event) {
        // Registration is a no-op stub during Phase 1; actual block/item instantiation
        // happens in Phase 7 once Phase 2-4 classes are in place
        ModBlocks.register();
        ModItems.register();

        FramedCompactDrawers.LOG.info("I am " + FramedCompactDrawers.MODNAME + " at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {
        registerRenderers();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public void registerRenderers() {}
}
