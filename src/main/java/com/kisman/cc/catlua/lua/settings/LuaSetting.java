package com.kisman.cc.catlua.lua.settings;

import com.kisman.cc.catlua.lua.exception.LuaIllegalNumberTypeException;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaSetting {
    public static Slider.NumberType getNumberTypeByName(String name) {
        for(Slider.NumberType type : Slider.NumberType.values()) if(type.name().equals(name)) return type;
        throw new LuaIllegalNumberTypeException("Lua: cant resolve number type " + name);
    }

    public static final class LuaBuilder extends OneArgFunction {
        @Override public LuaValue call(LuaValue arg) {
            return CoerceJavaToLua.coerce(new Setting(arg.tojstring()));
        }
    }
}
