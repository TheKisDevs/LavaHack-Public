package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.InventoryUtil;
import com.kisman.cc.util.manager.RotationManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

public class AntiBow extends Module {
    private Setting packet = new Setting("Packet", this, false);
    private Setting range = new Setting("Range", this, 40, 0, 40, false);
    private Setting checkUse = new Setting("CheckUse", this, false);
    private Setting maxUse = new Setting("MaxUse", this, 10, 0, 20, true);
    private Setting bowInHandCheck = new Setting("BowInHandCheck", this, true);

    private boolean bool;

    public AntiBow() {
        super("AntiBow", Category.COMBAT);

        setmgr.rSetting(packet);
        setmgr.rSetting(range);
        setmgr.rSetting(checkUse);
        setmgr.rSetting(maxUse);
        setmgr.rSetting(bowInHandCheck);
    }

    public void onEnable() {
        bool = false;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        EntityPlayer target = EntityUtil.getTarget(range.getValFloat());
        int oldSlot = -1;
        int shieldSlot = InventoryUtil.findItem(Items.SHIELD, 0, 9);

        if(target == null) {
            if(bool) {
                mc.gameSettings.keyBindUseItem.pressed = false;

                if(oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);

                bool = false;
            }
        } else {
            if(shieldSlot == -1) {
                target = null;
                return;
            }

            oldSlot = mc.player.inventory.currentItem;

            if(bowInHandCheck.getValBoolean()) {
                if(!target.getHeldItemMainhand().getItem().equals(Items.BOW)) {
                    return;
                }
            }

            if(checkUse.getValBoolean()) {
                if(target.getItemInUseMaxCount() <= maxUse.getValDouble()) {
                    return;
                }
            }

            if(!mc.player.getHeldItemMainhand().getItem().equals(Items.SHIELD)) InventoryUtil.switchToSlot(shieldSlot, true);

            mc.gameSettings.keyBindUseItem.pressed = true;
            RotationManager.look(target, packet.getValBoolean());
            bool = true;
        }
    }
}
