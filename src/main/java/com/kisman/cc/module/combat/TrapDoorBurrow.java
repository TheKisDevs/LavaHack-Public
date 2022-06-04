package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.Objects;

import static com.kisman.cc.util.BlockUtil.getPlaceableSide;

public class TrapDoorBurrow extends Module {
    private final Setting fallSpeed = new Setting("Fall Speed", this, 1, 1, 20, false);
    private final Setting instant = new Setting("Instant", this, false);
    private final Setting instantFactor = new Setting("Instant Factor", this, 20, 1.1, 20, false).setVisible(instant::getValBoolean);

    private BlockPos pos;
    private boolean opened = false;

    public TrapDoorBurrow() {
        super("TrapDoorBurrow", Category.COMBAT);

        setmgr.rSetting(fallSpeed);
        setmgr.rSetting(instant);
        setmgr.rSetting(instantFactor);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null || mc.world.getBlockState(pos).getBlock().equals(Blocks.TRAPDOOR)) {
            super.setToggled(false);
            return;
        }
        opened = false;
        mc.player.motionX = ((Math.floor(mc.player.posX) + 0.5) - mc.player.posX) / 2;
        mc.player.motionZ = ((Math.floor(mc.player.posZ) + 0.5) - mc.player.posZ) / 2;
        pos = mc.player.getPosition();
        if(mc.player.onGround) mc.player.jump();
    }

    public void onDisable() {
        EntityUtil.resetTimer();
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(instant.getValBoolean()) EntityUtil.setTimer(instantFactor.getValFloat());
        if(!mc.world.getBlockState(pos).getBlock().equals(Blocks.TRAPDOOR)) {
            int trapDoorSlot = InventoryUtil.findBlock(Blocks.TRAPDOOR, 0, 9), oldSlot = mc.player.inventory.currentItem;
            if(trapDoorSlot == -1) {
                ChatUtils.error("[TrapDoorBurrow] Cant found trap door in your hotbar");
                super.setToggled(false);
                return;
            }
            InventoryUtil.switchToSlot(trapDoorSlot, false);
//            float oldPitch = mc.player.rotationPitch;
//            mc.player.rotationPitch = 90;
            placeBlock(pos, EnumHand.MAIN_HAND);
            InventoryUtil.switchToSlot(oldSlot, false);
        } else {
            Vec3d facing = new Vec3d(pos).add(new Vec3d(0.5, 1, 0.5));
            if(!opened) {
                interactTrapdoor(facing);
                ChatUtils.complete("[TrapDoorBurrow] Done!");
                super.setToggled(false);
                opened = true;
            } else if(mc.player.onGround) {
                interactTrapdoor(facing);
                ChatUtils.complete("[TrapDoorBurrow] Done!");
                super.setToggled(false);
            } else mc.player.motionY = -fallSpeed.getValDouble();
        }
    }

    protected void interactTrapdoor(Vec3d vec) {
        mc.player.	rotationPitch = 90.0f;
        mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, vec, EnumHand.MAIN_HAND);
    }

    public static boolean placeBlock(BlockPos position, EnumHand hand) {
        if (!mc.world.getBlockState(position).getBlock().isReplaceable(mc.world, position)) return false;
        if (getPlaceableSide(position) == null) return false;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position, Objects.requireNonNull(getPlaceableSide(position)).getOpposite(), hand, 0.5f, 0, 0.5f));
        mc.player.connection.sendPacket(new CPacketAnimation(hand));
        return true;
    }

    public boolean isVisible() {return false;}
}
