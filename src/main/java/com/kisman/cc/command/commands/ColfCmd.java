package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.hypixel.skyblock.colf.MainColf;
import com.kisman.cc.hypixel.skyblock.colf.minecraft_integration.CoflSessionManager;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

import java.util.Base64;

public class ColfCmd extends Command {
    public ColfCmd() {
        super("colf");
    }

    public void runCommand(String s, String[] args) {
        if(args.length >= 1) {
            switch (args[0]) {
                case "start": {
                    ChatUtils.message("Starting connection...");
                    MainColf.wrapper.startConnection();
                    break;
                }
                case "stop": {
                    ChatUtils.message("Stopping connection...");
                    MainColf.wrapper.stop();
                    break;
                }
                case "reset": {
                    handleReset();
                    break;
                }
                case "connect": {
                    if(args.length == 2) {
                        String destination = args[1];

                        if(!destination.contains("://")) {
                            destination = new String(Base64.getDecoder().decode(destination));
                        }
                        ChatUtils.message("Stopping connection!");
                        MainColf.wrapper.stop();
                        ChatUtils.message("Opening connection to " + destination);
                        if(MainColf.wrapper.initializeNewSocket(destination)) {
                            ChatUtils.message("Success");
                        } else {
                            ChatUtils.message("Could not open connection, please check the logs");
                        }
                    }
                    break;
                }
                default: {
                    ChatUtils.message("Use: " + getSyntax());
                    break;
                }
            }
        }
    }

    public String getDescription() {
        return "colf";
    }

    public String getSyntax() {
        return "colf <start/stop/reset/connect";
    }

    private void handleReset() {
        MainColf.wrapper.stop();
        ChatUtils.message("Stopping Connection to CoflNet");
        CoflSessionManager.DeleteAllCoflSessions();
        ChatUtils.message("Deleting CoflNet sessions...");
        if(MainColf.wrapper.startConnection())
            ChatUtils.message("Started the Connection to CoflNet");
    }
}
