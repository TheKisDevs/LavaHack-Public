//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

// 
// Decompiled by Procyon v0.5.36
// 

package com.kisman.cc.util.pyro;

import net.minecraft.client.entity.EntityPlayerSP;

public class Rotation
{
    private float pitch;
    private float yaw;
    
    public Rotation(final float yaw, final float pitch) {
        this.setPitch(pitch);
        this.setYaw(yaw);
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
    
    public void fixedSensitivity(final float sensitivity) {
        final float f = sensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        this.yaw -= this.yaw % gcd;
        this.pitch -= this.pitch % gcd;
    }
    
    public void toPlayer(final EntityPlayerSP player) {
        player.rotationYaw = this.yaw;
        player.rotationPitch = this.pitch;
    }
}
