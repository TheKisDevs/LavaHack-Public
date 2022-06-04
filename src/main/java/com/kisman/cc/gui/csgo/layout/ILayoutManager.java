package com.kisman.cc.gui.csgo.layout;

import com.kisman.cc.gui.csgo.AbstractComponent;

import java.util.List;

public interface ILayoutManager {
    int[] getOptimalDiemension(List<AbstractComponent> components, int maxWidth);

    Layout buildLayout(List<AbstractComponent> components, int width, int height);
}