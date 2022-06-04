package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;

public class NameProtect extends Module {
    private Setting name = new Setting("Name", this, "Kisman", "Kisman", true);

    public static NameProtect instance;

    public NameProtect() {
        super("NameProtect", "NameProtect", Category.MISC);

        instance = this;

        setmgr.rSetting(name);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        SPacketChat packet;
        if (event.getPacket() instanceof SPacketChat && (packet = (SPacketChat)event.getPacket()).getType() != ChatType.GAME_INFO && getChatNames(packet.getChatComponent().getFormattedText())) event.cancel();
    });

    private boolean getChatNames(String message) {
        if (mc.player == null) return false;
        String out = message;
        out = out.replace(mc.player.getName(), name.getValString());
        ChatUtils.simpleMessage(out);
        return true;
    }
}
