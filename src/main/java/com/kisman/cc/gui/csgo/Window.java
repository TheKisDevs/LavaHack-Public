package com.kisman.cc.gui.csgo;

import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.csgo.components.Pane;
import com.kisman.cc.util.Render2DUtil;

import java.awt.*;

public class Window {
    public static final Color SECONDARY_FOREGROUND = new Color(80, 80, 80, 240);
    public static final Color TERTIARY_FOREGROUND = new Color(20, 20, 20, 150);
    public static final Color SECONDARY_OUTLINE = new Color(10, 10, 10, 255);
    public static final Color BACKGROUND = new Color(20, 20, 20, 220);
    public static final Color FOREGROUND = Color.white;

    private String title;
    private int x;
    private int y;
    private int width;
    private int height;

    private int headerHeight;

    private boolean beingDragged;
    private int dragX;
    private int dragY;

    private Pane contentPane;

    public Window(String title, int x, int y, int width, int height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(IRenderer renderer, int mouseX, int mouseY) {
        int fontHeight = renderer.getStringHeight(title);
        int headerFontOffset = fontHeight / 4;

        headerHeight = headerFontOffset * 2 + fontHeight;

        renderer.drawRect(x, y, width, height, BACKGROUND);
        renderer.drawRect(x, y, width, headerHeight, SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + width) / 2, (y + headerHeight) / 2, SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        renderer.drawString(x + width / 2 - renderer.getStringWidth(title) / 2, y + headerFontOffset, title, Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : FOREGROUND);

        if (contentPane != null) {
            if (contentPane.isSizeChanged()) contentPane.setSizeChanged(false);

            contentPane.setX(x);
            contentPane.setY(y + headerHeight);
            contentPane.setWidth(width);
            contentPane.setHeight(height - headerHeight);

            contentPane.render();
        }
    }

    public void postRender(IRenderer renderer) {
        if(contentPane != null) contentPane.postRender();
    }

    public void mousePressed(int button, int x, int y) {
        if (this.contentPane != null) contentPane.mousePressed(button, x, y, false);

        if (button == 0) {
            if (x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.headerHeight) {
                beingDragged = true;

                dragX = this.x - x;
                dragY = this.y - y;
            }
        }
    }

    private void drag(int mouseX, int mouseY) {
        if (beingDragged) {
            this.x = mouseX + dragX;
            this.y = mouseY + dragY;
        }
    }

    public void mouseReleased(int button, int x, int y) {
        if (this.contentPane != null) contentPane.mouseReleased(button, x, y, false);

        if (button == 0) {
            beingDragged = false;
        }
    }

    public void mouseMoved(int x, int y) {
        if (this.contentPane != null) contentPane.mouseMove(x, y, false);

        drag(x, y);
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public void keyPressed(int key, char c) {
        if (this.contentPane != null) contentPane.keyPressed(key, c);
    }

    public void mouseWheel(int change) {
        if (this.contentPane != null) contentPane.mouseWheel(change);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}