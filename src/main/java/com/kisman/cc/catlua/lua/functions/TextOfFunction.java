package com.kisman.cc.catlua.lua.functions;

import com.kisman.cc.catlua.common.trait.Nameable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class TextOfFunction extends OneArgFunction implements Nameable {

    @Override public LuaValue call(LuaValue arg) {
        return userdataOf((arg.tojstring()));
    }


    @Override public String getName() {
        return "textOf";
    }

}
