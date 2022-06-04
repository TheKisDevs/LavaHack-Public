package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import com.kisman.cc.settings.Setting;

public class StringEvent extends Event {
    public Setting set;
    public String str;
    public Era era;

    public boolean active;

    public StringEvent(Setting set, String str, Era era, boolean active) {
        this.set = set;
        this.str = str;
        this.era = era;
        this.active = active;
    }
}
