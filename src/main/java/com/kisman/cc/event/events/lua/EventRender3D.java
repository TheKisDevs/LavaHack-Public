package com.kisman.cc.event.events.lua;

import com.kisman.cc.event.Event;

public class EventRender3D extends Event {
    public float particalTicks;
    public EventRender3D(float particalTicks) {
        this.particalTicks = particalTicks;
    }

    public String getName() {
        return "render_3d";
    }
}
