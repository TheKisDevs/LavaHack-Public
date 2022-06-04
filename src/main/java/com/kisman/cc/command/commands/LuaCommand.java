package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.lua.tables.ModuleLua;
import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class LuaCommand extends Command {
    public LuaCommand() {
        super("lua");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("info")) complete("Documentation: https://cattyn.gitbook.io/ferret-lua-api/reference/readme.");
            else if(args[0].equalsIgnoreCase("load")) {
                for(ModuleScript script : Kisman.instance.scriptManager.scripts) {
                    if(script.getName().equalsIgnoreCase(args[1])) {
                        error("[Lua] Script " + args[1] + " is already loaded!");
                        return;
                    }
                }
                try {
                    ModuleScript script = new ModuleScript(args[1], ".lua script");
                    script.load();
                    Kisman.instance.scriptManager.scripts.add(script);
                    Kisman.reloadGUIs();
                    complete("[Lua] Successful loaded " + args[1] + " script!");
                } catch (IOException e) {
                    error("[Lua] Invalid script path!");
                }
            } else if(args[0].equalsIgnoreCase("get")) {
                if(Kisman.instance.scriptManager.isScriptExist(args[1])) {
                    if (args[2].equalsIgnoreCase("state")) {
                        boolean state;
                        if(args[3].equalsIgnoreCase("true")) state = true;
                        else if(args[3].equalsIgnoreCase("false")) state = false;
                        else {
                            error("[Lua] State " + args[3] + " doesn't convert to boolean type!");
                            return;
                        }
                        ModuleLua script = Kisman.instance.scriptManager.get(args[1]).get(args[4]);
                        if(script != null) {
                            script.setToggled(state);
                            message(TextFormatting.GRAY + "[Lua] Module " + (state ? TextFormatting.GREEN : TextFormatting.RED) + args[4] + TextFormatting.GRAY + " has been " + (state ? "enabled" : "disabled") + "!");
                        } else error("[Lua] Module " + args[4] + " in script " + args[1] + " doesn't exists!");
                    } else if(args[2].equalsIgnoreCase("action")) {
                        Action action;
                        if(args[3].equalsIgnoreCase("unload")) action = Action.UNLOAD;
                        else if(args[3].equalsIgnoreCase("reload")) action = Action.RELOAD;
                        else {
                            error("[Lua] Action " + args[3] +  " doesn't exists!");
                            return;
                        }
                        ModuleScript script = Kisman.instance.scriptManager.get(args[1]);
                        if(script != null) {
                            switch(action) {
                                case UNLOAD:
                                    script.unload(true);
                                    complete("[Lua] Script " + script.getName() + " has been unloaded!");
                                    break;
                                case RELOAD:
                                    script.reload();
                                    complete("[Lua] Script " + script.getName() + " has been reloaded!");
                                    break;
                            }
                        } else error("[Lua] Script " + args[4] + " doesn't exists!");
                    } else error("Usage: " + getSyntax());
                } else error("[Lua] Script " + args[1] + " doesn't exists!");
            }
        } catch (Exception e) {
            error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "catlua";
    }

    @Override
    public String getSyntax() {
        return "\n" +
                "lua info\n" +
                "lua load <script name>\n" +
                "lua get <script name> <action> <unload/reload>\n" +
                "lua get <script name> <state> <true/false> <module from script name>";
    }

    public enum Action {
        UNLOAD, RELOAD
    }
}
