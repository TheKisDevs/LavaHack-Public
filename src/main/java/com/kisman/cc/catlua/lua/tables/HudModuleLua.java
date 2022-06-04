package com.kisman.cc.catlua.lua.tables;

import com.kisman.cc.catlua.lua.LuaCallback;
import com.kisman.cc.catlua.lua.utils.*;
import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.hud.hudmodule.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.*;

public class HudModuleLua extends HudModule {
    private final ModuleScript script;
    private static LuaValue value;

    private LuaClosure onEnable;
    private LuaClosure onDisable;

    public HudModuleLua(String name, String description, HudCategory category, boolean drag, ModuleScript script) {
        super(name, description, category, drag);
        this.script = script;
    }

    public ModuleScript getScript() {
        return script;
    }

    public void body(LuaClosure luaClosure) {
        LuaUtils.safeCall(script, luaClosure);
        register();
    }

    public LuaCallback registerCallback(String name, LuaClosure luaFunction) {
        LuaCallback callback = new LuaCallback(name, luaFunction, this);
        script.callbacks.add(callback);
        return callback;
    }

    public void invoke(String name, LuaValue arg) {
        if(script.callbacks == null) return;
        script.callbacks.stream().filter(c -> c.name.equalsIgnoreCase(name)).forEach(c -> c.run(arg));
    }

    public void register() {
        script.addHudModule(this);
    }

    public void setPosition(LuaVec2d vec) {
        setX(vec.x);
        setY(vec.y);
    }

    public void setSize(LuaVec2d vec) {
        setW(vec.x);
        setH(vec.y);
    }

    public void setPositionWithSize(LuaVec2d pos, LuaVec2d size) {
        setPosition(pos);
        setSize(size);
    }

    public void onEnable(LuaClosure closure) {
        this.onEnable = closure;
    }

    public void onDisable(LuaClosure closure) {
        this.onDisable = closure;
    }

    @Override public void onEnable() {
        if(!script.isToggled()) return;
        LuaUtils.safeCall(onEnable);
    }

    @Override public void onDisable() {
        if(!script.isToggled()) return;
        LuaUtils.safeCall(onDisable);
    }

    public static LuaValue getLua() {
        if(value == null) {
            LuaValue hudModuleLua = CoerceJavaToLua.coerce(HudModuleLua.class);
            LuaTable table = new LuaTable();
            table.set("new", new HudModuleLua.New());
            table.set("__index", table);
            hudModuleLua.setmetatable(table);
            value = hudModuleLua;
        }
        return value;
    }

    @Override public String toString() {
        return "HudModuleLua{" +
                "script=" + script +
                "} " + super.toString();
    }

    static class New extends LibFunction {
        public LuaValue call(LuaValue name, LuaValue description, LuaValue category, LuaValue drag, LuaValue script) {
            if(!name.isstring() || !description.isstring() || !category.isstring() || !drag.isstring()) throw new IllegalArgumentException("Invalid arguments.");
            return CoerceJavaToLua.coerce(new HudModuleLua(name.tojstring(), description.tojstring(), HudCategory.valueOf(category.tojstring()), Boolean.valueOf(drag.tojstring()), ( ModuleScript ) CoerceLuaToJava.coerce(script, ModuleScript.class)));
        }
    }
}
