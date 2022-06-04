package com.kisman.cc.event.events.clickguiEvents.mouseClicked;

import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.clickguiEvents.drawScreen.render.GuiRenderPostEvent;
import net.minecraft.client.gui.GuiNewChat;

public class MouseClickedPreEvent extends Event {
    public int mouseX, mouseY, mouseButton;
    public GuiRenderPostEvent.Gui gui;

    public MouseClickedPreEvent(int mouseX, int mouseY, int mouseButton) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
        this.gui = GuiRenderPostEvent.Gui.OldGui;
    }

    public MouseClickedPreEvent(int mouseX, int mouseY, int mouseButton, GuiRenderPostEvent.Gui gui) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
        this.gui = gui;
    }
}
