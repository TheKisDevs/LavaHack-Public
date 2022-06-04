package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.*;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.util.Render2DUtil;
import net.minecraft.util.text.TextFormatting;

public class ComboBox extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;

    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;
    private boolean hoveredExtended;
    private ValueChangeListener<Integer> listener;
    private String[] values;
    private int selectedIndex;

    private boolean opened;
    private int mouseY;

    public ComboBox(IRenderer renderer, int preferredWidth, int preferredHeight, String[] values, int selectedIndex) {
        super(renderer);

        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        this.values = values;
        this.selectedIndex = selectedIndex;

        setWidth(preferredWidth);
        updateHeight();
    }

    public ComboBox(IRenderer renderer, String[] values, int selectedIndex) {
        this(renderer, PREFERRED_WIDTH, PREFERRED_HEIGHT, values, selectedIndex);
    }

    private void updateHeight() {
        if (opened) setHeight(preferredHeight * values.length + 4);
        else setHeight(preferredHeight);
    }

    @Override
    public void render() {
        updateHeight();

        renderer.drawRect(x, y, getWidth(), getHeight(), Window.TERTIARY_FOREGROUND);

        if (hovered) renderer.drawRect(x, y, getWidth(), preferredHeight, Window.SECONDARY_FOREGROUND);
        else if (hoveredExtended) {
            int offset = preferredHeight + 4;

            for (int i = 0; i < values.length; i++) {
                if (i == selectedIndex) continue;

                int height = preferredHeight;

                if ((selectedIndex == 0 ? i == 1 : i == 0) || (selectedIndex == values.length - 1 ? i == values.length - 2 : i == values.length - 1)) height++;

                if (mouseY >= getY() + offset && mouseY <= getY() + offset + preferredHeight) {
                    renderer.drawRect(x, y + offset, getWidth(), preferredHeight, Window.SECONDARY_FOREGROUND);
                    break;
                }
                offset += height;
            }
        }
        renderer.drawRect(x + getWidth() - preferredHeight, y, preferredHeight, getHeight(), (hovered || opened) ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);

        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered && !opened) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect((x + getWidth() - preferredHeight) / 2, y / 2, (x + getWidth()) / 2, (y + getHeight()) / 2, (hovered || opened) ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        String text = selectedIndex != -1 ?  values[selectedIndex] : TextFormatting.RED + "ERROR";

        renderer.drawString(x + 4, y + preferredHeight / 2 - renderer.getStringHeight(text) / 2, text, Window.FOREGROUND);

        if (opened) {
            int offset = preferredHeight + 4;

            for (int i = 0; i < values.length; i++) {
                if (i == selectedIndex) continue;

                int height = preferredHeight;

                if ((selectedIndex == 0 ? i == 1 : i == 0) || (selectedIndex == values.length - 1 ? i == values.length - 2 : i == values.length - 1)) height++;

                renderer.drawString(x + 4, y + offset + (PREFERRED_HEIGHT / 2 - renderer.getStringHeight(values[i]) / 2), values[i], Window.FOREGROUND);
                offset += height;
            }
        }
    }

    @Override
    public void postRender() {}

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + preferredHeight;
        hoveredExtended = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();

        mouseX = x;
        mouseY = y;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        if (button != 0) return false;

        if (hovered) {
            setOpened(!opened);
            updateHeight();
            return true;
        }
        if (hoveredExtended && opened) {
            int offset = this.y + preferredHeight + 4;

            for (int i = 0; i < values.length; i++) {
                if (i == selectedIndex) continue;

                if (y >= offset && y <= offset + preferredHeight) {
                    setSelectedChecked(i);
                    setOpened(false);
                    break;
                }
                offset += preferredHeight;
            }
            updateHovered(x, y, offscreen);
            return true;
        }
        return false;
    }

    private void setSelectedChecked(int i) {
        boolean change = true;

        if (listener != null) change = listener.onValueChange(i);
        if (change) selectedIndex = i;
    }

    public int getSelectedIndex() {return selectedIndex;}
    public void setSelectedIndex(int selectedIndex) {this.selectedIndex = selectedIndex;}
    public void setListener(ValueChangeListener<Integer> listener) {this.listener = listener;}

    public void setOpened(boolean opened) {
        this.opened = opened;
        updateHeight();
    }
}