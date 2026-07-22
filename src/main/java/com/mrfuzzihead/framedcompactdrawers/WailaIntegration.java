package com.mrfuzzihead.framedcompactdrawers;

import java.lang.reflect.Method;

import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedCompactDrawer;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedController;
import com.mrfuzzihead.framedcompactdrawers.block.BlockFramedSlave;
import com.mrfuzzihead.framedcompactdrawers.client.render.WailaFramedDrawerProvider;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaIntegration {

    public static final String WAILA_MODID = "Waila";
    private static final String REGISTRATION_METHOD = "register";

    public static void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded(WAILA_MODID)) {
            FMLInterModComms.sendMessage(
                WAILA_MODID,
                REGISTRATION_METHOD,
                WailaIntegration.class.getName() + "." + REGISTRATION_METHOD);
        }
    }

    public static void register(IWailaRegistrar registrar) {
        try {
            Class<?> compactDrawerProviderClass = Class.forName(WailaFramedDrawerProvider.class.getName());
            Object providerInstance = compactDrawerProviderClass.getDeclaredConstructor()
                .newInstance();

            Class<?> blockCompactDrawerClass = Class.forName(BlockFramedCompactDrawer.class.getName());
            Class<?> blockControllerClass = Class.forName(BlockFramedController.class.getName());
            Class<?> blockSlaveClass = Class.forName(BlockFramedSlave.class.getName());

            Method registerStackProvider = IWailaRegistrar.class
                .getMethod("registerStackProvider", Class.forName(IWailaDataProvider.class.getName()), Class.class);
            Method registerBodyProvider = IWailaRegistrar.class
                .getMethod("registerBodyProvider", Class.forName(IWailaDataProvider.class.getName()), Class.class);
            Method addConfig = IWailaRegistrar.class.getMethod("addConfig", String.class, String.class);

            registerStackProvider.invoke(registrar, providerInstance, blockCompactDrawerClass);
            registerBodyProvider.invoke(registrar, providerInstance, blockControllerClass);
            registerBodyProvider.invoke(registrar, providerInstance, blockSlaveClass);
            addConfig.invoke(registrar, FramedCompactDrawers.MODNAME, "display.content", "Show drawer contents");
            addConfig.invoke(registrar, FramedCompactDrawers.MODNAME, "display.stacklimit", "Show stack limit");
        } catch (Exception e) {
            FramedCompactDrawers.LOG.error("Failed to register WAILA providers", e);
        }
    }
}
