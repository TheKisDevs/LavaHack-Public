package com.kisman.cc.module.combat.holefiller;

import net.minecraft.util.math.BlockPos;

public class Hole {
    public BlockPos pos;
    public float distToPlayer;
    public float distToTarget;

    public Hole(BlockPos pos, float distToPlayer, float distToTarget) {
        this.pos = pos;
        this.distToPlayer = distToPlayer;
        this.distToTarget = distToTarget;
    }

    public BlockPos getDownHoleBlock() {
        if(pos == null) return BlockPos.ORIGIN;

        return pos.down();
    }
}
