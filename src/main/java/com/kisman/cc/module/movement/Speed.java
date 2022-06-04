package com.kisman.cc.module.movement;

import java.util.*;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.*;

import com.kisman.cc.util.*;
import com.kisman.cc.util.manager.Managers;
import i.gishreloaded.gishcode.utils.*;
import me.zero.alpine.listener.*;
import net.minecraft.block.Block;
import net.minecraft.init.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;

public class Speed extends Module {
    public static Speed instance;

    private float yPortSpeed;

    public Setting speedMode = new Setting("SpeedMode", this, "Strafe", new ArrayList<>(Arrays.asList("Strafe", "Strafe New", "YPort", "Sti", "Matrix 6.4", "Matrix Bhop", "Sunrise Strafe", "Bhop", "Strafe2", "Matrix")));

    private Setting useTimer = new Setting("Use Timer", this, false).setVisible(() -> speedMode.checkValString("Bhop") || speedMode.checkValString("Strafe New"));

    private Setting motionXmodifier = new Setting("Motion X Modifier", this, 0, 0, 0.5, false).setVisible(() -> speedMode.checkValString("Strafe2"));
    private Setting motionZmodifier = new Setting("Motion Z Modifier", this, 0, 0, 0.5, false).setVisible(() -> speedMode.checkValString("Strafe2"));

    private Setting strafeNewLine = new Setting("StrafeNewLine", this, "Strafe New").setVisible(() -> speedMode.checkValString("Strafe New"));
    private Setting strafeSpeed = new Setting("Strafe Speed", this, 0.2873f, 0.1f, 1, false).setVisible(() -> speedMode.checkValString("Strafe New"));
    private Setting slow = new Setting("Slow", this, false).setVisible(() -> speedMode.checkValString("Strafe New"));
    private Setting cap = new Setting("Cap", this, 10, 0, 10, false).setVisible(() -> speedMode.checkValString("Strafe New"));
    private Setting scaleCap = new Setting("Scale Cap", this, false).setVisible(() -> speedMode.checkValString("Strafe New"));
    private Setting lagTime = new Setting("Lag Time", this, 500, 0, 1000, Slider.NumberType.TIME).setVisible(() -> speedMode.checkValString("Strafe New"));

    private Setting yPortLine = new Setting("YPortLine", this, "YPort").setVisible(() -> speedMode.checkValString("YPort"));
    private Setting yWater = new Setting("Water", this, false).setVisible(() -> speedMode.checkValString("YPort"));
    private Setting yLava = new Setting("Lava", this, false).setVisible(() -> speedMode.checkValString("YPort"));

    private Setting stiLine = new Setting("StiLine", this, "Sti").setVisible(() -> speedMode.checkValString("Sti"));
    private Setting stiSpeed = new Setting("StiSpeed", this, 4, 0.1, 10, true).setVisible(() -> speedMode.checkValString("Sti"));

    private Setting bhopLine = new Setting("BhopLine", this, "Bhop").setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting useMotion = new Setting("Use Motion", this, false).setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting useMotionInAir = new Setting("Use Motion In Air", this, false).setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting jumpMovementFactorSpeed = new Setting("Jump Movement Factor Speed", this, 0.265f, 0.01f, 10, false).setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting jumpMovementFactor = new Setting("Jump Movement Factor", this, false).setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting boostSpeed = new Setting("Boost Speed", this, 0.265f, 0.01f, 10, false).setVisible(() -> speedMode.checkValString("Bhop"));
    private Setting boostFactor = new Setting("Boost Factor", this, false).setVisible(() -> speedMode.checkValString("Bhop"));

    private int stage;
    private double speed;
    private double dist;
    private boolean boost;

    private BlockPos lastPos;
    private Motion currentMotion;
    private double y = 1;

    public Speed() {
        super("Speed", "speed", Category.MOVEMENT);

        instance = this;

        setmgr.rSetting(speedMode);

        setmgr.rSetting(useTimer);

        setmgr.rSetting(motionXmodifier);
        setmgr.rSetting(motionZmodifier);

        setmgr.rSetting(strafeNewLine);
        setmgr.rSetting(strafeSpeed);
        setmgr.rSetting(slow);
        setmgr.rSetting(cap);
        setmgr.rSetting(scaleCap);
        setmgr.rSetting(lagTime);

        setmgr.rSetting(yPortLine);
        Kisman.instance.settingsManager.rSetting(new Setting("YPortSpeed", this, 0.06f, 0.01f, 0.15f, false).setVisible(() -> speedMode.checkValString("YPort")));
        setmgr.rSetting(yWater);
        setmgr.rSetting(yLava);

        setmgr.rSetting(stiLine);
        setmgr.rSetting(stiSpeed);

        setmgr.rSetting(bhopLine);
        setmgr.rSetting(useMotion);
        setmgr.rSetting(useMotionInAir);
        setmgr.rSetting(jumpMovementFactor);
        setmgr.rSetting(jumpMovementFactorSpeed);
        setmgr.rSetting(boostSpeed);
        setmgr.rSetting(boostFactor);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        if(mc.player == null || mc.world == null) return;
        stage = 4;
        dist = MovementUtil.getDistance2D();
        speed = MovementUtil.getSpeed();
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);

        EntityUtil.resetTimer();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        super.setDisplayInfo("[" + speedMode.getValString() + TextFormatting.GRAY + "]");

        yPortSpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "YPortSpeed").getValDouble();
        dist = MovementUtil.getDistance2D();

        if(mc.player.moveForward > 0 && mc.player.hurtTime < 5 && speedMode.getValString().equalsIgnoreCase("Strafe")) {
            if(mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = Utils.getDirection();

                mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
            } else {
                double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double speed = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90 ? 1.0064 : 1.001;
                double direction = Utils.getDirection();

                mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("YPort")) doYPortSpeed();
        else if(speedMode.getValString().equalsIgnoreCase("Strafe New") && !mc.player.isElytraFlying()) {
            if(useTimer.getValBoolean() && Managers.instance.passed(250)) EntityUtil.setTimer(1.0888f);
            if(!Managers.instance.passed(lagTime.getValInt())) return;
            if(stage == 1 && PlayerUtil.isMoving(mc.player)) speed = 1.35 * MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()) - 0.01;
            else if(stage == 2 && PlayerUtil.isMoving(mc.player) && mc.player.onGround) {
                mc.player.motionY = 0.3999 + MovementUtil.getJumpSpeed();
                speed *= boost ? 1.6835 : 1.395;
            } else if(stage == 3) {
                speed = dist  - 0.66 * (dist - MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
                boost = !boost;
            } else {
                if((mc.world.getCollisionBoxes(null, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) stage = PlayerUtil.isMoving(mc.player) ? 1 : 0;
                speed = dist - dist / 159;
            }

            speed = Math.min(speed, getCap());
            speed = Math.max(speed, MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
            MovementUtil.strafe((float) speed);

            if(PlayerUtil.isMoving(mc.player)) stage++;
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix Bhop") && PlayerUtil.isMoving(mc.player)) {
            mc.gameSettings.keyBindJump.pressed = false;

            if (mc.player.onGround) {
                mc.player.jump();
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0208f);
                mc.player.jumpMovementFactor = 0.1f;
                EntityUtil.setTimer(0.94f);
            }
            if (mc.player.fallDistance > 0.6 && mc.player.fallDistance < 1.3) {
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0208f);
                EntityUtil.setTimer(1.8f);
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix 6.4")) {
            if (mc.player.ticksExisted % 4 == 0) mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            if (!PlayerUtil.isMoving(mc.player)) return;
            if (mc.player.onGround) {
                mc.gameSettings.keyBindJump.pressed = false;
                mc.player.jump();
            } else if (mc.player.fallDistance <= 0.1) {
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0202f);
                mc.player.jumpMovementFactor = 0.027f;
                EntityUtil.setTimer(1.5f);
            } else if (mc.player.fallDistance > 0.1 && mc.player.fallDistance < 1.3) EntityUtil.setTimer(0.7f);
            else if (mc.player.fallDistance >= 1.3) {
                EntityUtil.resetTimer();
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0202f);
                mc.player.jumpMovementFactor = 0.025f;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Sunrise Strafe")) {
            if (PlayerUtil.isMoving(mc.player)) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    MovementUtil.strafe(MovementUtil.calcMoveYaw(mc.player.rotationYaw), MovementUtil.getSpeed() * 1.02);
                }
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Bhop")) doBhop();
        else if(speedMode.getValString().equalsIgnoreCase("Strafe2") && MovementUtil.isMoving()) {
            if(mc.player.onGround) mc.player.jump();
            else {
                double yaw = MovementUtil.getDirection();
                mc.player.motionX = -Math.sin(yaw) * motionXmodifier.getValFloat();
                mc.player.motionZ = Math.cos(yaw) * motionZmodifier.getValFloat();
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix") && MovementUtil.isMoving() && mc.player.ticksExisted % 2 == 0) {
            if(mc.player.onGround) mc.player.jump();
            else MovementUtil.setMotion(MovementUtil.WALK_SPEED * 1.025);
        }
    }

    private void doYPortSpeed() {
        if(!PlayerUtil.isMoving(mc.player) || (mc.player.isInWater() && !yWater.getValBoolean()) && (mc.player.isInLava() && !yLava.getValBoolean()) || mc.player.collidedHorizontally) return;
        if(mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            mc.player.jump();
            PlayerUtil.setSpeed(mc.player, PlayerUtil.getBaseMoveSpeed() + this.yPortSpeed);
        } else {
            mc.player.motionY = -1;
            EntityUtil.resetTimer();
        }
    }

    public double getCap() {
        double ret = cap.getValDouble();

        if (!scaleCap.getValBoolean()) return ret;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            ret *= 1 + 0.2 * (amplifier + 1);
        }

        if (slow.getValBoolean() && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            ret /= 1 + 0.2 * (amplifier + 1);
        }

        return ret;
    }

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

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

    private double getBaseMotionSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * ((double) amplifier + 1);
        }
        return baseSpeed;
    }

    private void doBhop() {
        currentMotion = getMotion();
        mc.player.setSprinting(true);

        if(mc.gameSettings.keyBindForward.isKeyDown()) {
            if(mc.player.onGround) {
                if(useMotion.getValBoolean() && currentMotion != null) {
                    switch (currentMotion) {
                        case mX:
                            mc.player.motionX=mc.player.motionX-0.1;
                            break;
                        case X:
                            mc.player.motionX=mc.player.motionX+0.1;
                            break;
                        case mY:
                            mc.player.motionY=mc.player.motionY-0.1;
                            break;
                        case Y:
                            mc.player.motionY=mc.player.motionY+0.1;
                            break;
                    }
                }
                y = 1;
                EntityUtil.resetTimer();
                if(useTimer.getValBoolean()) Managers.instance.timerManager.updateTimer(this, 2, 1.3f);
                mc.player.jump();
                double[] dirSpeed = directionSpeed((getBaseMotionSpeed() * boostSpeed.getValDouble()) + (boostFactor.getValBoolean() ? 0.3 : 0));
                mc.player.motionX = dirSpeed[0];
                mc.player.motionZ = dirSpeed[1];
            } else {
                if(jumpMovementFactor.getValBoolean()) mc.player.jumpMovementFactor = jumpMovementFactorSpeed.getValFloat();
                if(y == 1) y = mc.player.getPositionVector().y;
                else {
                    if(mc.player.getPositionVector().y < y) {
                        y = mc.player.getPositionVector().y;
                        mc.player.motionX = 0;
                        mc.player.motionZ = 0;
                        if(useTimer.getValBoolean()) EntityUtil.resetTimer();
                        Managers.instance.timerManager.updateTimer(this, 2, 16);
                    } else {
                        y = mc.player.getPositionVector().y;
                        if(useMotionInAir.getValBoolean() && currentMotion != null) {
                            switch (currentMotion) {
                                case mX:
                                    mc.player.motionX = mc.player.motionX - 0.2;
                                    break;
                                case X:
                                    mc.player.motionX = mc.player.motionX + 0.2;
                                    break;
                                case mY:
                                    mc.player.motionY = mc.player.motionY - 0.2;
                                    break;
                                case Y:
                                    mc.player.motionY = mc.player.motionY + 0.2;
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    private Motion getMotion() {
        BlockPos posToCheck = new BlockPos(EntityUtil.getRoundedBlockPos(mc.player).getX(), EntityUtil.getRoundedBlockPos(mc.player).getY(), EntityUtil.getRoundedBlockPos(mc.player).getZ());
        if(getBlock(posToCheck) == Blocks.AIR && lastPos != null) {
            if(posToCheck != lastPos) {
                if(lastPos.add(0, 0, -1).equals(posToCheck)) return Motion.mY;
                if(lastPos.add(0, 0, 1).equals(posToCheck)) return Motion.Y;
                if(lastPos.add(1, 0, 0).equals(posToCheck)) return Motion.X;
                if(lastPos.add(-1, 0, 0).equals(posToCheck)) return Motion.mX;
            }
        } else lastPos = new BlockPos(EntityUtil.getRoundedBlockPos(mc.player).getX(), EntityUtil.getRoundedBlockPos(mc.player).getY(), EntityUtil.getRoundedBlockPos(mc.player).getZ());
        return null;
    }

    private Block getBlock(BlockPos pos) {
        if(pos != null) return mc.world.getBlockState(pos).getBlock();
        return null;
    }

    public enum Motion {X,Y,mX,mY}

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook) {
            if(mc.player != null) dist = 0;
            speed = 0;
            stage = 4;
            EntityUtil.setTimer(1);
        }
    });

    @EventHandler private final Listener<EventPlayerUpdate> listener = new Listener<>(event -> {if(speedMode.getValString().equalsIgnoreCase("Sti")) mc.timer.tickLength = 50 / getSpeed();});
    private float getSpeed() {return Math.max((float) stiSpeed.getValDouble(), 0.1f);}
}
