package com.kisman.cc.catlua.lua.utils;

import net.minecraft.util.math.Vec3i;

public enum LuaDirection {
    NORTH(new Vec3i(0, 0, -1)),
    WEST(new Vec3i(-1, 0, 0)),
    SOUTH(new Vec3i(0, 0, 1)),
    EAST(new Vec3i(1, 0, 0));

    public Vec3i vec;
    LuaDirection(Vec3i vec) {this.vec = vec;}
}
