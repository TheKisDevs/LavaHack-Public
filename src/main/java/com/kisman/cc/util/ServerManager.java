package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

public class ServerManager {
    private Minecraft mc = Minecraft.getMinecraft();

    private float[] tpsCount;
    private DecimalFormat format;
    private TimerUtils timer;
    private float tps;
    private long lastUpdate;
    private String serverBrand;

    public ServerManager() {
        tpsCount = new float[10];
        format = new DecimalFormat("##.00##");
        timer = new TimerUtils();
        tps = 20;
        lastUpdate = -1L;
        serverBrand = "";

        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        final long currentTime = System.currentTimeMillis();

        if(lastUpdate == -1L) {
            lastUpdate = currentTime;
            return;
        }

        final long timeDiff = currentTime - lastUpdate;
        float tickTime = timeDiff / 20;

        if(tickTime == 0) {
            tickTime = 50;
        }

        float tps = 1000 / tickTime;
        if(tps > 20) {
            tps = 20;
        }

        System.arraycopy(tpsCount, 0, tpsCount, 1, tpsCount.length - 1);
        tpsCount[0] = tps;

        double total = 0;

        for(float f : tpsCount) {
            total += f;
        }

        if((total /= tpsCount.length) > 20) {
            total = 20f;
        }

        tps = Float.parseFloat(format.format(total));
        lastUpdate = currentTime;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        timer.reset();
    });

    public void reset() {
        Arrays.fill(tpsCount, 20);
        tps = 20;
    }

    public float getTpsFactor() {
        return 20 / tps;
    }

    public float getTps() {
        return tps;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public void setServerBrand(final String brand) {
        this.serverBrand = brand;
    }

    public int getPing() {
        if (mc.player == null && mc.world == null) {
            return 0;
        }
        try {
            return Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime();
        }
        catch (Exception e) {
            return 0;
        }
    }
}
