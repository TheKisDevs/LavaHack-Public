package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;


/**
 * Util for print debug log into game chat
 *
 */
public class ChatLoggerUtil {
    private final Minecraft mc;
    private final String _moduleName;

    public ChatLoggerUtil(String moduleName)
    {
        mc = Minecraft.getMinecraft();
        _moduleName = moduleName;
    }

    public boolean Log(String text)
    {
        if(mc.player == null || mc.world == null) return false;
        mc.player.sendMessage(new TextComponentString("§2[" + _moduleName + "]:§f " + text));
        return true;
    }

    public boolean LogLackluster(String text)
    {
        if(mc.player == null || mc.world == null) return false;
        mc.player.sendMessage(new TextComponentString("§7[" + _moduleName + "]:§7 " + text));
        return true;
    }

    public boolean Error(String text)
    {
        if(mc.player == null || mc.world == null) return false;
        mc.player.sendMessage(new TextComponentString("§c[" + _moduleName + "]:§f " + text));
        return true;
    }

    public boolean Warning(String text)
    {
        if(mc.player == null || mc.world == null) return false;
        mc.player.sendMessage(new TextComponentString("§e[" + _moduleName + "]:§f " + text));
        return true;
    }
}
