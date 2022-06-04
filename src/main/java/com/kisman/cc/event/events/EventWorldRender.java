package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventWorldRender extends Event{
    public final float particalTicks;

    public EventWorldRender(float particalTicks) {
        super();
        this.particalTicks = particalTicks;
    }

    public float getParticalTicks() {
        return particalTicks;
    }
}
