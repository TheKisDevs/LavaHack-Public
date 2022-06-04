package com.kisman.cc.util.mixin.util;

import com.google.common.collect.Ordering;
import com.kisman.cc.util.mixin.Mapping;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.lang.reflect.Field;

public class GuiPlayerTabOverlayUtil {
    public static Ordering<NetworkPlayerInfo> getEntityOrdering() {
        try {
            Field field = GuiPlayerTabOverlay.class.getField(Mapping.ENTRY_ORDERING);
            field.setAccessible(true);
            return (Ordering<NetworkPlayerInfo>) field.get(GuiPlayerTabOverlay.class);
        } catch (Exception e) {return null;}
    }
}
