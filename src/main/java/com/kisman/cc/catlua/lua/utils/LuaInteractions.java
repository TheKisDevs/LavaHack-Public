package com.kisman.cc.catlua.lua.utils;

import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.BlockUtil2;
import com.kisman.cc.util.Globals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class LuaInteractions implements Globals {

    private static LuaInteractions instance;

    LuaInteractions() {
        super();
    }

    public static LuaInteractions getDefault() {
        if (instance == null) instance = new LuaInteractions();
        return instance;
    }

    public boolean canBreak(BlockPos blockPos) {
        return BlockUtil.canBlockBeBroken(blockPos);
    }

    public boolean isPlaceable(BlockPos pos, boolean entityCheck) {
        return isPlaceable(pos, true, entityCheck);
    }

    public boolean isPlaceable(BlockPos pos, boolean sideCheck, boolean entityCheck) {
        return BlockUtil2.isPositionPlaceable(pos, sideCheck, entityCheck);
    }

    public boolean breakBlockPacket(BlockPos pos) {
        if (!canBreak(pos)) return false;
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
        return true;
    }

    public boolean breakBlock(BlockPos pos) {
        if (!canBreak(pos)) return false;
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
        return true;
    }

    public void useItem(BlockPos pos, EnumHand hand) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) pos.getX() + 0.5, ( double ) pos.getY() - 0.5, ( double ) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, EnumHand.MAIN_HAND, 0, 0, 0));
    }

    public void useItem(BlockPos pos) {
        useItem(pos, EnumHand.MAIN_HAND);
    }

    public void useItem(EnumHand hand) {
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(hand));
    }

    public boolean place(BlockPos pos, boolean airPlace) {
        return place(pos, airPlace, EnumHand.MAIN_HAND);
    }

    public boolean place(BlockPos pos, boolean packet, EnumHand hand) {
        return BlockUtil2.placeBlock(pos, hand, packet);
    }

    public LuaDirection calcSide(BlockPos pos) {
        for (LuaDirection d : LuaDirection.values())
            if (!mc.world.getBlockState(pos.add(d.vec)).getMaterial().isReplaceable()) return d;
        return null;
    }

    public LuaDirection right(LuaDirection direction) {
        switch (direction) {
            case EAST: return LuaDirection.SOUTH;
            case SOUTH: return LuaDirection.WEST;
            case WEST: return LuaDirection.NORTH;
            case NORTH: return LuaDirection.EAST;
            default: throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    public LuaDirection left(LuaDirection direction) {
        switch (direction) {
            case EAST: return LuaDirection.NORTH;
            case NORTH: return LuaDirection.WEST;
            case WEST: return LuaDirection.SOUTH;
            case SOUTH: return LuaDirection.EAST;
            default: throw new IllegalStateException("Unexpected value: " + direction);
        }
    }
}
