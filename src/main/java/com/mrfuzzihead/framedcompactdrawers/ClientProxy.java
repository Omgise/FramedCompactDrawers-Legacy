package com.mrfuzzihead.framedcompactdrawers;

import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedController;
import com.mrfuzzihead.framedcompactdrawers.client.render.RenderFramedSlave;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static int framedCompactDrawerRenderId;
    public static int framedDrawerControllerRenderId;
    public static int framedSlaveRenderId;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        framedCompactDrawerRenderId = RenderingRegistry.getNextAvailableRenderId();
        framedDrawerControllerRenderId = RenderingRegistry.getNextAvailableRenderId();
        framedSlaveRenderId = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(framedCompactDrawerRenderId, new RenderFramedCompactDrawer());
        RenderingRegistry.registerBlockHandler(framedDrawerControllerRenderId, new RenderFramedController());
        RenderingRegistry.registerBlockHandler(framedSlaveRenderId, new RenderFramedSlave());
    }
}
