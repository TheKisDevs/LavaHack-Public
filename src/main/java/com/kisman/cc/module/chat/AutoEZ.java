package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;

import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoEZ extends Module {
    public static List<String> AutoGgMessages = new ArrayList<>(Arrays.asList("{name} owned by {player_name} with " + Kisman.getName(), "gg, {name}!", Kisman.getName() + " owning {name}"));
    private ConcurrentHashMap targetedPlayers = null;
    private int index = -1;

    private Setting random = new Setting("Random message", this, true);

    public AutoEZ() {
        super("AutoEZ", "", Category.CHAT);

        setmgr.rSetting(random);
    }

    public void onEnable() {
        super.onEnable();
        targetedPlayers = new ConcurrentHashMap();
        Kisman.EVENT_BUS.subscribe(send);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if (this.targetedPlayers == null) this.targetedPlayers = new ConcurrentHashMap();
        Iterator var1 = mc.world.loadedEntityList.iterator();

        while (var1.hasNext()) {
            Entity entity = (Entity) var1.next();
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if(player.getHealth() < 0) {
                    String name = player.getName();
                    if(shouldAnnounce(name)) {
                        doAnnounce(name);
                        break;
                    }
                }
            }
        }

        targetedPlayers.forEach((name, timeout) -> {
            if((int) timeout < 0) targetedPlayers.remove(name);
            else targetedPlayers.put(name, (int) timeout - 1);
        });
    }

    @EventHandler
    private final Listener<PacketEvent.Send> send = new Listener<>(event -> {
        if(mc.player != null) {
            if(targetedPlayers == null) targetedPlayers = new ConcurrentHashMap();

            if(event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
                if(packet.action.equals(CPacketUseEntity.Action.ATTACK)) {
                    Entity entity = packet.getEntityFromWorld(mc.world);
                    if(entity instanceof EntityPlayer) addTargetedPlayer(entity.getName());
                }
            }
        }
    });

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if(mc.player != null) {
            if(targetedPlayers == null) targetedPlayers = new ConcurrentHashMap();

            EntityLivingBase entity = event.getEntityLiving();
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if(player.getHealth() < 0) {
                    String name = player.getName();
                    if(shouldAnnounce(name)) doAnnounce(name);
                }
            }
        }
    }

    private boolean shouldAnnounce(String name) {
        return this.targetedPlayers.containsKey(name);
    }

    private void doAnnounce(String name) {
        targetedPlayers.remove(name);
        if (index >= (AutoGgMessages.size() - 1)) index = -1;
        index++;
        String message;
        if (AutoGgMessages.size() > 0 && random.getValBoolean()) message = AutoGgMessages.get(index);
        else message = "{name} ezzzz " + Kisman.getName() + " " + Kisman.getVersion() + " on top!";
        String messageSanitized = message.replaceAll("ยง", "").replace("{name}", name).replace("{player_name}", mc.player.getName());
        if (messageSanitized.length() > 255) messageSanitized = messageSanitized.substring(0, 255);
        mc.player.sendChatMessage(messageSanitized);
    }

    public void addTargetedPlayer(String name) {
        if (!Objects.equals(name, mc.player.getName())) {
            if (targetedPlayers == null) targetedPlayers = new ConcurrentHashMap();
            targetedPlayers.put(name, 20);
        }
    }

    public void onDisable() {
        super.onDisable();
        targetedPlayers = null;
    }
}
