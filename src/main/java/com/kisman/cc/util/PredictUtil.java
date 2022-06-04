package com.kisman.cc.util;

import com.mojang.authlib.GameProfile;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class PredictUtil {
    static final Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayer predictPlayer(EntityPlayer entity, PredictSettings settings) {
        // Position of the player
        double[] posVec = new double[] {entity.posX, entity.posY, entity.posZ};
        // This is likely a temp variable that is going to replace posVec
        double[] newPosVec = posVec.clone();
        // entity motions
        double motionX = entity.posX - entity.prevPosX;
        double motionY = entity.posY - entity.prevPosY;
        if (settings.debug) ChatUtils.warning("Motion Y:" + motionY);
        double motionZ = entity.posZ - entity.prevPosZ;
        // Y Prediction stuff
        boolean goingUp = false;
        boolean start = true;
        int up = 0, down = 0;
        if (settings.debug)
            ChatUtils.warning(String.format("Values: %f %f %f", newPosVec[0], newPosVec[1], newPosVec[2]));

        // If he want manual out hole
        boolean isHole = false;
        if (settings.manualOutHole && motionY > .2) {
            if (HoleUtil.isHole(EntityUtil.getPosition(entity), false, true).getType() != HoleUtil.HoleType.NONE && BlockUtil.getBlock(EntityUtil.getPosition(entity).add(0, 2, 0)) instanceof BlockAir) isHole = true;
            else if (settings.aboveHoleManual && HoleUtil.isHole(EntityUtil.getPosition(entity).add(0, -1, 0), false, true).getType() != HoleUtil.HoleType.NONE) isHole = true;
            if (isHole) posVec[1] += 1;
        }
        
        // For stair predict
        boolean allowPredictStair = false;
        int stairPredicted = 0;
        if (settings.stairPredict) {
            allowPredictStair = Math.abs(entity.posX - entity.prevPosX) + Math.abs(entity.posZ - entity.prevPosZ) > settings.speedActivationStairs;
            if (settings.debug) ChatUtils.warning(String.format("Speed: %.2f Activation speed Stairs: %.2f", Math.abs(entity.posX - entity.prevPosX) + Math.abs(entity.posZ - entity.prevPosZ), settings.speedActivationStairs));
        }

        for(int i = 0; i < settings.tick; i++) {
            RayTraceResult result;
            // Here we can choose if calculating XZ separated or not
            if (settings.splitXZ) {
                // Clone posVec
                newPosVec = posVec.clone();
                // Add X
                newPosVec[0] += motionX;
                // Check collisions
                result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], posVec[2]));
                boolean predictedStair = false;
                if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) posVec = newPosVec.clone();
                else if (settings.stairPredict && allowPredictStair) {
                    if (BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) {
                        posVec[1] += 1;
                        predictedStair = true;
                    }
                }
                // Calculate Z
                newPosVec = posVec.clone();
                newPosVec[2] += motionZ;
                // Check collisions
                result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) posVec = newPosVec.clone();
                else if (settings.stairPredict && allowPredictStair && !predictedStair) if (BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) posVec[1] += 1;
                // In case of calculating them toogheter
            } else {
                // Add XZ and check collisions
                newPosVec = posVec.clone();
                newPosVec[0] += motionX;
                newPosVec[2] += motionZ;
                result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], posVec[1], newPosVec[2]));
                if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) posVec = newPosVec.clone();
                else if (settings.stairPredict && allowPredictStair) if (BlockUtil.getBlock(newPosVec[0], newPosVec[1] + 1, newPosVec[2]) instanceof BlockAir && stairPredicted++ < settings.nStairs) posVec[1] += 1;
            }

                /*
                    The y predict is a little bit complex:
                    1) We have to understand if we are going up or down
                    2) We have to understand when going up and going down and when switching it
                    3) Implement a physic to the y transiction
                    So,
                    1) We understand if we are going up or down by the default motionY: If it's > 0, we are going up.
                        This was the intention, then i decided to set it always to false because of strange bugs
                        it predict nicely so no problem
                    2) We understand when switch:
                        a) from down to up when we collide with something under us, if we collide with something
                        b) From up to down when the motionY become from positive to negative
                    and this open the 3 point about the physyc:
                    3) For making everything simpler and not overcomplex, when going up the motionY become
                    a little bit bigger untill the reach a value that is called Exponent Start.
                    Meanwhile, for going down, it's a little bit easier: we just do like before
                    but until we collide with something.
                    Yes, this is a unprecise but, this error of imprecision, is noticable only in
                    long tick predict, and you dont use long tick prediction lol.
                    i think this y prediction is usefull for detecting, in short distance, if we are going up or down
                    on a block
                    */
            if (settings.calculateY && !isHole) {
                newPosVec = posVec.clone();
                // If the enemy is not on the ground. We also be sure that it's not -0.078
                // Because -0.078 is the motion we have when standing in a block.
                // I dont know if we have antiHunger the server say we are onGround or not, i'll keep it here
                if (!entity.onGround && motionY != -0.0784000015258789 && motionY != 0) {
                    double decreasePow = settings.startDecrease / Math.pow(10, settings.exponentStartDecrease);
                    if (start) {
                        // If it's the first time, we have to check first if our motionY is == 0.
                        // MotionY is == 0 when we are jumping at the moment when we are going down
                        if (motionY == 0) motionY = decreasePow;
                        // Check if we are going up or down. We say > because of motionY
                        goingUp = false;
                        start = false;
                        if (settings.debug) ChatUtils.warning("Start motionY: " + motionY);
                    }
                    // Lets just add values to our motionY
                    double increasePowY = settings.increaseY / Math.pow(10, settings.exponentIncreaseY);
                    double decreasePowY = settings.decreaseY / Math.pow(10, settings.exponentDecreaseY);
                    motionY += goingUp ? increasePowY : decreasePowY;
                    // If the motionY is going too far, go down
                    if (Math.abs(motionY) > decreasePow) {
                        goingUp = false;
                        if (settings.debug) up++;
                        motionY = decreasePowY;
                    }
                    // Lets add motionY
                    newPosVec[1] += (goingUp ? 1 : -1) * motionY;
                    // Get result
                    result = mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], newPosVec[1], newPosVec[2]));

                    if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) posVec = newPosVec.clone();
                    else {
                        if (!goingUp) {
                            goingUp = true;
                            // Add this for deleting before motion
                            newPosVec[1] += increasePowY;
                            motionY = increasePowY;
                            newPosVec[1] += motionY;
                            if (settings.debug) down++;
                        }
                    }
                }
            }

            if (settings.show) ChatUtils.warning(String.format("Values: %f %f %f", posVec[0], posVec[1], posVec[2]));
        }
        if (settings.debug) ChatUtils.warning(String.format("Player: %s Total ticks: %d Up: %d Down: %d", entity.getGameProfile().getName(), settings.tick, up, down));
        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), entity.getName()));
        clonedPlayer.setPosition(posVec[0], posVec[1], posVec[2]);
        clonedPlayer.inventory.copyInventory(entity.inventory);
        clonedPlayer.setHealth(entity.getHealth());
        clonedPlayer.prevPosX = entity.prevPosX;
        clonedPlayer.prevPosY = entity.prevPosY;
        clonedPlayer.prevPosZ = entity.prevPosZ;
        for(PotionEffect effect : entity.getActivePotionEffects()) clonedPlayer.addPotionEffect(effect);
        return clonedPlayer;
    }

    public static class PredictSettings {
        final int tick;
        final boolean calculateY;
        final int startDecrease;
        final int exponentStartDecrease;
        final int decreaseY;
        final int exponentDecreaseY;
        final int increaseY;
        final int exponentIncreaseY;
        final boolean splitXZ;
        final int width;
        final boolean debug;
        final boolean show;
        final boolean manualOutHole;
        final boolean aboveHoleManual;
        final boolean stairPredict;
        final int nStairs;
        final double speedActivationStairs;
        
        public PredictSettings(int tick, boolean calculateY, int startDecrease, int exponentStartDecrease, int decreaseY, int exponentDecreaseY,
                               int increaseY, int exponentIncreaseY, boolean splitXZ, int width, boolean debug, boolean show, boolean manualOutHole,
                               boolean aboveHoleManual, boolean stairPredict, int nStairs, double speedActivationStairs) {
            this.tick = tick;
            this.calculateY = calculateY;
            this.startDecrease = startDecrease;
            this.exponentStartDecrease = exponentStartDecrease;
            this.decreaseY = decreaseY;
            this.exponentDecreaseY = exponentDecreaseY;
            this.increaseY = increaseY;
            this.exponentIncreaseY = exponentIncreaseY;
            this.splitXZ = splitXZ;
            this.width = width;
            this.debug = debug;
            this.show = show;
            this.manualOutHole = manualOutHole;
            this.aboveHoleManual = aboveHoleManual;
            this.stairPredict = stairPredict;
            this.nStairs = nStairs;
            this.speedActivationStairs = speedActivationStairs;
        }
    }
}
