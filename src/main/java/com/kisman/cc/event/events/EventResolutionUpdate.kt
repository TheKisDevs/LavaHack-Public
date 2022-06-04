package com.kisman.cc.event.events

import com.kisman.cc.event.Event

class EventResolutionUpdate(width: Int, height: Int) : Event() {
    override fun getName(): String {
        return "update_resolution"
    }
}