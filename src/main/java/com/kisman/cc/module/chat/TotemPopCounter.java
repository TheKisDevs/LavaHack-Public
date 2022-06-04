package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TotemPopCounter extends Module {
    private Setting target = new Setting("Target", this, TargetMode.Both);
    public TotemPopCounter() {
        super("TotemPopCounter", "totem pops count!", Category.CHAT);

        setmgr.rSetting(target);
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if(event.getPopEntity() instanceof EntityPlayer) {
            boolean isFriend = Kisman.instance.friendManager.isFriend((EntityPlayer) event.getPopEntity());
            if(isFriend && target.getValString().equals("Only Other Players")) return;
            if(!isFriend && target.getValString().equals("Only Friends")) return;

            ChatUtils.warning((isFriend ? TextFormatting.AQUA : TextFormatting.GRAY) + event.getPopEntity().getName() + TextFormatting.GRAY + " was popped totem!");
        }
    }

    public enum TargetMode {
        Friend("Only Friends"),
        OtherPLayers("Only Other Players"),
        Both("Both");

        public String name;

        TargetMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
