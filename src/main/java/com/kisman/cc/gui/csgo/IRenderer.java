package com.kisman.cc.gui.csgo;

import java.awt.*;

public interface IRenderer {
    void drawCheckMark(float x, float y, int width, int color);
    void drawRect(double x, double y, double w, double h, Color c);
    void drawOutline(double x, double y, double w, double h, float lineWidth, Color c);
    void setColor(Color c);
    void drawString(int x, int y, String text, Color color);
    int getStringWidth(String str);
    int getStringHeight(String str);
    void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color);
    void initMask();
    void useMask();
    void disableMask();
    int astolfoColor();
    Color astolfoColorToObj();
}
