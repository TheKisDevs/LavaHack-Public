package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventPlayerPushOutOfBlocks extends Event {
    public double x, y, z;

    public EventPlayerPushOutOfBlocks(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
