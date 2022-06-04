package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;

public class SaveConfigCommand extends Command {
    public SaveConfigCommand() {
        super("saveconfig");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args.length > 0) {
                error("Usage: " + getSyntax());
                return;
            }

            warning("Start saving configs!");
            Kisman.instance.configManager.getSaver().init();
            message("Saved Config!");
        } catch (Exception e) {
            error("Saving config is failed!");
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "saving confing";
    }

    @Override
    public String getSyntax() {
        return "saveconfig";
    }
}
