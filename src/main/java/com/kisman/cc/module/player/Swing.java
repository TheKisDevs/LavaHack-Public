package com.kisman.cc.module.player;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

public class Swing extends Module {
    private Setting mode = new Setting("Mode", this, Hand.MAINHAND);

    public Swing() {
        super("Swing", "swing", Category.PLAYER);

        setmgr.rSetting(mode);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(mode.getValString().equals(Hand.MAINHAND.name())) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        } else if(mode.getValString().equals(Hand.OFFHAND.name())) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        } else if(mode.getValString().equals(Hand.PACKETSWING.name()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }

    public enum Hand {
        OFFHAND,
        MAINHAND,
        PACKETSWING
    }
}
