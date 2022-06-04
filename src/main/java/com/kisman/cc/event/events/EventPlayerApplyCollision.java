package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.Entity;

public class EventPlayerApplyCollision extends Event {
    public Entity entity;

    public EventPlayerApplyCollision(Entity entity) {
        this.entity = entity;
    }
}
