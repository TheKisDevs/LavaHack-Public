package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.module.chat.AntiSpammer;

public class AntiSpammerCommand extends Command {
    public AntiSpammerCommand() {
        super("antispam");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("add")) {
                AntiSpammer.instance.illegalWords.add(args[1]);
                complete(args[1] + " added to AntiSpammer list");
            } else if(args[0].equalsIgnoreCase("remove")) {
                AntiSpammer.instance.illegalWords.remove(args[1]);
                complete(args[1] + " removed from AntiSpammer list");
            } else if(args[0].equalsIgnoreCase("clear")) {
                AntiSpammer.instance.illegalWords.clear();
                complete("AntiSpammer list has been cleared");
            } else if(args[0].equalsIgnoreCase("list")) {
                print("AntiSpammer list:");
                for(String str : AntiSpammer.instance.illegalWords) print(str);
            }
        } catch (Exception e) {
            error("Usage:" + getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getSyntax() {
        return "antispam <add/remove/list>";
    }
}
