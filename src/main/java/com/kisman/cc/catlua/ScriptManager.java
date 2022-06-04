package com.kisman.cc.catlua;

import com.kisman.cc.catlua.module.ModuleScript;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;

public class ScriptManager {
    public ArrayList<ModuleScript> scripts = new ArrayList<>();
    public static boolean strict = false;

    public ScriptManager load() {
        return this;
    }

    public ScriptManager unload() {
        scripts.clear();
        return this;
    }

    public void runCallback(String callback, LuaValue o) {
        for(ModuleScript script : scripts) {
            if(!script.isToggled() || !script.isLoaded()) continue;
            script.invoke(callback, o);
        }
    }

    public void runCallback(String callback) {
        runCallback(callback, LuaValue.NIL);
    }

    public ModuleScript get(String name) {
        return scripts.stream().filter(script -> script.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isScriptExist(String name) {
        for(ModuleScript script : scripts) if(script.getName().equalsIgnoreCase(name)) return true;
        return false;
    }
}
