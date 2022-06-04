package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class TurnEvent extends Event {
    public float yaw, pitch;

    public TurnEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
