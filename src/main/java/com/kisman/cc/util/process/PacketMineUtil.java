package com.kisman.cc.util.process;

import com.kisman.cc.event.events.*;
import com.kisman.cc.util.*;

import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PacketMineUtil implements Globals {
    private TimerUtils timer = new TimerUtils();
    public BlockPos currentPos;
    private long start;
    private int oldSlot, rebreakCount, instantAttempts, packetSpam, range;
    private boolean silent, instant, strictCheck, swap, checked, strict, autoSwitch;
    private double speed;

    public void update(double speed, int range, BlockPos currentPos, boolean autoSwitch, int oldSlot, int rebreakCount, int instantAttempts, boolean silent, boolean instant, int packetSpam, boolean strictCheck, boolean strict) {
        this.speed = speed;
        this.range = range;
        this.currentPos = currentPos;
        this.autoSwitch = autoSwitch;
        this.oldSlot = oldSlot;
        this.rebreakCount = rebreakCount;
        this.instantAttempts = instantAttempts;
        this.silent = silent;
        this.instant = instant;
        this.packetSpam = packetSpam;
        this.strictCheck = strictCheck;
        this.strict = strict;
    }

    @EventHandler
    private final Listener<EventDamageBlock> dmg = new Listener<>(event -> {
        if (swap) {
            event.cancel();
            return;
        }

        if (!BlockUtil.canBlockBeBroken(event.getBlockPos())) return;
        if (currentPos != null) {
            if (event.getBlockPos().toLong() == currentPos.toLong()) {
                if (!swap && getBlockProgress(currentPos, mc.player.inventory.getStackInSlot(InventoryUtil.findBestToolSlot(currentPos)), start) <= 1 - speed && mc.world.getBlockState(currentPos).getBlock() != Blocks.AIR) {
                    if (silent) swapTo();
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
                    event.cancel();
                }
                return;
            }

            if (event.getBlockPos().toLong() != currentPos.toLong()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, event.getFaceDirection()));
                mc.playerController.isHittingBlock = false;
            }
        }

        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        for (int j = 0; j < packetSpam; j++) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getBlockPos(), event.getFaceDirection()));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getBlockPos(), EnumFacing.DOWN));
        currentPos = event.getBlockPos();
        start = System.currentTimeMillis();
        strictCheck = true;
        timer.reset();
        event.cancel();
    });

    @EventHandler
    private final Listener<EventPlayerMove> listener = new Listener<>(event -> {
        if (currentPos != null) {
            if (instant) {
                if (mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
                    if (!checked) {
                        rebreakCount = 0;
                        checked = true;
                        start = System.currentTimeMillis();
                        timer.reset();
                        strictCheck = false;
                    }
                } else {
                    if (strict && !strictCheck) {
                        Block block = mc.world.getBlockState(currentPos).getBlock();
                        if (!(block.equals(Blocks.ENDER_CHEST) || block.equals(Blocks.ANVIL) || block.equals(Blocks.AIR))) {
                            rebreakCount = 0;
                            currentPos = null;
                            timer.reset();
                            strictCheck = true;
                            return;
                        }
                    }
                    checked = false;
                }
            }

            if (getBlockProgress(currentPos, mc.player.inventory.getStackInSlot(InventoryUtil.findBestToolSlot(currentPos)), start) <= 1 - speed && mc.world.getBlockState(currentPos).getBlock() != Blocks.AIR) if (autoSwitch) if(!swapTo()) return;
            if (!swap) oldSlot = mc.player.inventory.currentItem;
            if (currentPos != null && mc.player.getDistanceSq(currentPos) >= MathUtil.square(range)) currentPos = null;
        }
        try {mc.playerController.blockHitDelay = 0;} catch (Exception ignored) {}
    });
    
    public float getBlockProgress(BlockPos blockPos, ItemStack stack, long start) {
        return (float) MathUtil.clamp(1 - ((System.currentTimeMillis() - start) / (double) InventoryUtil.time(blockPos, stack)), 0, 1);
    }

    public boolean swapTo() {
        if (rebreakCount > instantAttempts - 1 && instantAttempts != 0) {
            currentPos = null;
            rebreakCount = 0;
            return false;
        }
        InventoryUtil.switchToSlot(InventoryUtil.findBestToolSlot(currentPos), false);
        if (silent) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
            rebreakCount++;
            if (!instant) currentPos = null;
            InventoryUtil.switchToSlot(oldSlot, false);
        } else swap = true;
        return true;
    }
}
