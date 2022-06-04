package com.kisman.cc.catlua.lua.functions;

import com.kisman.cc.catlua.common.util.StopWatch;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class StopWatchFunction extends ZeroArgFunction {

    @Override public LuaValue call() {
        return CoerceJavaToLua.coerce(new StopWatch());
    }

}
