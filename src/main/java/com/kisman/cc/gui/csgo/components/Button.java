package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.util.Render2DUtil;

public class Button extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    public static final int PREFERRED_HEIGHT = 22;

    public String title;
    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;
    private ActionEventListener listener, listener2;

    public Button(IRenderer renderer, String title, int preferredWidth, int preferredHeight) {
        super(renderer);

        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        setTitle(title);
    }

    public Button(IRenderer renderer, String title) {
        this(renderer, title, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        renderer.drawRect(x, y, getWidth(), getHeight(), hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + getWidth()) / 2, (y + getHeight()) / 2, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        renderer.drawString(x + getWidth() / 2 - renderer.getStringWidth(title) / 2, y + getHeight() / 2 - renderer.getStringHeight(title) / 2, title, Window.FOREGROUND);
    }

    @Override public void postRender() {}

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if (hovered && listener != null) {
                listener.onActionEvent();

                return true;
            }
        } else if(button == 1) {
            updateHovered(x, y, offscreen);

            if (hovered && listener2 != null) {
                listener2.onActionEvent();

                return true;
            }
        }

        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

        setWidth(Math.max(renderer.getStringWidth(title), preferredWidth));
        setHeight(Math.max(renderer.getStringHeight(title) * 5 / 4, preferredHeight));
    }

    public ActionEventListener getOnClickListener() {
        return listener;
    }

    public void setOnClickListener(ActionEventListener listener) {
        this.listener = listener;
    }

    public ActionEventListener getOnClickListener2() {
        return listener2;
    }

    public void setOnClickListener2(ActionEventListener listener) {
        this.listener2 = listener;
    }
}