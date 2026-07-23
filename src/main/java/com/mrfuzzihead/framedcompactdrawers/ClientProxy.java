package com.mrfuzzihead.framedcompactdrawers;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import com.mrfuzzihead.framedcompactdrawers.client.render.FramedCompactDrawerItemRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedCompactDrawerRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedControllerItemRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedControllerRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedSlaveItemRenderer;
import com.mrfuzzihead.framedcompactdrawers.client.render.FramedSlaveRenderer;
import com.mrfuzzihead.framedcompactdrawers.registry.ModBlocks;

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

        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(ModBlocks.blockFramedCompactDrawer),
            new FramedCompactDrawerItemRenderer());
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(ModBlocks.blockFramedController),
            new FramedControllerItemRenderer());
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockFramedSlave), new FramedSlaveItemRenderer());
    }
}
