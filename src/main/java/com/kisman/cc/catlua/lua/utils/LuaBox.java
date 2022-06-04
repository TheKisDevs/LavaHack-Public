package com.kisman.cc.catlua.lua.utils;

import net.minecraft.util.math.AxisAlignedBB;

public class LuaBox {
    public double minX, minY, minZ, maxX, maxY, maxZ;
    public LuaBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
    public AxisAlignedBB toAABB() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
