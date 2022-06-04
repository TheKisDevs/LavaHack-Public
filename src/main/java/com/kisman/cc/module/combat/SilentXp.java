package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.Arrays;

public class SilentXp extends Module {
    private final Setting lookPitch = new Setting("LookPitch", this, 90, 0, 100, false);
    private final Setting switchMode = new Setting("SwitchMode", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Client")));

    public SilentXp() {
        super("SilentXP", "SilentXp", Category.COMBAT);

        setmgr.rSetting(lookPitch);
        setmgr.rSetting(switchMode);
    }

    public void update() {
        if(mc.currentScreen == null && mc.player != null && mc.world != null) {
            int oldPitch = (int)mc.player.rotationPitch;
            int oldSlot = mc.player.inventory.currentItem;

            switch (switchMode.getValString()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9)));
                    break;
                case "Client":
                    mc.player.inventory.currentItem = InventoryUtil.findItem(Items.EXPERIENCE_BOTTLE, 0, 9);
                    break;
            }

            mc.player.rotationPitch = (float) lookPitch.getValDouble();
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, (float) lookPitch.getValDouble(), true));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.rotationPitch = oldPitch;

            switch (switchMode.getValString()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    break;
                case "Client":
                    mc.player.inventory.currentItem = oldSlot;
                    break;
            }
        }
    }
}
