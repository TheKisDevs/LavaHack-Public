package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;

public class SelfTrap extends AutoTrap {
    public SelfTrap() {
        super("SelfTrap", Category.COMBAT);

        setmgr.rSetting(disableOnComplete);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(rotate);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(antiScaffold);
        setmgr.rSetting(antiStep);
        setmgr.rSetting(surroundPlacing);
        setmgr.rSetting(range);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(packet);
        setmgr.rSetting(rewrite);
        setmgr.rSetting(dynamic);
        setmgr.rSetting(supportBlocks);
        setmgr.rSetting(rewriteRetries);
        setmgr.rSetting(rotateMode);
    }

    @Override
    protected void doRewriteTrap() {
        target = mc.player;

        int blockSlot;
        int oldSlot = mc.player.inventory.currentItem;
        if(InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else if(InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else return;

        InventoryUtil.switchToSlot(blockSlot, switch_.getValString().equalsIgnoreCase("Silent"));
        for(BlockPos pos : getPosList()) {
            if(!BlockUtil2.isPositionPlaceable(pos, true, true, tries <= rewriteRetries.getValInt())) continue;
            place(pos);
            tries++;
        }
        rewrPlacements = 0;
        if(switch_.getValString().equalsIgnoreCase(RewriteSwitchModes.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
        if(!getPosList().isEmpty()) return;
        tries = 0;
        if(disableOnComplete.getValBoolean()) setToggled(false);
    }

    @Override
    protected boolean check() {
        if(mc.player == null || startPos == null) return false;

        didPlace = false;
        placements = 0;
        final int obbySlot2 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (obbySlot2 == -1) setToggled(false);
        final int obbySlot3 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (!super.isToggled()) return true;
        if (!startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            setToggled(false);
            return true;
        }
        if (retryTimer.passedMillis(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot3 == -1) {
            ChatUtils.error(ChatFormatting.RED + "No Obsidian in hotbar, SelfTrap disabling...");
            setToggled(false);
            return true;
        }
        if (mc.player.inventory.currentItem != this.oldSlot && mc.player.inventory.currentItem != obbySlot3) this.oldSlot = mc.player.inventory.currentItem;
        isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        target = mc.player;
        return target == null || !timer.passedMillis(placeDelay.getValInt());
    }
}
