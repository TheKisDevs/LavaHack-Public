package com.kisman.cc.gui.csgo.layout;

import com.kisman.cc.gui.csgo.AbstractComponent;

import java.util.Map;

public class Layout {
    private Map<AbstractComponent, int[]> componentLocations;
    private int maxHeight;
    private int maxWidth;

    Layout(Map<AbstractComponent, int[]> componentLocations, int maxHeight, int maxWidth) {
        this.componentLocations = componentLocations;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
    }

    public Map<AbstractComponent, int[]> getComponentLocations() {
        return componentLocations;
    }

    public void setComponentLocations(Map<AbstractComponent, int[]> componentLocations) {
        this.componentLocations = componentLocations;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }
}