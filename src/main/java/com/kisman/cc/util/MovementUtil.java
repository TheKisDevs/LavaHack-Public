package com.kisman.cc.util;

import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Objects;

public class MovementUtil {
    public static final double WALK_SPEED = 0.221;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void setMotion(double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static float getDirection2() {
        float var1 = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0F) var1 += 180.0F;
        float forward = 1.0F;
        if (mc.player.moveForward < 0.0F) forward = -0.5F;
        else if (mc.player.moveForward > 0.0F) forward = 0.5F;
        if (mc.player.moveStrafing > 0.0F) var1 -= 90.0F * forward;
        if (mc.player.moveStrafing < 0.0F) var1 += 90.0F * forward;
        var1 *= 0.017453292F;
        return var1;
    }

    public static double getMotion(EntityPlayer entity) {
        return Math.abs(entity.motionX) + Math.abs(entity.motionZ);
    }

    public static double[] forward(final double speed) {
        float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) yaw += ((forward > 0.0f) ? -45 : 45);
            else if (side < 0.0f) yaw += ((forward > 0.0f) ? 45 : -45);
            side = 0.0f;
            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static boolean isBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(mc.player.posX - 0.3, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ + 0.3, mc.player.posX + 0.3, mc.player.posY + 2.5, mc.player.posZ - 0.3);
        return !MovementUtil.mc.world.getCollisionBoxes(mc.player, bb).isEmpty();
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = .2873;
        if (mc.player != null && mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getSpeed() {return getSpeed(false, 0.2873);}

    public static double getSpeed(boolean slowness, double defaultSpeed) {
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0;

        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            //noinspection ConstantConditions
            int amplifier = mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1;
        }

        return defaultSpeed;
    }

    public static void strafe(float yaw, double speed) {
        if (!isMoving()) return;
        mc.player.motionX = -Math.sin(yaw) * speed;
        mc.player.motionZ = Math.cos(yaw) * speed;
    }

    public static double getDistance2D() {
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static void strafe(float speed) {
        if (!isMoving()) return;
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * (double)speed;
        mc.player.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static boolean isMoving() {
        if (mc.player == null) return false;
        if (mc.player.movementInput.moveForward != 0.0f) return true;
        return mc.player.movementInput.moveStrafe != 0.0f;
    }

    public static float getDirection() {
        float var1 = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) var1 += 180.0f;

        float forward = 1.0f;

        if (mc.player.moveForward < 0.0f) forward = -0.5f;
        else if (mc.player.moveForward > 0.0f) forward = 0.5f;
        if (mc.player.moveStrafing > 0.0f) var1 -= 90.0f * forward;
        if (mc.player.moveStrafing < 0.0f) var1 += 90.0f * forward;

        return var1 *= (float)Math.PI / 180;
    }

    public static void hClip(double off) {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        mc.player.setPosition(mc.player.posX + (-Math.sin(yaw) * off), mc.player.posY, mc.player.posZ + (Math.cos(yaw) * off));
    }

    public static float getRoundedForward() {
        return getRoundedMovementInput(mc.player.movementInput.moveForward);
    }

    public static float getRoundedStrafing() {
        return getRoundedMovementInput(mc.player.movementInput.moveStrafe);
    }

    private static final float getRoundedMovementInput(float input) {
        return (input > 0.0F) ? 1.0F : ((input < 0.0F) ? -1.0F : 0.0F);
    }

    public static float calcMoveYaw(float targetYaw) {
        float moveForward = getRoundedForward();
        float moveString = getRoundedStrafing();
        float yawIn = targetYaw;
        float strafe = 90 * moveString;
        strafe *= (moveForward != 0.0F) ? (moveForward * 0.5F) : 1.0F;
        float yaw = yawIn - strafe;
        yaw -= ((moveForward < 0.0F) ? 180 : 0);
        yaw = (float) Math.toRadians(yaw);

        float sens = mc.gameSettings.mouseSensitivity / 0.005F;
        float f = 0.005F * sens;
        float gcd = f * f * f * 1.2F;
        yaw -= yaw % gcd;

        return yaw;
    }
}