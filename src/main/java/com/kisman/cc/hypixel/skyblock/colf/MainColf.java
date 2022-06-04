package com.kisman.cc.hypixel.skyblock.colf;

import com.kisman.cc.hypixel.skyblock.colf.event.EventRegistry;
import com.kisman.cc.hypixel.skyblock.colf.network.WSClientWrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.*;

public class MainColf {
    public static final Logger LOGGER = LogManager.getLogger("[ColfSky]");

    public static WSClientWrapper wrapper;

    public static final String[] webSocketURIPrefix = new String [] {
            "wss://sky.coflnet.com/modsocket",
            "wss://sky-mod.coflnet.com/modsocket",
            "ws://sky.coflnet.com/modsocket",
            "ws://sky-mod.coflnet.com/modsocket",
    };

    public static String commandUri = "https://sky-commands.coflnet.com/api/mod/commands";

    public void init(FMLInitializationEvent event) {
        LOGGER.info("Started!");

        wrapper = new WSClientWrapper(webSocketURIPrefix);

        MinecraftForge.EVENT_BUS.register(new EventRegistry());
    }
}
