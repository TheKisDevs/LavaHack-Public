package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TeamRusherLag extends Module {
    private Setting time = new Setting("Lag Time (in ms)", this, 3000, 1000, 10000, true);
    private Setting text = new Setting("Message", this, "> #TeamRusher Lag: ON", "> #TeamRusher Lag: ON", true);

    private boolean canSend = false;
    private long lastPacket = 0L;

    public TeamRusherLag() {
        super("TeamRusherLag","TeamRusherLag", Category.MISC);

        setmgr.rSetting(time);
        setmgr.rSetting(text);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        lastPacket = 0L;
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        lastPacket = 0L;
    }

    public void update() {
        if(lastPacket != 0L && System.currentTimeMillis() > lastPacket + time.getValDouble()) {
            if(canSend) {
                canSend = false;
                mc.player.sendChatMessage(text.getValString());
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        lastPacket = 0L;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        lastPacket = System.currentTimeMillis();
    });
}
