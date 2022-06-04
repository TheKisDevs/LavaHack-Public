package com.kisman.cc.util;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.*;
import java.util.*;
import java.util.List;

public class MathUtil {
    public static double[] getCircleCentre(double[] coord, double radius) {
        return new double[] {coord[0] + radius, coord[1] + radius};
    }

    public static double degToRad(double deg) {
        return deg * (float) (Math.PI / 180.0f);
    }

    public static Vec3d direction(float yaw) {
        return new Vec3d(Math.cos(degToRad(yaw + 90f)), 0, Math.sin(degToRad(yaw + 90f)));
    }

    public static AxisAlignedBB blockPosToDefaultBB(BlockPos pos) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
    }

    /**
     * @author DarkStorm
     */
    public static Point calculateMouseLocation() {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = mc.gameSettings.guiScale;
        if (scale == 0) scale = 1000;
        int scaleFactor = 0;
        while (scaleFactor < scale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) scaleFactor++;
        return new Point(Mouse.getX() / scaleFactor, mc.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }

    public static Vec3d getInterpolatedRenderPos(final Entity entity, final float ticks) {
        return interpolateEntity(entity, ticks).subtract(Minecraft.getMinecraft().getRenderManager().viewerPosX, Minecraft.getMinecraft().getRenderManager().viewerPosY, Minecraft.getMinecraft().getRenderManager().viewerPosZ);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static double getInputFromPercent(final double value, final double minInput, final double maxInput) {
        return (value * (maxInput - minInput)) / 100 + minInput;
    }

    public static Vec3d mult(Vec3d factor, Vec3d multiplier) {
        return new Vec3d(factor.x * multiplier.x, factor.y * multiplier.y, factor.z * multiplier.z);
    }

    public static Vec3d mult(Vec3d factor, float multiplier) {
        return new Vec3d(factor.x * multiplier, factor.y * multiplier, factor.z * multiplier);
    }

    public static Vec3d div(Vec3d factor, Vec3d divisor) {
        return new Vec3d(factor.x / divisor.x, factor.y / divisor.y, factor.z / divisor.z);
    }

    public static Vec3d div(Vec3d factor, float divisor) {
        return new Vec3d(factor.x / divisor, factor.y / divisor, factor.z / divisor);
    }

    public static double[] directionSpeedNoForward(double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = 1f;

        if (mc.gameSettings.keyBindLeft.isPressed() || mc.gameSettings.keyBindRight.isPressed() || mc.gameSettings.keyBindBack.isPressed() || mc.gameSettings.keyBindForward.isPressed()) forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0) yaw += (forward > 0 ? -45 : 45);
            else if (side < 0) yaw += (forward > 0 ? 45 : -45);
            side = 0;
            // forward = clamp(forward, 0, 1);
            if (forward > 0) forward = 1;
            else if (forward < 0) forward = -1;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

    public static double roundDouble(double number, int scale) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float roundFloat(double number, int scale) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(scale, RoundingMode.FLOOR);
        return bd.floatValue();
    }

    private static final Random random = new Random();

    public static int getRandom(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static double getRandom(double min, double max) {
        return MathHelper.clamp(min + random.nextDouble() * max, min, max);
    }

    public static float getRandom(float min, float max) {
        return MathHelper.clamp(min + random.nextFloat() * max, min, max);
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float sin(float value) {
        return MathHelper.sin(value);
    }

    public static float cos(float value) {
        return MathHelper.cos(value);
    }

    public static float wrapDegrees(float value) {
        return MathHelper.wrapDegrees(value);
    }

    public static double wrapDegrees(double value) {
        return MathHelper.wrapDegrees(value);
    }

    public static Vec3d roundVec(Vec3d vec3d, int places) {
        return new Vec3d(MathUtil.round(vec3d.x, places), MathUtil.round(vec3d.y, places), MathUtil.round(vec3d.z, places));
    }

    public static double square(double input) {
        return input * input;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    public static float wrap(float valI) {
        float val = valI % 360.0f;
        if (val >= 180.0f) val -= 360.0f;
        if (val < -180.0f) val += 360.0f;
        return val;
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.floatValue();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        LinkedList<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        if (descending) list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        else list.sort(Map.Entry.comparingByValue());
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry entry : list) result.put(entry.getKey(), entry.getValue());
        return result;
    }

    public static String getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(11);
        if (timeOfDay < 12) return "Good Morning";
        if (timeOfDay < 16) return "Good Afternoon";
        if (timeOfDay < 21) return "Good Evening";
        return "Good Night";
    }

    public static double radToDeg(double rad) {
        return rad * (double) 57.29578f;
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return (double) Math.round(val * one) / one;
    }

    public static double[] directionSpeed(double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
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
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static List<Vec3d> getBlockBlocks(Entity entity) {
        ArrayList<Vec3d> vec3ds = new ArrayList<>();
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        double y = entity.posY;
        double minX = MathUtil.round(bb.minX, 0);
        double minZ = MathUtil.round(bb.minZ, 0);
        double maxX = MathUtil.round(bb.maxX, 0);
        double maxZ = MathUtil.round(bb.maxZ, 0);
        if (minX != maxX) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(maxX, y, minZ));
            if (minZ != maxZ) {
                vec3ds.add(new Vec3d(minX, y, maxZ));
                vec3ds.add(new Vec3d(maxX, y, maxZ));
                return vec3ds;
            }
        } else if (minZ != maxZ) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(minX, y, maxZ));
            return vec3ds;
        }
        vec3ds.add(entity.getPositionVector());
        return vec3ds;
    }

    public static boolean areVec3dsAligned(Vec3d vec3d1, Vec3d vec3d2) {
        return MathUtil.areVec3dsAlignedRetarded(vec3d1, vec3d2);
    }

    public static boolean areVec3dsAlignedRetarded(Vec3d vec3d1, Vec3d vec3d2) {
        BlockPos pos1 = new BlockPos(vec3d1);
        BlockPos pos2 = new BlockPos(vec3d2.x, vec3d1.y, vec3d2.z);
        return pos1.equals(pos2);
    }

    public static double distance(float x, float y, float x1, float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }

    public static double cot(double x) { return 1 / Math.tan(x); }

    public static Vec2i getEndPortalCoords(int x1, int z1, float a1, int x2, int z2, float a2, boolean debug) {
        Vec2i coord = new Vec2i();
        double p = Math.PI / 180;

        if(Math.abs(a1 - a2) < 1) {
            if(debug) {
                ChatUtils.error("The angles cannot be equal");
                return null;
            }
        } else if((((a1 < 0) && (a2 > 0)) || ((a1 > 0) && (a2 < 0))) && (Math.abs(Math.abs(Math.abs(a1) - 180) - Math.abs(a2)) < 1)) {
            if(debug) {
                ChatUtils.error("The angles cannot be opposite");
                return null;
            }
        } else {
            switch (Math.round(a1)) {
                case -180: {}
                case 0: {}
                case 180: {
                    coord.x = Math.round(x1);
                    coord.y = (int) Math.round(cot(-a2 * p) * x1 - (x2 * cot(-a2 * p) - z2));
                    break;
                }
                case -90: {}
                case 90: {
                    coord.x = Math.round(z1);
                    coord.y = (int) Math.round(Math.round(x2 * cot(-a2 * p) - z2 + z1) / cot(-a2 * p));
                    break;
                }
                default: {
                    switch (Math.round(a2)) {
                        case -180: {}
                        case 0: {}
                        case 180: {
                            coord.x = Math.round(x2);
                            coord.y = (int) Math.round(cot(-a1 * p) * x2 - (x1 * cot(-a1 * p) - z1));
                            break;
                        }
                        case -90: {}
                        case 90: {
                            coord.x = Math.round(z2);
                            coord.y = (int) Math.round(Math.round(x1 * cot(-a1 * p) - z1 + z2) / cot(-a1 * p));
                            break;
                        }
                        default: {
                            coord.x = (int) Math.round(((x1 * cot(-a1 * p) - z1) - (x2 * cot(-a2 * p) - z2))/(cot(-a1 * p) - cot(-a2 * p)));
                            coord.y = (int) Math.round(cot(-a1 *p) * coord.x-(x1 * cot(-a1 * p)-z1));
                        }
                    }
                }
            }
        }

        return coord;
    }
    
    public static double lerp(double from, double to, double delta) {
        return from + (to - from) * delta;
    }

    public static double roundHalf(double a){
        if(a != a) // a is NaN
            return Double.NaN;
        double x = a < 0.0 ? 0.0 - a : a; // abs
        double c = (long) a; // casting to long truncates
        if(x > 0x10000000000000L || c == a) // x is an integer or bigger than 1 << 52
            return a; // so we just have to return a
        c = c < 0.0 ? c - 1.0 :  c;
        return c + 0.5; // after we floor, we add a half
    }
}
