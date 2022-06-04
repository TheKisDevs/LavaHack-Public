package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;

public class EventRenderHand extends Event {
    private final float ticks;

    public EventRenderHand(float ticks) {this.ticks = ticks;}
    public float getPartialTicks() {return ticks;}
    public static class PostOutline extends EventRenderHand { public PostOutline(float ticks) {super(ticks);}}
    public static class PreOutline extends EventRenderHand { public PreOutline(float ticks) {super(ticks);}}
    public static class PostFill extends EventRenderHand { public PostFill(float ticks) {super(ticks);}}
    public static class PreFill extends EventRenderHand { public PreFill(float ticks) {super(ticks);}}
}
