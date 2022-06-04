package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import com.kisman.cc.module.movement.ElytraFly;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;

public class ElytraEquip extends Module {
    private final Setting synsWithElytraFly = new Setting("Syns With ElytraFly", this, false);
    private final Setting autoDisable = new Setting("Auto Disable", this, true);

    public static ElytraEquip instance;
    private State state = State.NeedElytra;

    public ElytraEquip() {
        super("ElytraEquip", Category.PLAYER);

        instance = this;

        setmgr.rSetting(synsWithElytraFly);
        setmgr.rSetting(autoDisable);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        state = mc.player != null ? mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA ? State.NeedChest : State.NeedElytra : State.None;
    }

    public void update() {
        if(state != State.None || mc.player == null || mc.world == null) return;
        int slot;
        if(mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) slot = InventoryUtil.findItem(Items.ELYTRA, 0, 36);
        else slot = InventoryUtil.findChestplate(0, 36);
        if(slot == -1) super.setToggled(false);
        boolean armorOnChest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
        if(armorOnChest) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
        state = State.None;
        if(autoDisable.getValBoolean()) super.setToggled(false);
    }

    public void updateState() {
        if(synsWithElytraFly.getValBoolean()) {
            if(ElytraFly.instance.isToggled()) state = State.NeedElytra;
            else state = State.NeedChest;
        }
    }

    public enum State {NeedElytra, NeedChest, None}
}