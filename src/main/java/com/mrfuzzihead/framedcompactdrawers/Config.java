package com.mrfuzzihead.framedcompactdrawers;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {

    public static String greeting = "Hello World";

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
