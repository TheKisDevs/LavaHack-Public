package com.kisman.cc.gui.halq.util;

public enum LayerMap {
    ZERO(0, 0),
    FIRST(1, 5),
    SECOND(2, 10);

    public int index, modifier;
    LayerMap(int index, int modifier) {
        this.index = index;
        this.modifier = modifier;
    }

    public static LayerMap getLayer(int index) {
        for(LayerMap layer : LayerMap.values()) if(layer.index == index) return layer;
        return ZERO;
    }
}
