package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

public class CheckUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    private static double x;
    private static double y;
    private static double z;

    public static void savecord() {
        x = mc.player.posX;
        y = mc.player.posY;
        z = mc.player.posZ;
    }


    public static void loadcord() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));

        mc.player.setPositionAndUpdate(x, y, z);
        mc.player.setPosition(x, y, z);
    }
}
