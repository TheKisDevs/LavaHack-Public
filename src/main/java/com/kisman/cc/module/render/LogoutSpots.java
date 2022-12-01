package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.mojang.authlib.GameProfile;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class LogoutSpots extends Module {

    public LogoutSpots() {
        super("LogoutSpots", Category.RENDER);

    }

   
}
