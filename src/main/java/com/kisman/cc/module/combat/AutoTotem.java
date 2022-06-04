package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", "simple offhand", Category.COMBAT);

        Kisman.instance.settingsManager.rSetting(new Setting("Health", this, 10, 1, 20, true));
    }

    public boolean isBeta() {return true;}

    public void update() {
        if(mc.player == null && mc.world == null) return;

        int totemSlot = 0;

        NonNullList<ItemStack> inv;
        ItemStack offhand = mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        int inventoryIndex;
        inv = mc.player.inventory.mainInventory;

        int health = (int) Kisman.instance.settingsManager.getSettingByName(this, "Health").getValDouble();

        for(inventoryIndex = 0; inventoryIndex < inv.size(); inventoryIndex++) {
            if(inv.get(inventoryIndex) != ItemStack.EMPTY) {
                if(inv.get(inventoryIndex).getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSlot = inventoryIndex;
                }
            }
        }

        if(mc.player.getHealth() + mc.player.getAbsorptionAmount() < health) {
            if(offhand == null || offhand.getItem() == Items.TOTEM_OF_UNDYING) {
                return;
            }

            if(totemSlot != 0) {
                mc.playerController.windowClick(0, totemSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0,45,0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, totemSlot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }
}
