package com.mrfuzzihead.framedcompactdrawers.client;

import com.mrfuzzihead.framedcompactdrawers.CommonProxy;
import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedController;
import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedSlave;

import cpw.mods.fml.client.registry.RenderRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static int framedCompactDrawerRenderId = -1;
    public static int framedDrawerControllerRenderId = -1;
    public static int framedSlaveRenderId = -1;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Assign render IDs
        framedCompactDrawerRenderId = RenderingRegistry.getNextAvailableRenderId();
        framedDrawerControllerRenderId = RenderingRegistry.getNextAvailableRenderId();
        framedSlaveRenderId = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Register render handlers
        RenderRegistry.registerBlockHandler(new RenderFramedCompactDrawer());
        RenderRegistry.registerBlockHandler(new RenderFramedController());
        RenderRegistry.registerBlockHandler(new RenderFramedSlave());
    }
}
