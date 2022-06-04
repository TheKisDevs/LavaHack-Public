package com.kisman.cc.util.process;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import com.kisman.cc.module.combat.Surround;
import com.kisman.cc.util.*;

public class DynamicTrapUtil implements Globals {
    public EntityPlayer target = null;
    public boolean dynamic, packet, surroundPlacing, antiStep;
    private int placement = 0;
    public int blocksPerTick = 0, tries, rewriteRetries;
    public String rotateMode, supportBlocks, switch_;
    public boolean canTopTrap = false;

    public void update(EntityPlayer target, boolean dynamic, boolean packet, String rotateMode, String supportBlocks, boolean surroundPlacing, boolean antiStep, int rewriteRetries, String switch_, boolean canTopTrap) {
        this.target = target;
        this.dynamic = dynamic;
        this.packet = packet;
        this.rotateMode = rotateMode;
        this.supportBlocks = supportBlocks;
        this.surroundPlacing = surroundPlacing;
        this.antiStep = antiStep;
        this.rewriteRetries = rewriteRetries;
        this.switch_ = switch_;
        this.canTopTrap = canTopTrap;
    }

    public void trapProcess() {
        if(target == null) return;

        int blockSlot;
        int oldSlot = mc.player.inventory.currentItem;
        if(InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else if(InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else return;

        InventoryUtil.switchToSlot(blockSlot, switch_.equalsIgnoreCase("Silent"));
        for(BlockPos pos : getPosList()) {
            if(!BlockUtil2.isPositionPlaceable(pos, true, true, tries <= rewriteRetries)) continue;
            place(pos);
            tries++;
        }
        placement = 0;
        if(switch_.equalsIgnoreCase(RewriteSwitchModes.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
        if(!getPosList().isEmpty()) return;
        tries = 0;
    }

    private List<BlockPos> getUnsafeBlocks() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        for(BlockPos pos :  getOffsets()) {
            if(isSafe(pos)) continue;
            positions.add(pos);
        }
        return positions;
    }

    private boolean isSafe(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos);
    }

    private List<BlockPos> getOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        if (dynamic) {
            int z;
            int x;
            double decimalX = Math.abs(target.posX) - Math.floor(Math.abs(target.posX));
            double decimalZ = Math.abs(target.posZ) - Math.floor(Math.abs(target.posZ));
            int lengthX = calculateLength(decimalX, false);
            int negativeLengthX = calculateLength(decimalX, true);
            int lengthZ = calculateLength(decimalZ, false);
            int negativeLengthZ = calculateLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<>();
            offsets.addAll(getOverlapPositions());
            for (x = 1; x < lengthX + 1; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
            }
            for (x = 0; x <= negativeLengthX; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
            }
            for (z = 1; z < lengthZ + 1; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
            }
            for (z = 0; z <= negativeLengthZ; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
            }
            offsets.addAll(tempOffsets);
        } else for (EnumFacing side : EnumFacing.HORIZONTALS) offsets.add(getPlayerPosition().add(side.getFrontOffsetX(), 0, side.getFrontOffsetZ()));
        return offsets;
    }

    private ArrayList<BlockPos> getPosList() {
        List<BlockPos> startPosList = getUnsafeBlocks();
        ArrayList<BlockPos> finalPosList = new ArrayList<>();

        for(BlockPos pos : startPosList) {
            if(!supportBlocks.equalsIgnoreCase(Surround.SupportModes.None.name())) if(BlockUtil.getPlaceableSide(pos) == null || supportBlocks.equalsIgnoreCase(Surround.SupportModes.Static.name()) && BlockUtil2.isPositionPlaceable(pos, true, true)) finalPosList.add(pos.down());
            if(surroundPlacing) finalPosList.add(pos);
        }

        for(BlockPos pos : getAroundOffset()) {
            finalPosList.add(pos.up());
            if(antiStep) finalPosList.add(pos.up().up());
        }

        for(BlockPos pos : getOverlapPositions()) {
            if(antiStep && canTopTrap) finalPosList.add(pos.up().up().up());
        }

        return  finalPosList;
    }

    private void place(BlockPos posToPlace) {
        if(placement < blocksPerTick) {
            float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
            if(!rotateMode.equalsIgnoreCase(RewriteRotateModes.None.name())) {
                float[] rots = RotationUtils.getRotationToPos(posToPlace);
                mc.player.rotationYaw = rots[0];
                mc.player.rotationPitch = rots[1];
            }
            BlockUtil2.placeBlock(posToPlace, EnumHand.MAIN_HAND, packet);
            placement++;
            if(rotateMode.equalsIgnoreCase(RewriteRotateModes.Silent.name())) {
                mc.player.rotationYaw = oldRots[0];
                mc.player.rotationPitch = oldRots[1];
            }
        }
    }

    private List<BlockPos> getAroundOffset() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        if (dynamic) {
            int z;
            int x;
            double decimalX = Math.abs(target.posX) - Math.floor(Math.abs(target.posX));
            double decimalZ = Math.abs(target.posZ) - Math.floor(Math.abs(target.posZ));
            int lengthX = calculateLength(decimalX, false);
            int negativeLengthX = calculateLength(decimalX, true);
            int lengthZ = calculateLength(decimalZ, false);
            int negativeLengthZ = calculateLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<>();
            for (x = 1; x < lengthX + 1; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
            }
            for (x = 0; x <= negativeLengthX; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
            }
            for (z = 1; z < lengthZ + 1; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
            }
            for (z = 0; z <= negativeLengthZ; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
            }
            offsets.addAll(tempOffsets);
        } else for (EnumFacing side : EnumFacing.HORIZONTALS) offsets.add(getPlayerPosition().add(side.getFrontOffsetX(), 0, side.getFrontOffsetZ()));
        return offsets;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(target.posX, target.posY - Math.floor(target.posY) > Double.longBitsToDouble(Double.doubleToLongBits(19.39343307331816) ^ 0x7FDAFD219E3E896DL) ? Math.floor(target.posY) + Double.longBitsToDouble(Double.doubleToLongBits(4.907271931218261) ^ 0x7FE3A10BE4A4A510L) : Math.floor(target.posY), target.posZ);
    }

    private List<BlockPos> getOverlapPositions() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        int offsetX = calculateOffset(target.posX - Math.floor(target.posX));
        int offsetZ = calculateOffset(target.posZ - Math.floor(target.posZ));
        positions.add(getPlayerPosition());
        for (int x = 0; x <= Math.abs(offsetX); ++x) {
            for (int z = 0; z <= Math.abs(offsetZ); ++z) {
                int properX = x * offsetX;
                int properZ = z * offsetZ;
                positions.add(getPlayerPosition().add(properX, -1, properZ));
            }
        }
        return positions;
    }

    private int calculateOffset(double dec) {
        return dec >= Double.longBitsToDouble(Double.doubleToLongBits(22.19607388697261) ^ 0x7FD05457839243F9L) ? 1 : (dec <= Double.longBitsToDouble(Double.doubleToLongBits(7.035587642812949) ^ 0x7FCF1742257B24DBL) ? -1 : 0);
    }

    private int calculateLength(double decimal, boolean negative) {
        if (negative) return decimal <= Double.longBitsToDouble(Double.doubleToLongBits(30.561776836994962) ^ 0x7FEDBCE3A865B81CL) ? 1 : 0;
        return decimal >= Double.longBitsToDouble(Double.doubleToLongBits(22.350511399288944) ^ 0x7FD03FDD7B12B45DL) ? 1 : 0;
    }

    private BlockPos addToPosition(BlockPos pos, double x, double z) {
        block1: {
            if (pos.getX() < 0) x = -x;
            if (pos.getZ() >= 0) break block1;
            z = -z;
        }
        return pos.add(x, Double.longBitsToDouble(Double.doubleToLongBits(1.4868164896774578E308) ^ 0x7FEA7759ABE7F7C1L), z);
    }

    public enum RewriteSwitchModes {Normal, Silent}
    public enum RewriteSupportModes {None, Dynamic, Static}
    public enum RewriteRotateModes {None, Normal, Silent}
}
