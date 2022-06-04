package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.util.MovementUtil;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.client.CPacketPlayer;

public class FastLadder extends Module {
    public FastLadder() {
        super("FastLadder", Category.PLAYER);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(send);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(send);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(mc.player.isOnLadder()) mc.player.jump();
    }

    @EventHandler
    private final Listener<PacketEvent.Send> send = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer && mc.player.isOnLadder() && MovementUtil.isMoving()) ((CPacketPlayer) event.getPacket()).onGround = true;
    });
}
