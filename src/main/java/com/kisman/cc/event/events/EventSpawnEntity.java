package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.entity.Entity;

public class EventSpawnEntity extends Event {
    public Entity entity;

    public EventSpawnEntity(Entity entity) {
        this.entity = entity;
    }
}
