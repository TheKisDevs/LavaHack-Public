package com.kisman.cc.event.events.lua;

import com.kisman.cc.event.Event;

public class EventClientChat extends Event {
    public String message;
    public EventClientChat(String message) {this.message = message;}
    public String getMessage() {return message;}
    @Override public String getName() {return "client_chat";}
}
