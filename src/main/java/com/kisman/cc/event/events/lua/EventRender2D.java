package com.kisman.cc.event.events.lua;

import com.kisman.cc.event.Event;

public class EventRender2D extends Event {
    public float particalTicks;
    public EventRender2D(float particalTicks) {
        this.particalTicks = particalTicks;
    }

    public String getName() {
        return "render_2d";
    }
}
