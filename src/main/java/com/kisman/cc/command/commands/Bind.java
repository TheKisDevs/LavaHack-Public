package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import com.kisman.cc.module.Module;

import org.lwjgl.input.Keyboard;

public class Bind extends Command{
    public Bind() {
        super("bind");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            String key = args[0];
            String isList = args[0];

            if(args.length == 1 && !isList.equalsIgnoreCase("list")) {
                error("Usage: " + getSyntax());
                return;
            }

            if(args.length > 2 && isList.equalsIgnoreCase("list")) {
                error("Usage: " + getSyntax());
                return;
            }

            if(args.length == 1 && isList.equalsIgnoreCase("list")) {
                message("----------------------------------");
                message("Bind List:");
                for(Module mod : Kisman.instance.moduleManager.modules) if(Keyboard.KEY_NONE != mod.getKey()) message(mod.getName() + " | " + Keyboard.getKeyName(mod.getKey()));
                message("----------------------------------");
                return;
            }

            for(Module mod : Kisman.instance.moduleManager.modules) {
                if(mod.getName().equalsIgnoreCase(args[1])) {
                    mod.setKey(Keyboard.getKeyIndex((key.toUpperCase())));
                    message(mod.getName() + " binned to " + Keyboard.getKeyName(mod.getKey()));
                }
            }
        } catch(Exception e) {error("Usage: " + getSyntax());}
    }

    @Override
	public String getDescription() {
		return "Change key for modules/commands.";
	}

	@Override
	public String getSyntax() {
		return "bind <key> <command/module> or bind list";
	}
}
