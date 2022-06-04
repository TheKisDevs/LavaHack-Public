package com.kisman.cc.catlua.lua.functions;

import com.kisman.cc.catlua.common.trait.Nameable;
import com.kisman.cc.catlua.lua.utils.LuaVec2d;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class Vec2dFunction extends TwoArgFunction implements Nameable {

    @Override public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return userdataOf(new LuaVec2d(arg1.todouble(), arg2.todouble()));
    }

    @Override public String getName() {
        return "vec2d";
    }

}
