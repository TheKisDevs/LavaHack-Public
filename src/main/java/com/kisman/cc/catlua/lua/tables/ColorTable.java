package com.kisman.cc.catlua.lua.tables;

import com.kisman.cc.util.Colour;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.awt.*;

public class ColorTable {

    private static LuaValue table;

    public static LuaValue getLua() {
        if (table == null) {
            table = new LuaTable();
            table.set("rgb", new Rgb());
            table.set("rgba", new Rgba());
            table.set("hsv", new Hsv());
            table.set("pulse", new Pulse());
            table.set("fade", new Fade());
            table.set("rainbow", new Rainbow());
        }
        return table;
    }

    static class Rgb extends ThreeArgFunction {
        @Override public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            return userdataOf(new Color(arg1.toint(), arg2.toint(), arg3.toint()));
        }
    }

    static class Rgba extends LibFunction {
        @Override public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4) {
            return userdataOf(new Color(arg1.toint(), arg2.toint(), arg3.toint(), arg4.toint()));
        }
    }

    static class Hsv extends ThreeArgFunction {
        @Override public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            return userdataOf(Color.getHSBColor(arg1.tofloat(), arg2.tofloat(), arg3.tofloat()));
        }
    }

    static class Pulse extends LibFunction {
        @Override public LuaValue call(LuaValue from, LuaValue to, LuaValue time, LuaValue delay) {
            double current = (((System.currentTimeMillis() + delay.toint()) % time.todouble()) / time.todouble());
            if (current > 0.5) return fade(to, from, LuaValue.valueOf((current - 0.5) * 2 ));
            else return fade(from, to, LuaValue.valueOf(current * 2));
        }
    }

    static class Fade extends ThreeArgFunction {
        @Override public LuaValue call(LuaValue from, LuaValue to, LuaValue progress) {
            return fade(from, to, progress);
        }
    }

    static class Rainbow extends LibFunction {
        @Override public LuaValue call(LuaValue d, LuaValue s, LuaValue b, LuaValue a) {
            double rainbowState = Math.ceil((System.currentTimeMillis() + d.toint()) / 20.0);
            rainbowState %= 360.0;

            Color c = Color.getHSBColor(( float ) (rainbowState / 360.0), s.tofloat(), b.tofloat());
            return userdataOf(new Color(c.getRed(), c.getGreen(), c.getBlue(), a.toint()));
        }
    }

    static class Astolfo extends LibFunction {
        @Override public LuaValue call(LuaValue d, LuaValue s, LuaValue b, LuaValue a) {
            return userdataOf(new Colour(ColorUtils.astolfoColors(100, 100)).setSaturation(s.tofloat()).setBrightness(b.tofloat()).setAlpha(a.toint()).getColor());
        }
    }

    public static LuaValue fade(LuaValue from, LuaValue to, LuaValue progress) {
        Color cfrom = ( Color ) CoerceLuaToJava.coerce(from, Color.class);
        Color cto = ( Color ) CoerceLuaToJava.coerce(to, Color.class);
        return CoerceJavaToLua.coerce(new Color(
                (int) (cfrom.getRed() * progress.tofloat() + cto.getRed() * (1 - progress.tofloat())),
                (int) (cfrom.getGreen() * progress.tofloat() + cto.getGreen() * (1 - progress.tofloat())),
                (int) (cfrom.getBlue() * progress.tofloat() + cto.getBlue() * (1 - progress.tofloat())),
                (int) (cfrom.getAlpha() * progress.tofloat() + cto.getAlpha() * (1 - progress.tofloat()))
        ));
    }

}
