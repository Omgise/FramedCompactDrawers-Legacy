package com.mrfuzzihead.framedcompactdrawers;

import com.mrfuzzihead.framedcompactdrawers.client.render.FramedCompactDrawerRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedControllerRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedSlaveRenderer;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers() {
        framedCompactDrawerRenderID = RenderingRegistry.getNextAvailableRenderId();
        framedControllerRenderID = RenderingRegistry.getNextAvailableRenderId();
        framedSlaveRenderID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(framedCompactDrawerRenderID, new FramedCompactDrawerRenderer());
        RenderingRegistry.registerBlockHandler(framedControllerRenderID, new FramedControllerRenderer());
        RenderingRegistry.registerBlockHandler(framedSlaveRenderID, new FramedSlaveRenderer());
    }
}
