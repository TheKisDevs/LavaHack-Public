package com.kisman.cc.util;

import com.kisman.cc.util.pyro.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class VecRotation
{
    private Vec3d vec;
    private Rotation rotation;
    private EnumFacing sideHit;
    
    public VecRotation(final Vec3d vec, final Rotation rotation) {
        this.setVec(vec);
        this.setRotation(rotation);
    }
    
    public VecRotation(final Vec3d vec, final Rotation rot, final EnumFacing sideHit) {
        this(vec, rot);
        this.setSideHit(sideHit);
    }

    public Vec3d getVec() {
        return this.vec;
    }
    
    public void setVec(final Vec3d vec) {
        this.vec = vec;
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
    }
    
    public EnumFacing getSideHit() {
        return this.sideHit;
    }
    
    public void setSideHit(final EnumFacing sideHit) {
        this.sideHit = sideHit;
    }
}
