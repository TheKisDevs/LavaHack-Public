package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.module.misc.DDOSModule;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class DDOSCommand extends Command {
    public DDOSCommand() {
        super("ddos");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("ip")) {
                DDOSModule.customIp = args[1];
            }
        } catch (Exception e) {
            error("ddos command get error");
        }
    }

    @Override
    public String getDescription() {
        return "Set custom ip for DDOS module or WebhookSpammer";
    }

    @Override
    public String getSyntax() {
        return "ddos ip <ip address>";
    }
}
