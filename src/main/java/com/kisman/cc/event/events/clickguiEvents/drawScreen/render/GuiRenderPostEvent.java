package com.kisman.cc.event.events.clickguiEvents.drawScreen.render;

import com.kisman.cc.event.Event;

public class GuiRenderPostEvent extends Event {
    public int mouseX, mouseY;
    public float partialTicks;
    public Gui gui;

    public GuiRenderPostEvent(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }

    public GuiRenderPostEvent(int mouseX, int mouseY, float partialTicks, Gui gui) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        this.gui = gui;
    }

    public enum Gui {
        NewGui,
        OldGui,
        CSGOGui
    }
}
