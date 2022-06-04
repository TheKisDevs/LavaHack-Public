package com.kisman.cc.catlua.lua.tables;

import com.kisman.cc.catlua.lua.LuaCallback;
import com.kisman.cc.catlua.lua.utils.LuaUtils;
import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.module.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.*;

public class ModuleLua extends Module {

    private final ModuleScript script;
    private static LuaValue value;

    private LuaClosure onEnable;
    private LuaClosure onDisable;

    public ModuleLua(String name, String desc, Category category, ModuleScript script) {
        super(name, desc, category);
        this.script = script;
        super.subscribes = false;
    }

    public ModuleLua(String name, String desc, String category, ModuleScript script) {
        super(name, desc, Category.valueOf(category));
        this.script = script;
        super.subscribes = false;
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
        script.addModule(this);
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
            LuaValue moduleLua = CoerceJavaToLua.coerce(ModuleLua.class);
            LuaTable table = new LuaTable();
            table.set("new", new New());
            table.set("__index", table);
            moduleLua.setmetatable(table);
            value = moduleLua;
        }
        return value;
    }

    @Override public String toString() {
        return "ModuleLua{" +
                "script=" + script +
                "} " + super.toString();
    }

    static class New extends LibFunction {
        public LuaValue call(LuaValue name, LuaValue description, LuaValue category, LuaValue script) {
            if(!name.isstring() || !description.isstring() || !category.isstring()) throw new IllegalArgumentException("Invalid arguments.");
            return CoerceJavaToLua.coerce(new ModuleLua(name.tojstring(), description.tojstring(), Category.valueOf(category.tojstring()), ( ModuleScript ) CoerceLuaToJava.coerce(script, ModuleScript.class)));
        }
    }
}
