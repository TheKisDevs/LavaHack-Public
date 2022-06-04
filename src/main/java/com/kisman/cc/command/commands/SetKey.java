package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.hypixel.util.ConfigHandler;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;

public class SetKey extends Command {
    public SetKey() {
        super("setkey");
    }

    public void runCommand(String s, String[] args) {
        if (args.length > 0) {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", args[0]);
            complete(
                    TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" +
                            TextFormatting.GRAY + " API Key set to " + TextFormatting.GREEN + args[0]
            );
        } else {
            error("Usage: " + getSyntax());
        }
    }

    public String getDescription() {
        return "";
    }

    public String getSyntax() {
        return "setkey <Hypixel Skyblock API key>";
    }
}
