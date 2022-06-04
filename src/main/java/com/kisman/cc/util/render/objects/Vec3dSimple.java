package com.kisman.cc.util.render.objects;

public class Vec3dSimple {
    public double x, y, z;
    public Vec3dSimple(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override public boolean equals(Object obj) {
        if(obj instanceof Vec3dSimple) {
            Vec3dSimple vec = (Vec3dSimple) obj;
            return vec.x == x && vec.y == y && vec.z == z;
        }
        return false;
    }
    public void round() {
        x = Math.round(x);
        y = Math.round(y);
        z = Math.round(z);
    }
}
