package part.kotopushka.lavahack.utils;


import java.util.Objects;

import com.kisman.cc.module.render.Breadcrumbs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class MovementHelper extends Breadcrumbs.Helper {
    static Minecraft mc = Minecraft.getMinecraft();

    public MovementHelper(Vec3d vec) {
        super(vec);
    }

    public static boolean isMoving() {
        return MovementHelper.mc.player.movementInput.moveForward != 0.0f || MovementHelper.mc.player.movementInput.moveStrafe != 0.0f;
    }

    public static int getSpeedEffect() {
        if (MovementHelper.mc.player.isPotionActive(MobEffects.SPEED)) {
            return Objects.requireNonNull(MovementHelper.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1;
        }
        return 0;
    }

    public static boolean isInsideBlock() {
        return BlockHelper.collideBlock(MovementHelper.mc.player.getEntityBoundingBox(), 0.0f, block -> !(block instanceof BlockAir));
    }

    public static boolean isInsideBlock2() {
        for (int i = MathHelper.floor(MovementHelper.mc.player.boundingBox.minX); i < MathHelper.floor(MovementHelper.mc.player.boundingBox.maxX) + 1; ++i) {
            for (int j = MathHelper.floor(MovementHelper.mc.player.boundingBox.minY + 1.0); j < MathHelper.floor(MovementHelper.mc.player.boundingBox.maxY) + 2; ++j) {
                for (int k = MathHelper.floor(MovementHelper.mc.player.boundingBox.minZ); k < MathHelper.floor(MovementHelper.mc.player.boundingBox.maxZ) + 1; ++k) {
                    Block block = MovementHelper.mc.world.getBlockState(new BlockPos(i, j, k)).getBlock();
                    if (block == null || block instanceof BlockAir) continue;
                    AxisAlignedBB axisAlignedBB = block.getSelectedBoundingBox(MovementHelper.mc.world.getBlockState(new BlockPos(i, j, k)), MovementHelper.mc.world, new BlockPos(i, j, k));
                    if (block instanceof BlockHopper) {
                        axisAlignedBB = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1);
                    }
                    if (axisAlignedBB == null || !MovementHelper.mc.player.boundingBox.intersects(axisAlignedBB)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static double[] getSpeed(double speed) {
        float moveForward = MovementHelper.mc.player.movementInput.moveForward;
        float moveStrafe = MovementHelper.mc.player.movementInput.moveStrafe;
        float rotationYaw = MovementHelper.mc.player.prevRotationYaw + (MovementHelper.mc.player.rotationYaw - MovementHelper.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static void teleport(CPacketPlayer player, double dist) {
        double forward = MovementHelper.mc.player.movementInput.moveForward;
        double strafe = MovementHelper.mc.player.movementInput.moveStrafe;
        float yaw = MovementHelper.mc.player.rotationYaw;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += (float)(forward > 0.0 ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            } else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        double xspeed = forward * dist * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * dist * Math.sin(Math.toRadians(yaw + 90.0f));
        double zspeed = forward * dist * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * dist * Math.cos(Math.toRadians(yaw + 90.0f));
        player.x = xspeed;
        player.z = zspeed;
    }

    public static void teleport(double dist) {
        double forward = MovementHelper.mc.player.movementInput.moveForward;
        double strafe = MovementHelper.mc.player.movementInput.moveStrafe;
        float yaw = MovementHelper.mc.player.rotationYaw;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += (float)(forward > 0.0 ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            } else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        double x = MovementHelper.mc.player.posX;
        double y = MovementHelper.mc.player.posY;
        double z = MovementHelper.mc.player.posZ;
        double xspeed = forward * dist * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * dist * Math.sin(Math.toRadians(yaw + 90.0f));
        double zspeed = forward * dist * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * dist * Math.cos(Math.toRadians(yaw + 90.0f));
        MovementHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x + xspeed, y, z + zspeed, true));
        MovementHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x + xspeed, y + 0.1, z + zspeed, true));
        MovementHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x + xspeed, y, z + zspeed, true));
        MovementHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x + xspeed * 2.0, y, z + zspeed * 2.0, true));
    }

    public static float getPlayerDirection() {
        float rotationYaw = MovementHelper.mc.player.rotationYaw;
        if (MovementHelper.mc.player.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MovementHelper.mc.player.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MovementHelper.mc.player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MovementHelper.mc.player.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MovementHelper.mc.player.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return (float)Math.toRadians(rotationYaw);
    }

    public static float getEntityDirection(EntityLivingBase entity) {
        float rotationYaw = entity.rotationYaw;
        if (entity.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (entity.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (entity.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (entity.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (entity.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return (float)Math.toRadians(rotationYaw);
    }

    public static int getJumpBoostModifier() {
        return EntityHelper.getPotionModifier(MovementHelper.mc.player, Potion.getPotionById(8));
    }

    public static double getJumpHeight() {
        return MovementHelper.getJumpHeight(0.42f);
    }

    public static double getJumpHeight(double baseJumpHeight) {
        return baseJumpHeight + (double)((float)MovementHelper.getJumpBoostModifier() * 0.1f);
    }

    public static double getMinFallDist() {
        double baseFallDist = 3.0;
        return baseFallDist += (double)MovementHelper.getJumpBoostModifier();
    }

    public static boolean airBlockAbove2() {
        return !MovementHelper.mc.world.checkBlockCollision(MovementHelper.mc.player.getEntityBoundingBox().offset(0.0, MovementHelper.mc.player.onGround ? 0.3 : 0.0, 0.0));
    }

    public static boolean isUnderBedrock() {
        if (MovementHelper.mc.player.posY <= 3.0) {
            RayTraceResult trace = MovementHelper.mc.world.rayTraceBlocks(MovementHelper.mc.player.getPositionVector(), new Vec3d(MovementHelper.mc.player.posX, 0.0, MovementHelper.mc.player.posZ), false, false, false);
            return trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK;
        }
        return false;
    }

    public static boolean isBlockUnder(float value) {
        if (MovementHelper.mc.player.posY < 0.0) {
            return false;
        }
        AxisAlignedBB bb = MovementHelper.mc.player.getEntityBoundingBox().offset(0.0, -value, 0.0);
        return MovementHelper.mc.world.getCollisionBoxes(MovementHelper.mc.player, bb).isEmpty();
    }

    public static float getMoveDirection() {
        double motionX = MovementHelper.mc.player.motionX;
        double motionZ = MovementHelper.mc.player.motionZ;
        float direction = (float)(Math.atan2(motionX, motionZ) / Math.PI * 180.0);
        return -direction;
    }

    public static boolean airBlockAboveHead() {
        AxisAlignedBB bb = new AxisAlignedBB(MovementHelper.mc.player.posX - 0.3, MovementHelper.mc.player.posY + (double)MovementHelper.mc.player.getEyeHeight(), MovementHelper.mc.player.posZ + 0.3, MovementHelper.mc.player.posX + 0.3, MovementHelper.mc.player.posY + (!MovementHelper.mc.player.onGround ? 1.5 : 2.5), MovementHelper.mc.player.posZ - 0.3);
        return MovementHelper.mc.world.getCollisionBoxes(MovementHelper.mc.player, bb).isEmpty();
    }

    public static void calculateSilentMove(EventStrafe event, float yaw) {
        float dist;
        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();
        int difference = (int)((MathHelper.wrapDegrees(MovementHelper.mc.player.rotationYaw - yaw - 23.5f - 135.0f) + 180.0f) / 45.0f);
        float calcForward = 0.0f;
        float calcStrafe = 0.0f;
        switch (difference) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5: {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
            }
        }
        if (calcForward > 1.0f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1.0f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f;
        }
        if (calcStrafe > 1.0f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1.0f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f;
        }
        if ((dist = calcStrafe * calcStrafe + calcForward * calcForward) >= 1.0E-4f) {
            dist = (float)((double)friction / Math.max(1.0, Math.sqrt(dist)));
            float yawSin = MathHelper.sin((float)((double)yaw * Math.PI / 180.0));
            float yawCos = MathHelper.cos((float)((double)yaw * Math.PI / 180.0));
            MovementHelper.mc.player.motionX += (double)((calcStrafe *= dist) * yawCos - (calcForward *= dist) * yawSin);
            MovementHelper.mc.player.motionZ += (double)(calcForward * yawCos + calcStrafe * yawSin);
        }
    }

    public static void setEventSpeed(EventMove event, double speed) {
        double forward = MovementHelper.mc.player.movementInput.moveForward;
        double strafe = MovementHelper.mc.player.movementInput.moveStrafe;
        float yaw = MovementHelper.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public static float getSpeed() {
        return (float)Math.sqrt(MovementHelper.mc.player.motionX * MovementHelper.mc.player.motionX + MovementHelper.mc.player.motionZ * MovementHelper.mc.player.motionZ);
    }

    public static void setSpeed(float speed) {
        float yaw = MovementHelper.mc.player.rotationYaw;
        float forward = MovementHelper.mc.player.movementInput.moveForward;
        float strafe = MovementHelper.mc.player.movementInput.moveStrafe;
        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        MovementHelper.mc.player.motionX = (double)(forward * speed) * Math.cos(Math.toRadians(yaw + 90.0f)) + (double)(strafe * speed) * Math.sin(Math.toRadians(yaw + 90.0f));
        MovementHelper.mc.player.motionZ = (double)(forward * speed) * Math.sin(Math.toRadians(yaw + 90.0f)) - (double)(strafe * speed) * Math.cos(Math.toRadians(yaw + 90.0f));
    }

    public static double getDirectionAll() {
        float rotationYaw = MovementHelper.mc.player.rotationYaw;
        float forward = 1.0f;
        if (MovementHelper.mc.player.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        if (MovementHelper.mc.player.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MovementHelper.mc.player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MovementHelper.mc.player.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MovementHelper.mc.player.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void strafePlayer() {
        double yaw = MovementHelper.getDirectionAll();
        float speed = MovementHelper.getSpeed();
        MovementHelper.mc.player.motionX = -Math.sin(yaw) * (double)speed;
        MovementHelper.mc.player.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static double[] forward(double speed) {
        float forward = MovementHelper.mc.player.movementInput.moveForward;
        float side = MovementHelper.mc.player.movementInput.moveStrafe;
        float yaw = MovementHelper.mc.player.prevRotationYaw + (MovementHelper.mc.player.rotationYaw - MovementHelper.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getBaseSpeed() {
        double baseSpeed = 0.2873;
        if (MovementHelper.mc.player.isPotionActive(Potion.getPotionById(1))) {
            int amplifier = MovementHelper.mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static void strafe() {
        MovementHelper.strafe(MovementHelper.getSpeed());
    }

    public static void strafe(float speed) {
        if (!MovementHelper.isMoving()) {
            return;
        }
        double yaw = MovementHelper.getPlayerDirection();
        MovementHelper.mc.player.motionX = -Math.sin(yaw) * (double)speed;
        MovementHelper.mc.player.motionZ = Math.cos(yaw) * (double)speed;
    }
}


