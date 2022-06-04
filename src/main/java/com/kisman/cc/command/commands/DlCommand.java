package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class DlCommand extends Command {
    public DlCommand() {
        super("dl");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            switch(args[0]) {
                case "default": {

                }
            }
        } catch (Exception e) {ChatUtils.error("Usage: " + getSyntax());}
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "dl <module> <x/y/z> <coord>";
    }
}
