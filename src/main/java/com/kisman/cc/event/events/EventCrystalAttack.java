package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventCrystalAttack extends Event {
    public int entityId;

    public EventCrystalAttack(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityID() {
        return this.entityId;
    }
}