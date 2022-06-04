package com.kisman.cc.util;

import com.kisman.cc.event.events.PacketEvent;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class TickRateUtil {
    public static TickRateUtil INSTANCE;

    static {
        INSTANCE = new TickRateUtil();
    }

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate;

    @EventHandler
    public final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketTimeUpdate) INSTANCE.onTimeUpdate();
    });

    public void reset() {
        this.nextIndex = 0;
        this.timeLastTimeUpdate = -1L;
        Arrays.fill(this.tickRates, 0.0F);
    }

    public float getTickRate() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : this.tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F);
    }

    public float getMinTickRate() {
        float minTick = 20.0F;
        for (float tickRate : this.tickRates) if (tickRate > 0.0F && tickRate < minTick) minTick = tickRate;
        return MathHelper.clamp(minTick, 0.0F, 20.0F);
    }

    public float getLatestTickRate() {
        try {
            return MathHelper.clamp(tickRates[tickRates.length - 1], 0.0F, 20.0F);
        } catch (Exception e) {
            e.printStackTrace();
            return 20.0F;
        }
    }

    public void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0F;
            this.tickRates[(this.nextIndex % this.tickRates.length)] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
            this.nextIndex += 1;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }
}