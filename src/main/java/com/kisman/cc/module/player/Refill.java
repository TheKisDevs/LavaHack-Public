package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.init.*;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;

public class Refill extends Module {
    private final Setting[] settings = new Setting[]{
            new Setting("One", this, ItemMode.None),
            new Setting("Two", this, ItemMode.None),
            new Setting("Three", this, ItemMode.None),
            new Setting("Four", this, ItemMode.None),
            new Setting("Five", this, ItemMode.None),
            new Setting("Six", this, ItemMode.None),
            new Setting("Seven", this, ItemMode.None),
            new Setting("Eight", this, ItemMode.None),
            new Setting("Nine", this, ItemMode.None)
    };

    private final Slot[] slots = new Slot[] {
            new Slot(36, null),
            new Slot(37, null),
            new Slot(38, null),
            new Slot(39, null),
            new Slot(40, null),
            new Slot(41, null),
            new Slot(42, null),
            new Slot(43, null),
            new Slot(44, null)
    };

    public Refill() {
        super("Refill", Category.PLAYER);

        setmgr.rSetting(settings[0]);
        setmgr.rSetting(settings[1]);
        setmgr.rSetting(settings[2]);
        setmgr.rSetting(settings[3]);
        setmgr.rSetting(settings[4]);
        setmgr.rSetting(settings[5]);
        setmgr.rSetting(settings[6]);
        setmgr.rSetting(settings[7]);
        setmgr.rSetting(settings[8]);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        for(int i = 0; i < slots.length; i++) {
            if(getItemFromValue(settings[i]) != null) {
                slots[i].item = getItemFromValue(settings[i]);
                slots[i].updateRequirestSlot();

                if (slots[i].item != null && slots[i].requirestSlot != -1 && mc.currentScreen == null && slots[i].slot != -1 && mc.player.inventoryContainer.getInventory().get(slots[i].slot).getItem() != slots[i].item) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slots[i].requirestSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slots[i].slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slots[i].requirestSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.updateController();
                }
            }
        }
    }

    private Item getItemFromValue(Setting set) {
        switch (set.getValString()) {
            case "Crystal": return Items.END_CRYSTAL;
            case "Pearl": return Items.ENDER_PEARL;
            case "Totem": return Items.TOTEM_OF_UNDYING;
            case "Sword": return Items.DIAMOND_SWORD;
            case "Pickaxe": return Items.DIAMOND_PICKAXE;
            case "Axe": return Items.DIAMOND_AXE;
            case "Strength": return Items.POTIONITEM;
            case "Chorus": return Items.CHORUS_FRUIT;
            case "Shield": return Items.SHIELD;
            case "Gapple": return Items.GOLDEN_APPLE;
            case "RedstoneBlock": return ItemBlock.getItemFromBlock(Blocks.REDSTONE_BLOCK);
            case "Piston": return ItemBlock.getItemFromBlock(Blocks.PISTON);
            case "Obsidian": return ItemBlock.getItemFromBlock(Blocks.OBSIDIAN);
            case "EnderChest": return ItemBlock.getItemFromBlock(Blocks.ENDER_CHEST);
            case "Exp": return Items.EXPERIENCE_BOTTLE;
            default: return null;
        }
    }

    public enum ItemMode {
        None,
        Crystal,
        Pearl,
        Totem,
        Sword,
        Pickaxe,
        Axe,
        Strength,
        Chorus,
        Shield,
        Gapple,
        RedstoneBlock,
        Piston,
        Obsidian,
        EnderChest,
        Exp
    }

    public static class Slot {
        public final int slot;
        public int requirestSlot = -1;
        public Item item;

        public Slot(int slot, Item item) {
            this.slot  = slot;
            this.item = item;
        }

        public void updateRequirestSlot() {
            if(item != null) requirestSlot = InventoryUtil.findItemInInventory(item);
        }
    }
}
