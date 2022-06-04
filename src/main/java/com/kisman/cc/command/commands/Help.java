package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Help extends Command {
    public Help() {
        super("help");
    }

    public void runCommand(String s, String[] args) {
        message("Commands:");
        message("bind <key> <module>");
        message("bind list");
        message("credits");
        message("flip - this command only for Hypixel Skyblock");
        message("friend <add/remove> <player's name>");
        message("friend list");
        message("help");
        message("loadconfig");
        message("opendir");
        message("saveconfig");
        message("setkey - this command only for Hypixel Skyblock");
        message("slider <module> <slider's name> <value>");
        message("toggle <module>");
        message("tp <x> <y> <z>");
        message("tp <player's nickname>");
    }

    public String getDescription() {
        return "help of commands";
    }

    public String getSyntax() {
        return "help";
    }
}
