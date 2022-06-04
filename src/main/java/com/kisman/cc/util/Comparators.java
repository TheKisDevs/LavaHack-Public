package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

/**
 * @author Tigermouthbear 5/3/20
 */
public class Comparators {
    public static final EntityDistance entityDistance = new EntityDistance();
    public static final BlockDistance blockDistance = new BlockDistance();

    private static class EntityDistance implements Comparator<Entity> {
        private static Minecraft mc = Minecraft.getMinecraft();

        @Override
        public int compare(Entity p1, Entity p2) {
            final double one = Math.sqrt(mc.player.getDistance(p1));
            final double two = Math.sqrt(mc.player.getDistance(p2));
            return Double.compare(one, two);
        }
    }

    private static class BlockDistance implements Comparator<BlockPos> {
        private static Minecraft mc = Minecraft.getMinecraft();

        @Override
        public int compare(BlockPos pos1, BlockPos pos2) {
            final double one = Math.sqrt(mc.player.getDistanceSq(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5));
            final double two = Math.sqrt(mc.player.getDistanceSq(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5));
            return Double.compare(one, two);
        }
    }
}
