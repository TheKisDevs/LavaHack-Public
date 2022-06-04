package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;

public class Tp extends Command {
    private final String regex1 = "-[1-9][0-9]*";
    private final String regex2 = "[1-9][0-9]*";
    private final String regex3 = "-*[^0-9]*";

    public Tp() {
        super("tp");
    }

    public void runCommand(String s, String[] args) {
        if(args.length > 3) {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        } else if(args.length == 3) {
            for(String str : args) {
                if(str.matches(regex3)) {
                    ChatUtils.error("Usage: " + getSyntax());
                    return;
                }
            }

            //tp to coord

            int x = getCoord(args[0]);
            int y = getCoord(args[1]);
            int z = getCoord(args[2]);

            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        } else if(args.length == 1) {
            //tp to player

            if(getPlayer(args[0]) == null) {
                ChatUtils.error("The player" + args[0] + " does not exist!");
                return;
            }

            double x = getPlayer(args[0]).posX;
            double y = getPlayer(args[0]).posY;
            double z = getPlayer(args[0]).posZ;

            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        }
    }

    public String getDescription() {
        return "bypass tp on the world";
    }

    public String getSyntax() {
        return "tp <x> <y> <z> or tp <player nickname>";
    }

    private int getCoord(String str) {
        if(str.matches(regex1)) {
            return -(Integer.parseInt(str.substring(0)));
        } else if(str.matches(regex2)) {
            return Integer.parseInt(str);
        } else {
            return 0;
        }
    }

    private EntityPlayer getPlayer(String name) {
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player != mc.player && player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }
}
