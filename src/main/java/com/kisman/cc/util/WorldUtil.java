package com.kisman.cc.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class WorldUtil {
    public static String vectorToString(Vec3d vector, boolean... includeY) {
        boolean reallyIncludeY = includeY.length <= 0 || includeY[0];
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append((int) Math.floor(vector.x));
        builder.append(", ");
        if(reallyIncludeY) {
            builder.append((int) Math.floor(vector.y));
            builder.append(", ");
        }
        builder.append((int) Math.floor(vector.z));
        builder.append(")");
        return builder.toString();
    }

    public static float getDistance(Entity entityOut, BlockPos pos) {
        float f = (float)(entityOut.posX - pos.getX());
        float f1 = (float)(entityOut.posY - pos.getY());
        float f2 = (float)(entityOut.posZ - pos.getZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    /**
     * Excluding y
     */
    public static List<BlockPos> getBlocks(BlockPos fromPos, BlockPos toPos, boolean connect, boolean outside){
        List<BlockPos> positions = new ArrayList<>();
        double xDiff = fromPos.getX() - toPos.getX();
        double zDiff = fromPos.getZ() - toPos.getZ();
        double d = xDiff > zDiff ? zDiff / xDiff : xDiff / zDiff;
        double a = 0.5;
        int last = (int) (d * a);
        if(xDiff > zDiff){
            for(int i = 0; i <= ((int) xDiff); i++){
                double delta = d * a;
                if(connect && (int) delta > last){
                    if(outside){
                        positions.add(new BlockPos(fromPos.add(i, 0, last)));
                    } else {
                        positions.add(new BlockPos(fromPos.add(i - 1, 0, delta)));
                    }
                }
                BlockPos pos = new BlockPos(fromPos.add(i, 0, delta));
                positions.add(pos);
                last = (int) delta;
                a += 1.0;
            }
        } else {
            for(int i = 0; i <= ((int) zDiff); i++){
                double delta = d * a;
                if(connect && (int) delta > last){
                    if(outside){
                        positions.add(new BlockPos(fromPos.add(last, 0, i)));
                    } else {
                        positions.add(new BlockPos(fromPos.add(delta, 0, i - 1)));
                    }
                }
                BlockPos pos = new BlockPos(fromPos.add(delta, 0, i));
                positions.add(pos);
                a += 1.0;
            }
        }
        return positions;
    }

    private static double absSub(double a, double b){
        double x = Math.max(a, b);
        double y = Math.min(a, b);
        return x - y;
    }
}
