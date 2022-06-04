package com.kisman.cc.event.events.clickguiEvents.mouseReleased;

import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.clickguiEvents.drawScreen.render.GuiRenderPostEvent;

public class MouseReleasedPreEvent extends Event {
    public int mouseX, mouseY, mouseButton;
    public GuiRenderPostEvent.Gui gui;

    public MouseReleasedPreEvent(int mouseX, int mouseY, int mouseButton) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
        this.gui = GuiRenderPostEvent.Gui.OldGui;
    }

    public MouseReleasedPreEvent(int mouseX, int mouseY, int mouseButton, GuiRenderPostEvent.Gui gui) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
        this.gui = gui;
    }
}
