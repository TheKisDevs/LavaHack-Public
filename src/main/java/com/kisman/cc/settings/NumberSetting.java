package com.kisman.cc.settings;

import java.util.function.Supplier;
import com.kisman.cc.settings.Setting2;

public class NumberSetting
        extends Setting2 {
    private double current;
    private double minimum;
    private double maximum;
    private double increment;
    private String desc;

    public NumberSetting(String name, float current, float minimum, float maximum, float increment) {
        this.name = name;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisible(() -> true);
    }

    public NumberSetting(String name, double current, double minimum, double maximum, double increment) {
        this.name = name;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisible(() -> true);
    }

    public NumberSetting(String name, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        this.name = name;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisible(visible);
    }

    public NumberSetting(String name, String desc, float current, float minimum, float maximum, float increment, Supplier<Boolean> visible) {
        this.name = name;
        this.desc = desc;
        this.minimum = minimum;
        this.current = current;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisible(visible);
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getMinimum() {
        return (float)this.minimum;
    }

    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }

    public float getMaximum() {
        return (float)this.maximum;
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    public float getCurrentValue() {
        return (float)this.current;
    }

    public int getCurrentValueInt() {
        return (int)this.current;
    }

    public void setCurrentValue(float current) {
        this.current = current;
    }

    public float getIncrement() {
        return (float)this.increment;
    }

    public void setIncrement(float increment) {
        this.increment = increment;
    }
}


