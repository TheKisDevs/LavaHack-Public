package com.kisman.cc.module.combat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class CrystalBasePlace extends Module {
    private final Setting delay = new Setting("Delay", this, 10, 0, 10000, Slider.NumberType.TIME);
    private final Setting placeRange = new Setting("Place Range", this, 5, 1, 6, false);
    private final Setting targetRange = new Setting("Target Range", this, 15, 5, 20, false);
    private final Setting useAutoRerTarget = new Setting("Use Auto Rer Target", this, false);
    private final Setting switchMode = new Setting("Switch Mode", this, SwitchMode.Silent);
    private final Setting ignoreY = new Setting("Ignore Y", this, false);
    private final Setting minDmg = new Setting("Min DMG", this, 15, 0, 37, true);
    private final Setting packet = new Setting("Packet", this, false);

    public static CrystalBasePlace instance;

    private final TimerUtils delayTimer = new TimerUtils();
    private EntityPlayer target;
    private BlockPos pos;

    public CrystalBasePlace() {
        super("CrystalBasePlace", Category.COMBAT);

        instance = this;

        setmgr.rSetting(delay);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(useAutoRerTarget);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(ignoreY);
        setmgr.rSetting(minDmg);
        setmgr.rSetting(packet);
    }

    public void onEnable() {
        delayTimer.reset();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        target = useAutoRerTarget.getValBoolean() ? AutoRer.currentTarget : EntityUtil.getTarget(targetRange.getValFloat());
        if(target == null) return;
        else super.setDisplayInfo("[" + target.getName() + "]");
        pos = getAbsBlockWithMaxDamageForTarget();
        if(pos == null) return;
        if(delayTimer.passedMillis(delay.getValLong())) {
            int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9), oldSlot = mc.player.inventory.currentItem;

            if(switchMode.getValString().equalsIgnoreCase(SwitchMode.None.name()) && obbySlot == -1) return;
            else if(!switchMode.getValString().equalsIgnoreCase(SwitchMode.None.name())) InventoryUtil.switchToSlot(obbySlot, switchMode.getValString().equalsIgnoreCase(SwitchMode.Silent.name()));
            BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, packet.getValBoolean());
            if(switchMode.getValString().equalsIgnoreCase(SwitchMode.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
            delayTimer.reset();
        }
    }

    private BlockPos getAbsBlockWithMaxDamageForTarget() {
        BlockPos posToReturn = null;
        BlockPos targetFlooredPos = PlayerUtil.GetLocalPlayerPosFloored();

        double maxDamage = 0.5;
        for(BlockPos pos : CrystalUtils.getSphere(target, placeRange.getValFloat(), true, false)) {
            if(mc.world.getBlockState(pos).getBlock() != Blocks.AIR) continue;
            if(!BlockUtil.canPlaceBlock(pos)) continue;
            if(targetFlooredPos.getX() == pos.getX() && targetFlooredPos.getZ() == targetFlooredPos.getZ()) continue;
            if(!ignoreY.getValBoolean() && targetFlooredPos.getY() != pos.getY()) continue;
            double damage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, target, false);
            if(damage > minDmg.getValInt() && damage > maxDamage) {
                posToReturn = pos;
                maxDamage = damage;
            }
        }

        return posToReturn;
    }

    public enum SwitchMode {None, Normal, Silent}
}
