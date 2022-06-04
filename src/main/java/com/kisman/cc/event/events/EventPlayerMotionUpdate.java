package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.function.Consumer;

public class EventPlayerMotionUpdate extends Event {
    protected float yaw;
    protected float pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;
    private Consumer<EntityPlayerSP> funcToCall;
    private boolean isForceCancelled;

    public EventPlayerMotionUpdate(Era era, float yaw, float pitch, double posX, double posY, double posZ, boolean OnGround) {
        super(era);
        this.funcToCall = null;
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.onGround = OnGround;
    }

    public Consumer<EntityPlayerSP> getFunc() {
        return this.funcToCall;
    }

    public void setFunct(final Consumer<EntityPlayerSP> post) {
        this.funcToCall = post;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(final double yaw) {
        this.yaw = (float)yaw;
    }

    public void setPitch(final double pitch) {
        this.pitch = (float)pitch;
    }

    public void forceCancel() {
        this.isForceCancelled = true;
    }

    public boolean isForceCancelled() {
        return this.isForceCancelled;
    }

    public void setX(final double posX) {
        this.x = posX;
    }

    public void setY(final double d) {
        this.y = d;
    }

    public void setZ(final double posZ) {
        this.z = posZ;
    }

    public void setOnGround(final boolean b) {
        this.onGround = b;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public String getName() {
        return "player_motion";
    }
}
