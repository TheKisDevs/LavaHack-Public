package com.kisman.cc.catlua.lua.functions;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import java.awt.*;

public class ColorFunction extends LibFunction {

    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4) {
        return userdataOf(new Color(arg1.toint(), arg2.toint(), arg3.toint(), arg4.toint()));
    }

    public Varargs invoke(Varargs varargs) {
        return call(varargs.arg1(), varargs.arg(2), varargs.arg(3), varargs.arg(4));
    }

}