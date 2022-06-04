package com.kisman.cc.util;

import net.minecraft.client.Minecraft;

public class Rotation {
    private Minecraft mc = Minecraft.getMinecraft();

    private float yaw;
    private float pitch;
    private final Rotate rotate;

    public Rotation(float yaw, float pitch, Rotate rotate) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = rotate;
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = Rotate.NONE;
    }

    public void updateModelRotations() {
        if (mc.player != null && mc.world != null) {
            switch (rotate) {
                case PACKET:
                    mc.player.renderYawOffset = yaw;
                    mc.player.rotationYawHead = yaw;
//                    Cosmos.INSTANCE.getRotationManager().setHeadPitch(pitch);
                    break;
                case CLIENT:
                    mc.player.rotationYaw = yaw;
                    mc.player.rotationPitch = pitch;
                    break;
                case NONE:
                	break;
            }
        }
    }

    public void restoreRotations() {
        if (mc.player != null && mc.world != null) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
        }
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Rotate getRotation() {
        return rotate;
    }

    public enum Rotate {
        PACKET, CLIENT, NONE
    }
}

