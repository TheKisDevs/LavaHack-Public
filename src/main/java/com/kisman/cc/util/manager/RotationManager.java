package com.kisman.cc.util.manager;

import com.kisman.cc.util.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class RotationManager {
    private static Minecraft mc = Minecraft.getMinecraft();
    private float yaw, pitch;

    public void update() {
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public void reset() {
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
        mc.player.rotationYawHead = yaw;
    }

    public static  void set(float yaw, float pitch, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
        } else {
            mc.player.rotationYaw = yaw;
            mc.player.rotationYawHead = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    public static Rotation calcRotation(@Nonnull BlockPos bp) {
        float[] angles = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(bp.getX() + .5f, bp.getY() + .5f, bp.getZ() + .5f));
        return new Rotation(angles[ 0 ], angles[ 1 ]);
    }

    public static Rotation calcRotation(@Nonnull Entity e) {
        float[] angles = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), e.getPositionEyes(mc.getRenderPartialTicks()));
        return new Rotation(angles[ 0 ], angles[ 1 ]);
    }

    public static float[] look(BlockPos bp, boolean packet) {
        return look(bp, packet, true);
    }

    public static  float[] look(Entity bp, boolean packet) {
        return look(bp, packet, true);
    }

    public  static float[] look(BlockPos bp, boolean packet, boolean set) {
        float[] angles = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(bp.getX() + .5f, bp.getY() + .5f, bp.getZ() + .5f));
        if (set) set(angles[ 0 ], angles[ 1 ], packet);
        return angles;
    }

    public static  float[] look(Entity entity, boolean packet, boolean set) {
        float[] angles = calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        if (set) set(angles[ 0 ], angles[ 1 ], packet);
        return angles;
    }

    //credits to 3arthqu4ke cuz im bad at math :)
    public static float[] calcAngle(Vec3d from, Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0F;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { ( float ) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f), ( float ) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }

}
