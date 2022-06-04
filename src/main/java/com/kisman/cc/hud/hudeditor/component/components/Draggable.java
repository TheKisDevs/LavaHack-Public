package com.kisman.cc.hud.hudeditor.component.components;

import com.kisman.cc.hud.hudeditor.component.Component;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.Render2DUtil;

import java.awt.Color;

public class Draggable extends Component {
    private final HudModule mod;
    private boolean drag;
    private double dragX, dragY;

    public Draggable(HudModule mod) {
        this.mod = mod;
    }

    public void drawScreen(int mouseX, int mouseY) {
        if(!mod.isToggled()) return;

        if(drag) {
            mod.setX(mouseX - dragX);
            mod.setY(mouseY - dragY);
        }

        Render2DUtil.drawRect(mod.getX(), mod.getY(), mod.getX() + mod.getW(), mod.getY() + mod.getH(), new Color(10, 10, 10, 170).getRGB());
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(!mod.isToggled()) return;
        drag = isMouseOnButton(mouseX, mouseY);
        dragX = mouseX - mod.getX();
        dragY = mouseY - mod.getY();
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        drag = false;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > mod.getX() && x < mod.getX() + mod.getW() && y > mod.getY() && y < mod.getY() + mod.getH();
    }
}
