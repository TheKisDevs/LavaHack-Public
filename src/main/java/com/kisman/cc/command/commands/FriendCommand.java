package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class FriendCommand extends Command {
    public String[] subCommands = new String[] {"add", "remove", "list"};

    public FriendCommand() {
        super("friend");
    }

    public void runCommand(String s, String[] args) {
        try {
            if(args[0] != null && args[1] != null) {
                if(args[0].equalsIgnoreCase(subCommands[0])) {
                    Kisman.instance.friendManager.addFriend(args[1]);
                    complete(args[1] + " added in friends!");
                } else if(args[0].equalsIgnoreCase(subCommands[1])) {
                    Kisman.instance.friendManager.removeFriend(args[1]);
                    complete(args[1] + " removed from friends :(");
                } else error("Usage: " + getSyntax());
            } else if(subCommands[2].equalsIgnoreCase(args[0])) {
                print("----------------------------------");
                print("Friends:");
                print(Kisman.instance.friendManager.getFriendsNames());
                print("----------------------------------");
            }
        } catch (Exception e) {error("Usage: " + getSyntax());}
    }

    public String getDescription() {return "friend's command";}
    public String getSyntax() {return "friend <add/remove> <player's name> or friend list";}
}
