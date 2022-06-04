package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean raytraceBlock(BlockPos blockPos, Raytrace raytrace) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + raytrace.getOffset(), blockPos.getZ() + 0.5), false, true, false) != null;
    }

    public static boolean raytraceEntity(Entity entity, double offset) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entity.posX, entity.posY + offset, entity.posZ), false, true, false) == null;
    }

    public enum Raytrace {
        NONE(-1), BASE(0.5), NORMAL(1.5), DOUBLE(2.5), TRIPLE(3.5);

        private final double offset;

        Raytrace(double offset) {
            this.offset = offset;
        }

        public double getOffset() {
            return offset;
        }
    }
}
