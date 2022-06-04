package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.util.text.ITextComponent;

public class EventDisconnect extends Event {
    public ITextComponent component;
    public EventDisconnect(ITextComponent component) {
        this.component = component;
    }
}
