package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.util.Render2DUtil;

public class LineButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private final String label;

    public LineButton(IRenderer renderer, String label) {
        super(renderer);
        setWidth(PREFERRED_WIDTH);
        setHeight(PREFERRED_HEIGHT);
        this.label = label;
    }

    @Override
    public void render() {
        renderer.drawString(x, y, label, Window.FOREGROUND);
        renderer.drawRect(x, y + getHeight() - 4, getWidth(), 4, Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, (y + getHeight() - 4) / 2, (x + getWidth()) / 2, (y + getHeight()) / 2, Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());
    }

    @Override public void postRender() {}
}
