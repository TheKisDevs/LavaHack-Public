package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class NoCumCommand extends Command {
    public NoCumCommand() {
        super("nocum");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("default")) {

            } else if(args[0].equalsIgnoreCase("spiral")) {

            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "sus";
    }

    @Override
    public String getSyntax() {
        return "nocum spiral <centreX/centreZ/max/skip/step> <value> or nocum default <x/z> <value>";
    }
}
