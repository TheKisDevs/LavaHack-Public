package com.kisman.cc.catlua.lua;

import com.kisman.cc.catlua.lua.utils.LuaUtils;
import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.module.Module;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

/**
 * callback names, tick, hud, events
 */

public class LuaCallback {
    public String name;
    public LuaClosure callback;
    public Module script;
    public LuaCallback(String name, LuaClosure callback, Module script) {
        this.name = name;
        this.callback = callback;
        this.script = script;
    }
    public void run(LuaValue o) {
        if (script.isToggled()) LuaUtils.safeCall(script, callback, o);
    }
}
