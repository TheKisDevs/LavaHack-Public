package com.kisman.cc.util.bypasses;

import com.kisman.cc.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;

public class SilentSwitchBypass {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final int oldSlot, newSlot;

    public SilentSwitchBypass(Item item) {
        this.oldSlot = mc.player.inventory.currentItem + 36;
        this.newSlot = InventoryUtil.findItemInInventory(item);
    }

    public void doSwitch() {
        if(mc.currentScreen != null || newSlot == -1 || oldSlot == -1) return;

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, newSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, oldSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, newSlot, 0, ClickType.PICKUP, mc.player);
    }
}
