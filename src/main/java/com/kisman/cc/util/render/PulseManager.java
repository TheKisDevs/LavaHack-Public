package com.kisman.cc.util.render;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.manager.Managers;
import net.minecraft.util.math.*;

public class PulseManager {
    public static int min;
    public static int max;
    private int current;
    private boolean up;
    
    public PulseManager() {
        this.current = PulseManager.min;
        this.up = true;
    }
    
    public void update() {
        PulseManager.min = Config.instance.pulseMin.getValInt();
        PulseManager.max = Config.instance.pulseMax.getValInt();
        this.current = this.step(this.current);
    }
    
    public int getCurrentPulse() {
        return this.current;
    }
    
    public int getDifference(final int value) {
        int ret = this.current;
        if (this.up) {
            ret += value % 210;
            if (ret > PulseManager.max) {
                final int i = Math.abs(ret - PulseManager.max);
                ret = PulseManager.max - i;
            }
        } else {
            ret -= value % 210;
            if (ret < PulseManager.min) {
                final int i = Math.abs(ret - PulseManager.min);
                ret = PulseManager.min + i;
            }
        }
        return MathHelper.clamp(ret, PulseManager.min, PulseManager.max);
    }
    
    public int clamp(int value) {
        if (this.up) {
            if (value >= PulseManager.max) {
                value = PulseManager.max;
                this.up = false;
            }
        } else if (value <= PulseManager.min) {
            value = PulseManager.min;
            this.up = true;
        }
        return value;
    }
    
    public int step(final int from) {
        int ret = (int)(this.up ? (from + PulseManager.max / Config.instance.pulseSpeed.getValDouble() * Managers.instance.fpsManager.getFrametime()) : (from - PulseManager.min / Config.instance.pulseSpeed.getValDouble() * Managers.instance.fpsManager.getFrametime()));
        ret = this.clamp(ret);
        return ret;
    }
    
    static {
        PulseManager.min = 100;
        PulseManager.max = 255;
    }
}