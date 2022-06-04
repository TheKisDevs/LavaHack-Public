package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.file.LoadConfig;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class LoadConfigCommand extends Command {
    public LoadConfigCommand() {
        super("loadconfig");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args.length > 0) {
                error("Usage: " + getSyntax());
                return;
            }

            warning("Start loading configs!");
            LoadConfig.init();
            message("Loaded Config!");
        } catch (Exception e) {
            error("Loaded config is failed!");
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "loading config";
    }

    @Override
    public String getSyntax() {
        return "loadconfig";
    }
}
