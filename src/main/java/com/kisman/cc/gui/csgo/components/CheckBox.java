package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.util.Render2DUtil;

import java.awt.*;

public class CheckBox extends AbstractComponent {
    private static final int PREFERRED_HEIGHT = 22;

    private boolean selected;
    private String title;
    private int preferredHeight;
    private boolean hovered;
    private ValueChangeListener<Boolean> listener;

    public CheckBox(IRenderer renderer, String title, int preferredHeight) {
        super(renderer);

        this.preferredHeight = preferredHeight;

        setTitle(title);
    }

    public CheckBox(IRenderer renderer, String title) {
        this(renderer, title, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        renderer.drawRect(x, y, preferredHeight, preferredHeight, hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);

        if (selected) {
            Color color = hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND;

            renderer.drawRect(x, y, preferredHeight, preferredHeight,color);
        }

        renderer.drawOutline(x, y, preferredHeight, preferredHeight, 1.0f, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);
        if(Config.instance.guiGlow.getValBoolean() && selected) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + preferredHeight) / 2, (y + preferredHeight) / 2, hovered ? Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());
        if(Config.instance.guiBetterCheckBox.getValBoolean() && selected) renderer.drawCheckMark(x / 2 - 5, y/ 2 - 1, preferredHeight, Color.WHITE.getRGB());
        renderer.drawString(x + preferredHeight + preferredHeight / 4, y + getHeight() / 2 - renderer.getStringHeight(title) / 2, title, Window.FOREGROUND);
    }

    @Override public void postRender() { }

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

            if (hovered) {
                boolean newVal = !selected;
                boolean change = true;

                if (listener != null) change = listener.onValueChange(newVal);
                if (change) selected = newVal;

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

        setWidth(renderer.getStringWidth(title) + preferredHeight + preferredHeight / 4);
        setHeight(preferredHeight);
    }

    public void setListener(ValueChangeListener<Boolean> listener) {
        this.listener = listener;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
