package com.kisman.cc.util.enums;

import com.kisman.cc.util.Globals;
import net.minecraft.util.math.*;

public enum  Direction implements Globals {
    XPlus(new Vec3i(1, 0, 0)),
    XMinus(new Vec3i(-1, 0, 0)),
    ZPlus(new Vec3i(0, 0, 1)),
    ZMinus(new Vec3i(0, 0, -1)),
    None(new Vec3i(0, 0, 0));

    public Vec3i vec;
    Direction(Vec3i vec) {this.vec = vec;}

    public static Direction getDirectionForPlayerByBlockPos(BlockPos pos) {
        if(mc.player.getPosition().getX() > pos.getX()) return XPlus;
        else if(mc.player.getPosition().getX() < pos.getX()) return XMinus;
        if(mc.player.getPosition().getZ() > pos.getZ()) return ZPlus;
        else if(mc.player.getPosition().getZ() < pos.getZ()) return ZMinus;
        return None;
    }
    public static Direction byName(String name) {
        if(name.equals(XPlus.name())) return XPlus;
        if(name.equals(XMinus.name())) return XMinus;
        if(name.equals(ZPlus.name())) return ZPlus;
        if(name.equals(ZMinus.name())) return ZMinus;
        return None;
    }
}
