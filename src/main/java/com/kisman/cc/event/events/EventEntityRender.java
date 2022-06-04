package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventEntityRender extends Event {
    private float partialTicks;
    private Era era;

    public EventEntityRender(float partialTicks, Era era) {
        this.partialTicks = partialTicks;
        this.era = era;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public Era getEra() {
        return era;
    }
}
