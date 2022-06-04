package com.kisman.cc.gui.vega.component.components.sub.patterns;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;

public class DefaultButton {
    public int offset = 0;
    public int x, y;
    public int width, height;
    public DefaultButton(int x, int y, int offset, int width, int height) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.width = height;
    }

    public void update(int mouseX, int mouseY) {

    }

    public void render() {
        Gui.drawRect(this.x - 3, this.y + 3 + offset, this.x + this.width + 3, this.y + this.height + 3 + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y + offset, this.x + this.width + 3, this.y + this.height + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y + 2 + offset, this.x + this.width + 2, this.y + this.height + 2 + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y + offset, this.x + this.width + 2, this.y + this.height + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y + 1 + offset, this.x + this.width + 1, this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + offset, this.x + this.width + 1, this.y + this.height + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y + offset, this.x + this.width, this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));
    }

    public void updatePos(int x, int y, int offset) {
        this.x = x;
        this.y = y;
        this.offset = offset;
    }
}
