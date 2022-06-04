package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

public class NoFriendDamage extends Module {
    public NoFriendDamage() {
        super("NoFriendDamage", Category.COMBAT);
    }

    public void onEnable() {Kisman.EVENT_BUS.subscribe(listener);}
    public void onDisable() {Kisman.EVENT_BUS.unsubscribe(listener);}

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            Entity target = packet.getEntityFromWorld(mc.world);
            if(target instanceof EntityPlayer && FriendManager.instance.isFriend(target.getName())) event.cancel();
        }
    });
}
