package com.kisman.cc.catlua.module;

import com.google.gson.JsonObject;
import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.lua.LuaCallback;
import com.kisman.cc.catlua.lua.functions.*;
import com.kisman.cc.catlua.lua.settings.LuaSetting;
import com.kisman.cc.catlua.lua.tables.*;
import com.kisman.cc.catlua.lua.utils.*;
import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.*;

import javax.script.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleScript extends Module {
    protected JsonObject cache = new JsonObject();
    public transient String script;
    private final Path path;
    private boolean loaded;
    public transient final List<LuaCallback> callbacks = new ArrayList<>();
    public transient final List<ModuleLua> modulesLua = new ArrayList<>();
    public transient final List<HudModuleLua> hudModulesLua = new ArrayList<>();

    public ModuleScript(String name, String desc) throws IOException {
        super(name, desc, Category.LUA);
        this.path = Paths.get(Kisman.fileName + Kisman.luaName, name);
        this.script = new String(Files.readAllBytes(path));
    }

    public ModuleLua get(String name) {
        return modulesLua.stream().filter(modulesLua -> modulesLua.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void load() {
        Kisman.instance.remapper3000.classCache.clear();
        Kisman.instance.remapper3000.fieldsCache.clear();
        Kisman.instance.remapper3000.methodsCache.clear();
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("lua");

        try {
            applyEngine(engine);

            engine.eval(script);
            engine.eval("main()");
        } catch (Exception e) {
            ChatUtils.error(e.getMessage());
        }
        if (cache.has("__toggled")) setToggled(cache.get("__toggled").getAsBoolean());
        cache = new JsonObject();
        loaded = true;
    }

    public void unload(boolean remove) {
        loaded = false;
        if (remove) {
            Kisman.instance.scriptManager.scripts.remove(this);
            Kisman.instance.remapper3000.classCache.clear();
            Kisman.instance.remapper3000.fieldsCache.clear();
            Kisman.instance.remapper3000.methodsCache.clear();
            Kisman.instance.moduleManager.modules.removeAll(modulesLua);
            Kisman.reloadGUIs();
        }
        for (ModuleLua lua : modulesLua) Kisman.instance.settingsManager.getSettings().removeIf(setting -> setting.getParentMod().equals(lua) || setting.getParentMod() == null || setting.getParentMod().equals(this));
        Kisman.instance.moduleManager.modules.removeAll(modulesLua);
        Kisman.instance.hudModuleManager.modules.removeAll(hudModulesLua);
        modulesLua.clear();
        hudModulesLua.clear();
        callbacks.clear();
    }

    public void reload() {
        try {
            this.script = new String(Files.readAllBytes(path));
            unload(false);
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO make that shit better lole
    private void applyEngine(ScriptEngine engine) {
        engine.put("mc", Minecraft.instance);
        engine.put("this", this);
        engine.put("textOf", new TextOfFunction());
        engine.put("box", new BoxFunction());
        engine.put("vec2d", new Vec2dFunction());
        engine.put("vec3d", new Vec3dFunction());
        engine.put("color", new ColorFunction());
        engine.put("colorutil", ColorTable.getLua());
        engine.put("StopWatch", new StopWatchFunction());
        engine.put("client", Kisman.instance);
        engine.put("renderer", LuaRenderer.getDefault());
        engine.put("globals", LuaGlobals.getDefault());
        engine.put("interactions", LuaInteractions.getDefault());
        engine.put("files", LuaFiles.getDefault());
        engine.put("Module", ModuleLua.getLua());
        engine.put("HudModule", HudModuleLua.getLua());
        engine.put("GuiBuilder", GuiBuilder.getLua());
        engine.put("rotations", Kisman.instance.luaRotation);
        engine.put("net", LuaNet.Companion.getDefault());

        engine.put("Builder", new LuaSetting.LuaBuilder());
        engine.put("SettingManager", Kisman.instance.settingsManager);
    }

    public LuaCallback registerCallback(String name, LuaClosure luaFunction) {
        LuaCallback callback = new LuaCallback(name, luaFunction, this);
        callbacks.add(callback);
        return callback;
    }

    public void invoke(String name, LuaValue arg) {
        if (callbacks == null || callbacks.isEmpty()) return;
        for (int i = 0; i < callbacks.size(); i++) {
            final LuaCallback c = callbacks.get(i);
            if (c.name.equalsIgnoreCase(name)) c.run(arg);
        }
    }

    public void invoke(String name) {
        invoke(name, LuaValue.NONE);
    }

    public void addModule(ModuleLua lua) {
        modulesLua.add(lua);
        Kisman.instance.moduleManager.modules.add(lua);
        Kisman.reloadGUIs();
    }

    public void addHudModule(HudModuleLua lua) {
        hudModulesLua.add(lua);
        Kisman.instance.hudModuleManager.modules.add(lua);
        Kisman.reloadHudGUIs();
    }

    public List<LuaCallback> getCallbacks() {
        return callbacks;
    }

    public Path getPath() {
        return path;
    }

    public boolean isLoaded() { return loaded; }
}
