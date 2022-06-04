package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * @author Halq
 * @apiNote this is not AI
 * @since 28/02/2022 20:32PM
 */

public class AI {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static AI instance = new AI();

    static class HalqPos {
        BlockPos blockPos;
        float targetDamage;

        public HalqPos(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public float getTargetDamage() {
            return targetDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }
    }

    public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));

        if (placePosition.getY() == 255)
            return EnumFacing.DOWN;

        if (rayTrace) {
            return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        }

        return EnumFacing.UP;
    }

}
