package com.kisman.cc.module.combat.autocrystal;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.kisman.cc.module.combat.autocrystal.AI.mc;

public class AIUtils {

    public static List<BlockPos> getSphere(double radius) {
        ArrayList<BlockPos> posList = new ArrayList<>();
        BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
        for (int x = pos.getX() - (int) radius; x <= pos.getX() + radius; ++x) {
            for (int y = pos.getY() - (int) radius; y < pos.getY() + radius; ++y) {
                for (int z = pos.getZ() - (int) radius; z <= pos.getZ() + radius; ++z) {
                    double distance = (pos.getX() - x) * (pos.getX() - x) + (pos.getZ() - z) * (pos.getZ() - z) + (pos.getY() - y) * (pos.getY() - y);
                    BlockPos position = new BlockPos(x, y, z);
                    if (distance < radius * radius && !mc.world.getBlockState(position).getBlock().equals(Blocks.AIR)) {
                        posList.add(position);
                    }
                }
            }
        }
        return posList;
    }

    public static BlockPos crystalPos(){
        EntityEnderCrystal crystal = null;

        crystal.getPosition().getX();
        crystal.getPosition().getY();
        crystal.getPosition().getX();
        return null;
    }

}
