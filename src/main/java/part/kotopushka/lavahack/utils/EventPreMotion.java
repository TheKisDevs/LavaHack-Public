package part.kotopushka.lavahack.utils;


import net.minecraft.client.Minecraft;

public class EventPreMotion
        extends EventCancellable
        implements Event {
    private float yaw;
    private float pitch;
    private double posX;
    private double posY;
    private double posZ;
    private boolean onGround;

    public EventPreMotion(float yaw, float pitch, double posX, double posY, double posZ, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = onGround;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getPosX() {
        return this.posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    public void stopGround(boolean onGround){
        this.onGround = !this.onGround;
    }
}


