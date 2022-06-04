package com.kisman.cc.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ColourSuper extends Colour {
    public int mode;
    public Colour color;

    public ColourSuper(int mode) {
        super(Colour.getEnumByInt(mode).getColour().getColor());
        this.mode = mode;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public ColourSuper(ColourEnum mode) {
        super(Colour.getEnumByInt(mode.mode).getColour().getColor());
        this.mode = mode.mode;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        syns(get());
    }

    public Colour get() {
        return Colour.getEnumByInt(mode).getColour();
    }
}
