package com.kisman.cc.catlua.lua.functions;

import com.kisman.cc.catlua.common.trait.Nameable;
import com.kisman.cc.catlua.lua.utils.LuaBox;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

public class BoxFunction extends LibFunction implements Nameable {
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4, LuaValue arg5, LuaValue arg6) {
        return userdataOf(new LuaBox(arg1.todouble(), arg2.todouble(), arg3.todouble(), arg4.todouble(), arg5.todouble(), arg6.todouble()));
    }

    @Override
    public String getName() {
        return "box";
    }
}
