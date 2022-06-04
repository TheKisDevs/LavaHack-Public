package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.util.pyro.Rotation;
import me.zero.alpine.listener.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.*;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;

import static com.kisman.cc.util.BlockUtil.getEyesPos;

@SideOnly(Side.CLIENT)
public class RotationUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    private Random random;
    private int keepLength;
    public Rotation targetRotation;
    public Rotation serverRotation;
    public boolean keepCurrentRotation;
    private double x;
    private double y;
    private double z;
    final Packet<?>[] packet = new Packet<?>[1];
    final CPacketPlayer[] packetPlayer = new CPacketPlayer[1];

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        if (this.targetRotation != null) {
            --this.keepLength;
            if (this.keepLength <= 0) this.reset();
        }
        if (this.random.nextGaussian() > 0.8) this.x = Math.random();
        if (this.random.nextGaussian() > 0.8) this.y = Math.random();
        if (this.random.nextGaussian() > 0.8) this.z = Math.random();
    }

    public static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static float[] lookAtRandomed(Entity entityIn) {
        double diffX = entityIn.posX - mc.player.posX;
        double diffZ = entityIn.posZ - mc.player.posZ;
        double diffY;

        if (entityIn instanceof EntityLivingBase) diffY = entityIn.posY + entityIn.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight()) - 0.4;
        else diffY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (mc.player.posY + mc.player.getEyeHeight());

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = ((float) (((Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f)) + MathUtil.getRandom(-2, 2));
        float pitch = ((float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + MathUtil.getRandom(-2, 2));
        yaw = mc.player.rotationYaw + getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        pitch = mc.player.rotationPitch + getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        return new float[] {yaw, pitch};
    }

    public static float getFixedRotation(float value) {
        return (Math.round(value / getGCD())) * getGCD();
    }

    private static float getGCD() {
        float f = (float) (mc.gameSettings.mouseSensitivity * 0.6D + 0.2D);
        return ((f * f * f * 8.0f) * 0.15f);
    }

    public RotationUtils() {
        this.random = new Random();
        this.serverRotation = new Rotation(0.0f, 0.0f);
        this.keepCurrentRotation = false;
        this.x = this.random.nextDouble();
        this.y = this.random.nextDouble();
        this.z = this.random.nextDouble();
        final Packet<?>[] packet = new Packet<?>[1];
        final CPacketPlayer[] packetPlayer = new CPacketPlayer[1];
        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        packet[0] = event.getPacket();
        if (packet[0] instanceof CPacketPlayer) {
            packetPlayer[0] = (CPacketPlayer) packet[0];
            if (packetPlayer[0].rotating) this.serverRotation = new Rotation(packetPlayer[0].yaw, packetPlayer[0].pitch);
        }
    });

    public static double yawDist(BlockPos pos) {
        if(pos != null) {
            Vec3d difference = new Vec3d(pos).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static double yawDist(Entity e) {
        if(e != null) {
            Vec3d difference = e.getPositionVector ( ).add (new Vec3d( 0.0f , e.getEyeHeight ( ) / 2.0f , 0.0f )).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && ( mc.player.getDistanceSq ( pos ) < 4.0 || yawDist ( pos ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && ( mc.player.getDistanceSq ( entity ) < 4.0 || yawDist ( entity ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static float getFov() {
        return mc.gameSettings.fovSetting;
    }

    public static float getHalvedfov() {
        return getFov ( ) / 2.0f;
    }

    public static boolean isInFov ( Vec3d vec3d , Vec3d other ) {
        if ( mc.player.rotationPitch > 30.0f ? other.y > mc.player.posY : mc.player.rotationPitch < - 30.0f && other.y < mc.player.posY ) return true;
        float angle = calcAngleNoY ( vec3d , other )[0] - transformYaw ( );
        if ( angle < - 270.0f ) return true;
        float fov = (mc.gameSettings.fovSetting) / 2.0f;
        return angle < fov + 10.0f && angle > - fov - 10.0f;
    }

    public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difZ = to.z - from.z;
        return new float[]{(float) MathHelper.wrapDegrees ( Math.toDegrees (Math.atan2(difZ, difX)) - 90.0)};
    }

    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }

    public static Vec2f getRotationTo(AxisAlignedBB box) {
        EntityPlayerSP player = mc.player;
        if (player == null) return Vec2f.ZERO;

        Vec3d eyePos = player.getPositionEyes(1.0f);

        if (player.getEntityBoundingBox().intersects(box)) return getRotationTo(eyePos, box.getCenter());

        double x = MathHelper.clamp(eyePos.x, box.minX, box.maxX);
        double y = MathHelper.clamp(eyePos.y, box.minY, box.maxY);
        double z = MathHelper.clamp(eyePos.z, box.minZ, box.maxZ);

        return getRotationTo(eyePos, new Vec3d(x, y, z));
    }

    public static Vec2f getRotationTo(Vec3d posTo) {
        EntityPlayerSP player = mc.player;
        return player != null ? getRotationTo(player.getPositionEyes(1.0f), posTo) : Vec2f.ZERO;
    }

    public static void lookAtVec3d(final Vec3d vec3d) {
        final float[] angle = AngleUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        setPlayerRotations(angle[0], angle[1]);
    }

    public static void setPlayerRotations(com.kisman.cc.util.Rotation rotation) {
        setPlayerRotations(rotation.getYaw(), rotation.getPitch());
    }

    public static void setPlayerRotations(final float yaw, final float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    /**
     * Get rotation from a position vector to another position vector
     *
     * @param posFrom Calculate rotation from this position vector
     * @param posTo   Calculate rotation to this position vector
     */
    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.x, vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));

        return new Vec2f((float) yaw, (float) pitch);
    }

    public static float[] getRotationToPos(BlockPos pos) {
        double lengthXZ = Math.hypot(pos.getX(), pos.getZ());
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(pos.getZ(), pos.getX())) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(pos.getY(), lengthXZ)));
        return new float[] {(float) yaw, (float) pitch};
    }

    public static double normalizeAngle(double angle) {
        angle %= 360.0;

        if (angle >= 180.0) angle -= 360.0;
        if (angle < -180.0) angle += 360.0;

        return angle;
    }

    public static float normalizeAngle(float angle) {
        angle %= 360.0f;

        if (angle >= 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;

        return angle;
    }

    public static float transformYaw ( ) {
        float yaw = mc.player.rotationYaw % 360.0f;
        if ( mc.player.rotationYaw > 0.0f ) if ( yaw > 180.0f ) yaw = - 180.0f + ( yaw - 180.0f );
        else if ( yaw < - 180.0f ) yaw = 180.0f + ( yaw + 180.0f );
        if ( yaw < 0.0f ) return 180.0f + yaw;

        return - 180.0f + yaw;
    }

    public static float[] getAverageRotations(final List list) {
        double d = 0.0;
        double d2 = 0.0;
        double d3 = 0.0;
        for (final Object entityw : list) {
            final Entity entity = (Entity)entityw;
            d += entity.posX;
            d2 += entity.getEntityBoundingBox().maxY - 2.0;
            d3 += entity.posZ;
        }
        final float[] array = new float[2];
        final int n = 0;
        d /= list.size();
        d3 /= list.size();
        array[n] = getRotationFromPosition(d, d3, d2 /= list.size())[0];
        array[1] = getRotationFromPosition(d, d3, d2)[1];
        return array;
    }

    public static float[] getRotation(Entity entity){
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ );
        final double X = entity.getPositionVector().x - eyesPos.x + (Math.random() / 4);
        final double Y = entity.getPositionVector().y + entity.getEyeHeight() - eyesPos.y + (Math.random() / 4);
        final double Z = entity.getPositionVector().z - eyesPos.z + (Math.random() / 4);
        final double XZ = Math.sqrt(X * X + Z * Z);
        float yaw = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(Z, X)) - 90.0f );
        float pitch = MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(Y, XZ))) + 5);

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = (f * f * f) * 10;
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new float[]{MathHelper.clamp(yaw,-360,360), MathHelper.clamp(pitch,-90,90)};
    }
    
    public static float getDistanceBetweenAngles(final float f, final float f2) {
        float f3 = Math.abs(f - f2) % 360.0f;
        if (f3 > 180.0f) f3 = 360.0f - f3;
        return f3;
    }
    
    public static float getTrajAngleSolutionLow(final float f, final float f2, final float f3) {
        final float f4 = f3 * f3 * f3 * f3 - 0.006f * (0.006f * (f * f) + 2.0f * f2 * (f3 * f3));
        return (float)Math.toDegrees(Math.atan((f3 * f3 - Math.sqrt(f4)) / (0.006f * f)));
    }
    
    public static float[] getRotations(double x, double y, double z) {
        double diffX = x + 0.5 - mc.player.posX;
        double diffY = (y + 0.5) / 2.0 - (mc.player.posY + (double) mc.player.getEyeHeight());
        double diffZ = z + 0.5 - mc.player.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
    
    public static float[] getRotationFromPosition(final double d, final double d2, final double d3) {
        final double d4 = d - Minecraft.getMinecraft().player.posX;
        final double d5 = d2 - Minecraft.getMinecraft().player.posZ;
        final double d6 = d3 - Minecraft.getMinecraft().player.posY - 0.6;
        final double d7 = MathHelper.sqrt(d4 * d4 + d5 * d5);
        final float f = (float)(Math.atan2(d5, d4) * 180.0 / 3.141592653589793) - 90.0f;
        final float f2 = (float)(-(Math.atan2(d6, d7) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }

    public static int getDirection4D ( ) {
        return MathHelper.floor ( ( mc.player.rotationYaw * 4.0F / 360.0F ) + 0.5D ) & 3;
    }

    public static String getDirection4D ( boolean northRed ) {
        int dirnumber = getDirection4D();
        if ( dirnumber == 0 ) return "South (+Z)";
        if ( dirnumber == 1 ) return "West (-X)";
        if ( dirnumber == 2 ) return ( northRed ? TextFormatting.RED : "" ) + "North (-Z)";
        if ( dirnumber == 3 ) return "East (+X)";
        return "Loading...";
    }
    
    public static float[] getNeededRotations(final Entity entityLivingBase) {
        final double d = entityLivingBase.posX - Minecraft.getMinecraft().player.posX;
        final double d2 = entityLivingBase.posZ - Minecraft.getMinecraft().player.posZ;
        final double d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (Minecraft.getMinecraft().player.getEntityBoundingBox().minY + (Minecraft.getMinecraft().player.getEntityBoundingBox().maxY - Minecraft.getMinecraft().player.getEntityBoundingBox().minY));
        final double d4 = MathHelper.sqrt(d * d + d2 * d2);
        final float f = (float)(MathHelper.atan2(d2, d) * 180.0 / 3.141592653589793) - 90.0f;
        final float f2 = (float)(-(MathHelper.atan2(d3, d4) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }
    
    public static float[] getRotations(final EntityLivingBase entityLivingBase, final String string) {
        if (string.equals("Head")) {
            final double d = entityLivingBase.posX;
            final double d2 = entityLivingBase.posZ;
            final double d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f;
            return getRotationFromPosition(d, d2, d3);
        }
        if (string.equals("Chest")) {
            final double d = entityLivingBase.posX;
            final double d4 = entityLivingBase.posZ;
            final double d5 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 0.75;
            return getRotationFromPosition(d, d4, d5);
        }
        if (string.equals("Dick")) {
            final double d = entityLivingBase.posX;
            final double d6 = entityLivingBase.posZ;
            final double d7 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 1.2;
            return getRotationFromPosition(d, d6, d7);
        }
        if (string.equals("Legs")) {
            final double d = entityLivingBase.posX;
            final double d8 = entityLivingBase.posZ;
            final double d9 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 1.5;
            return getRotationFromPosition(d, d8, d9);
        }
        final double d = entityLivingBase.posX;
        final double d10 = entityLivingBase.posZ;
        final double d11 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 0.5;
        return getRotationFromPosition(d, d10, d11);
    }
    
    public static float getNewAngle(float f) {
        if ((f %= 360.0f) >= 180.0f) f -= 360.0f;
        if (f < -180.0f) f += 360.0f;
        return f;
    }

    public VecRotation faceBlock(final BlockPos blockPos) {
        if (blockPos == null) return null;
        VecRotation vecRotation = null;
        for (double xSearch = 0.1; xSearch < 0.9; xSearch += 0.1) {
            for (double ySearch = 0.1; ySearch < 0.9; ySearch += 0.1) {
                for (double zSearch = 0.1; zSearch < 0.9; zSearch += 0.1) {
                    final Vec3d eyesPos = new Vec3d(RotationUtils.mc.player.posX, RotationUtils.mc.player.getEntityBoundingBox().minY + RotationUtils.mc.player.getEyeHeight(), RotationUtils.mc.player.posZ);
                    final Vec3d posVec = new Vec3d((Vec3i)blockPos).add(new Vec3d(xSearch, ySearch, zSearch));
                    final double dist = eyesPos.distanceTo(posVec);
                    final double diffX = posVec.x - eyesPos.x;
                    final double diffY = posVec.y - eyesPos.y;
                    final double diffZ = posVec.z - eyesPos.z;
                    final double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
                    final Rotation rotation = new Rotation(MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                    final Vec3d rotationVector = this.getVectorForRotation(rotation);
                    final Vec3d vector = eyesPos.add(new Vec3d(rotationVector.x * dist, rotationVector.y * dist, rotationVector.z * dist));
                    final RayTraceResult obj = RotationUtils.mc.world.rayTraceBlocks(eyesPos, vector, false, false, true);
                    if (obj.typeOfHit == RayTraceResult.Type.BLOCK) {
                        final VecRotation currentVec = new VecRotation(posVec, rotation, obj.sideHit);
                        if (vecRotation == null || this.getRotationDifference(currentVec.getRotation()) < this.getRotationDifference(vecRotation.getRotation())) vecRotation = currentVec;
                    }
                }
            }
        }
        return vecRotation;
    }

    public double getRotationDifference(final Entity entity) {
        final Rotation rotation = this.toRotation(this.getCenter(entity.getEntityBoundingBox()), true);
        return this.getRotationDifference(rotation, new Rotation(RotationUtils.mc.player.rotationYaw, RotationUtils.mc.player.rotationPitch));
    }

    public double getRotationDifference(final Rotation rotation) {
        return (this.serverRotation == null) ? 0.0 : this.getRotationDifference(rotation, this.serverRotation);
    }

    public double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(this.getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    public Rotation toRotation(final Vec3d vec, final boolean predict) {
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.getEntityBoundingBox().minY + mc.player.getEyeHeight(), mc.player.posZ);
        if (predict) {
            eyesPos.add(new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ));
        }
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        return new Rotation(MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    public Vec3d getCenter(final AxisAlignedBB bb) {
        return new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public Vec3d getVectorForRotation(final Rotation rotation) {
        final float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292f - 3.1415927f);
        final float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292f - 3.1415927f);
        final float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292f);
        final float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292f);
        return new Vec3d((double)(yawSin * pitchCos), (double)pitchSin, (double)(yawCos * pitchCos));
    }

    private float getAngleDifference(final float a, final float b) {
        return ((a - b) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
        final float yawDifference = this.getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
        final float pitchDifference = this.getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());
        return new Rotation(currentRotation.getYaw() + ((yawDifference > turnSpeed) ? turnSpeed : Math.max(yawDifference, -turnSpeed)), currentRotation.getPitch() + ((pitchDifference > turnSpeed) ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
    }

    public VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random, final boolean predict, final boolean throughWalls) {
        if (outborder) {
            final Vec3d Vec3d = new Vec3d(bb.minX + (bb.maxX - bb.minX) * (this.x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (this.y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (this.z * 0.3 + 1.0));
            return new VecRotation(Vec3d, this.toRotation(Vec3d, predict));
        }
        final Vec3d randomVec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * this.x * 0.8, bb.minY + (bb.maxY - bb.minY) * this.y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * this.z * 0.8);
        final Rotation randomRotation = this.toRotation(randomVec, predict);
        VecRotation vecRotation = null;
        for (double xSearch = 0.15; xSearch < 0.85; xSearch += 0.1) {
            for (double ySearch = 0.15; ySearch < 1.0; ySearch += 0.1) {
                for (double zSearch = 0.15; zSearch < 0.85; zSearch += 0.1) {
                    final Vec3d Vec3d2 = new Vec3d(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = this.toRotation(Vec3d2, predict);
                    if (throughWalls || this.isVisible(Vec3d2)) {
                        final VecRotation currentVec = new VecRotation(Vec3d2, rotation);
                        if (vecRotation != null) {
                            if (random) if (this.getRotationDifference(currentVec.getRotation(), randomRotation) >= this.getRotationDifference(vecRotation.getRotation(), randomRotation)) continue;
                            else if (this.getRotationDifference(currentVec.getRotation()) >= this.getRotationDifference(vecRotation.getRotation())) continue;
                        }
                        vecRotation = currentVec;
                    }
                }
            }
        }
        return vecRotation;
    }

    public boolean isVisible(final Vec3d Vec3d) {
        final Vec3d eyesPos = new Vec3d(RotationUtils.mc.player.posX, RotationUtils.mc.player.getEntityBoundingBox().minY + RotationUtils.mc.player.getEyeHeight(), RotationUtils.mc.player.posZ);
        return RotationUtils.mc.world.rayTraceBlocks(eyesPos, Vec3d) == null;
    }

    public void reset() {
        this.keepLength = 0;
        this.targetRotation = null;
    }

    public static float[] getMatrixRotations(Entity e, boolean oldPositionUse) {
        double diffY;
        double diffX = (oldPositionUse ? e.prevPosX : e.posX) - (oldPositionUse ? mc.player.prevPosX : mc.player.posX);
        double diffZ = (oldPositionUse ? e.prevPosZ : e.posZ) - (oldPositionUse ? mc.player.prevPosZ : mc.player.posZ);
        if (e instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)e;
            float randomed = RandomUtils.nextFloat((float)(entitylivingbase.posY + (double)(entitylivingbase.getEyeHeight() / 1.5f)), (float)(entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (double)(entitylivingbase.getEyeHeight() / 3.0f)));
            diffY = (double)randomed - (mc.player.posY + (double) mc.player.getEyeHeight());
        } else diffY = (double)RandomUtils.nextFloat((float)e.getEntityBoundingBox().minY, (float)e.getEntityBoundingBox().maxY) - (mc.player.posY + (double) mc.player.getEyeHeight());
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI - 90.0) + RandomUtils.nextFloat(-2.0f, 2.0f);
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + RandomUtils.nextFloat(-2.0f, 2.0f);
        yaw = mc.player.rotationYaw + GCDUtil.getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        pitch = mc.player.rotationPitch + GCDUtil.getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }

    public static boolean isInFOV(Entity player, Entity entity, double angle) {
        final double angleDiff = getAngle360(player.rotationYaw, getLookNeeded(player, entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0.0 && angleDiff < (angle *= 0.5)) || (-angle < angleDiff && angleDiff < 0.0);
    }

    public static float[] getRotations(final Entity e) {
        final double diffX = e.posX - RotationUtils.mc.player.posX;
        final double diffZ = e.posZ - RotationUtils.mc.player.posZ;
        double diffY;
        if (e instanceof EntityLivingBase) diffY = e.posY + e.getEyeHeight() - (RotationUtils.mc.player.posY + RotationUtils.mc.player.getEyeHeight()) - 0.4;
        else diffY = (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0 - (RotationUtils.mc.player.posY + RotationUtils.mc.player.getEyeHeight());
        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793 - 90.0) + RandomUtils.nextFloat(-2.0f, 2.0f);
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793)) + RandomUtils.nextFloat(-2.0f, 2.0f);
        yaw = RotationUtils.mc.player.rotationYaw + GCDFix.getFixedRotation(MathHelper.wrapDegrees(yaw - RotationUtils.mc.player.rotationYaw));
        pitch = RotationUtils.mc.player.rotationPitch + GCDFix.getFixedRotation(MathHelper.wrapDegrees(pitch - RotationUtils.mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[] { yaw, pitch };
    }

    public static boolean isInFOV(Entity entity, double angle) {
        return isInFOV(mc.player, entity, angle);
    }

    public static float[] getLookNeeded(final Entity entity, final double x, final double y, final double z) {
        final double d = x + 0.5 - entity.posX;
        final double g = y - entity.posY;
        final double e = z + 0.5 - entity.posZ;
        final double h = Math.sqrt(d * d + e * e);
        return new float[] {(float)(Math.atan2(e, d) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(g, h) * 180.0 / 3.141592653589793))};
    }

    private static float getAngle360(final float dir, final float yaw) {
        final float f = Math.abs(yaw - dir) % 360.0f;
        return (f > 180.0f) ? (360.0f - f) : f;
    }

    public static class GCDFix
    {
        private float yaw;
        private float pitch;
        private final static Minecraft mc = Minecraft.getMinecraft();

        public GCDFix(final float yaw, final float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public static float getFixedRotation(final float rot) {
            return getDeltaMouse(rot) * getGCDValue();
        }

        public static float getGCDValue() {
            return (float)(getGCD() * 0.15);
        }

        public static float getGCD() {
            final float f1;
            return (f1 = (float)(mc.gameSettings.mouseSensitivity * 0.6 + 0.2)) * f1 * f1 * 8.0f;
        }

        public static float getDeltaMouse(final float delta) {
            return (float)Math.round(delta / getGCDValue());
        }

        public final float getYaw() {
            return this.yaw;
        }

        public final void setYaw(final float var1) {
            this.yaw = var1;
        }

        public final float getPitch() {
            return this.pitch;
        }

        public final void setPitch(final float var1) {
            this.pitch = var1;
        }

        @Override
        public String toString() {
            return "Rotation(yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
        }

        @Override
        public int hashCode() {
            return Float.hashCode(this.yaw) * 31 + Float.hashCode(this.pitch);
        }

        @Override
        public boolean equals(final Object var1) {
            if (this == var1) return true;
            if (var1 instanceof GCDFix) {
                final GCDFix var2 = (GCDFix) var1;
                return Float.compare(this.yaw, var2.yaw) == 0 && Float.compare(this.pitch, var2.pitch) == 0;
            }
            return false;
        }
    }
}