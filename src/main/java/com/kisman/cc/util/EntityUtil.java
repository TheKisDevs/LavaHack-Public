package com.kisman.cc.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.combat.AntiBot;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

/**
 * @author 086
 * @author Crystallinqq/Auto
 * @author _kisman_
 */

public class EntityUtil {
    private static final DamageSource EXPLOSION_SOURCE;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isBlockAboveHead(EntityPlayer target) {
        AxisAlignedBB bb = new AxisAlignedBB(target.posX - 0.3, target.posY + (double) target.getEyeHeight(), target.posZ + 0.3, target.posX + 0.3, target.posY + 2.5, target.posZ - 0.3);
        return !mc.world.getCollisionBoxes(target, bb).isEmpty();
    }

    public static float getHealth(EntityPlayer entity) {
        return entity.getHealth();
    }

    public static boolean isFluid(double y) {
        return mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + y, mc.player.posZ)).getBlock().equals(Blocks.WATER);
    }

    public static boolean canSee(BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), false, true, false) == null;
    }

    public static double getSpeedBPS(final Entity entity) {
        final double tX = Math.abs(entity.posX - entity.prevPosX);
        final double tZ = Math.abs(entity.posZ - entity.prevPosZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);
        length *= EntityUtil.mc.getRenderPartialTicks();
        return length * 20.0;
    }

    public static boolean isOnLiquid() {
        final double y = EntityUtil.mc.player.posY - 0.03;
        for (int x = MathHelper.floor(EntityUtil.mc.player.posX); x < MathHelper.ceil(EntityUtil.mc.player.posX); ++x) {
            for (int z = MathHelper.floor(EntityUtil.mc.player.posZ); z < MathHelper.ceil(EntityUtil.mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (EntityUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) return true;
            }
        }
        return false;
    }

    public static boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }

    public static BlockPos getRoundedBlockPos(final Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static boolean stopSneaking(final boolean isSneaking) {
        if (isSneaking && mc.player != null) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        return false;
    }

    public static EntityPlayer getTarget(final float range, float wallRange) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if(!antibotCheck(player) && AntiBot.instance.isToggled() && AntiBot.instance.mode.checkValString("Zamorozka")) continue;
            if (!isntValid(player, range, wallRange)) {
                if (currentTarget == null) currentTarget = player;
                else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) currentTarget = player;
            }
        }
        return currentTarget;
    }

    public static EntityPlayer getTarget(final float range) {
        return getTarget(range, range);
    }

    public static Entity getTarget(float range, float wallRange, boolean players, boolean animals, boolean monsters) {
        Entity currentTarget = null;
        for (Entity entity1 : mc.world.loadedEntityList) {
            if(!(entity1 instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) entity1;
            if(!antibotCheck(entity) && AntiBot.instance.isToggled() && AntiBot.instance.mode.checkValString("Zamorozka")) continue;
            if (!isntValid(entity, range, wallRange) && !isntValid2(entity, players, animals, monsters)) {
                if (currentTarget == null) currentTarget = entity;
                else if (mc.player.getDistanceSq(entity) < mc.player.getDistanceSq(currentTarget)) currentTarget = entity;
            }
        }
        return currentTarget;
    }

    public static boolean isntValid(final EntityLivingBase entity, final double range, double wallRange) {
        return (mc.player.getDistance(entity) > (mc.player.canEntityBeSeen(entity) ? range : wallRange)) || entity == mc.player || entity.getHealth() <= 0.0f || entity.isDead || FriendManager.instance.isFriend(entity.getName());
    }

    public static boolean antibotCheck(EntityLivingBase entity) {
        return (Kisman.target_by_click != null && Kisman.target_by_click.equals(entity));
    }

    public static boolean isntValid2(final EntityLivingBase entity, boolean players, boolean animals, boolean monsters) {
        return (players && entity instanceof EntityPlayer) || (animals && isPassive(entity)) || (monsters && isMobAggressive(entity));
    }

    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) return false;
        if (e instanceof EntityAgeable || e instanceof EntityAmbientCreature || e instanceof EntitySquid) return true;
        return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            // arms raised = aggressive, angry = either game or we have set the anger
            // cooldown
            if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) return true;
        } else if (entity instanceof EntityWolf) return ((EntityWolf) entity).isAngry() && !Minecraft.getMinecraft().player.equals(((EntityWolf) entity).getOwner());
        else if (entity instanceof EntityEnderman) return ((EntityEnderman) entity).isScreaming();
        return isHostileMob(entity);
    }

    /**
     * If the mob by default wont attack the player, but will if the player attacks
     * it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    /**
     * If the mob is friendly (not aggressive)
     */
    public static boolean isFriendlyMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.CREATURE, false) && !EntityUtil.isNeutralMob(entity)) || (entity.isCreatureType(EnumCreatureType.AMBIENT, false)) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !EntityUtil.isMobAggressive(entity));
    }

    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity));
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        }
        return block;
    }

    public static boolean isInLiquid(boolean feet) {
        if (mc.player != null) {
            if (mc.player.fallDistance >= 3.0f) return false;
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
            int y = MathHelper.floor(bb.minY - (feet ? 0.03 : 0.2));
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) return false;
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static boolean isInLiquid() {
        return isInLiquid(false);
    }

    public static float calculate(final double posX, final double posY, final double posZ, final EntityLivingBase entity) {
        final double v = (1.0 - entity.getDistance(posX, posY, posZ) / 12.0) * getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
        return getBlastReduction(entity, getDamageMultiplied((float)((v * v + v) / 2.0 * 85.0 + 1.0)));
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI) {
        float damage = damageI;
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        damage *= 1.0f - MathHelper.clamp((float) EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EntityUtil.EXPLOSION_SOURCE), 0.0f, 20.0f) / 25.0f;
        if (entity.isPotionActive(MobEffects.RESISTANCE)) return damage - damage / 4.0f;
        return damage;
    }

    public static float getDamageMultiplied(final float damage) {
        final int diff = EntityUtil.mc.world.getDifficulty().getDifficultyId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        final double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        final double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        final double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        final double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        float j2 = 0.0f;
        float k2 = 0.0f;
        for (float f = 0.0f; f <= 1.0f; f += (float)d0) {
            for (float f2 = 0.0f; f2 <= 1.0f; f2 += (float)d2) {
                for (float f3 = 0.0f; f3 <= 1.0f; f3 += (float)d3) {
                    final double d6 = bb.minX + (bb.maxX - bb.minX) * f;
                    final double d7 = bb.minY + (bb.maxY - bb.minY) * f2;
                    final double d8 = bb.minZ + (bb.maxZ - bb.minZ) * f3;
                    if (rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec, false, false, false) == null) ++j2;
                    ++k2;
                }
            }
        }
        return j2 / k2;
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, final Vec3d vec32, final boolean stopOnLiquid, final boolean ignoreBlockWithoutBoundingBox, final boolean returnLastUncollidableBlock) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i2 = MathHelper.floor(vec31.y);
        int j2 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i2, j2);
        final IBlockState iblockstate = EntityUtil.mc.world.getBlockState(blockpos);
        final Block block = iblockstate.getBlock();
        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid))
        return iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);

        RayTraceResult raytraceresult2 = null;
    int k2 = 200;
        while (k2-- >= 0) {
        if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
            return null;
        }
        if (l == i && i2 == j && j2 == k) {
            return returnLastUncollidableBlock ? raytraceresult2 : null;
        }
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        double d0 = 999.0;
        double d2 = 999.0;
        double d3 = 999.0;
        if (i > l) {
            d0 = l + 1.0;
        }
        else if (i < l) {
            d0 = l + 0.0;
        }
        else {
            flag2 = false;
        }
        if (j > i2) {
            d2 = i2 + 1.0;
        }
        else if (j < i2) {
            d2 = i2 + 0.0;
        }
        else {
            flag3 = false;
        }
        if (k > j2) {
            d3 = j2 + 1.0;
        }
        else if (k < j2) {
            d3 = j2 + 0.0;
        }
        else {
            flag4 = false;
        }
        double d4 = 999.0;
        double d5 = 999.0;
        double d6 = 999.0;
        final double d7 = vec32.x - vec31.x;
        final double d8 = vec32.y - vec31.y;
        final double d9 = vec32.z - vec31.z;
        if (flag2) {
            d4 = (d0 - vec31.x) / d7;
        }
        if (flag3) {
            d5 = (d2 - vec31.y) / d8;
        }
        if (flag4) {
            d6 = (d3 - vec31.z) / d9;
        }
        if (d4 == -0.0) {
            d4 = -1.0E-4;
        }
        if (d5 == -0.0) {
            d5 = -1.0E-4;
        }
        if (d6 == -0.0) {
            d6 = -1.0E-4;
        }
        EnumFacing enumfacing;
        if (d4 < d5 && d4 < d6) {
            enumfacing = ((i > l) ? EnumFacing.WEST : EnumFacing.EAST);
            vec31 = new Vec3d(d0, vec31.y + d8 * d4, vec31.z + d9 * d4);
        }
        else if (d5 < d6) {
            enumfacing = ((j > i2) ? EnumFacing.DOWN : EnumFacing.UP);
            vec31 = new Vec3d(vec31.x + d7 * d5, d2, vec31.z + d9 * d5);
        }
        else {
            enumfacing = ((k > j2) ? EnumFacing.NORTH : EnumFacing.SOUTH);
            vec31 = new Vec3d(vec31.x + d7 * d6, vec31.y + d8 * d6, d3);
        }
        l = MathHelper.floor(vec31.x) - ((enumfacing == EnumFacing.EAST) ? 1 : 0);
        i2 = MathHelper.floor(vec31.y) - ((enumfacing == EnumFacing.UP) ? 1 : 0);
        j2 = MathHelper.floor(vec31.z) - ((enumfacing == EnumFacing.SOUTH) ? 1 : 0);
        blockpos = new BlockPos(l, i2, j2);
        final IBlockState iblockstate2 = EntityUtil.mc.world.getBlockState(blockpos);
        final Block block2 = iblockstate2.getBlock();
        if (ignoreBlockWithoutBoundingBox && iblockstate2.getMaterial() != Material.PORTAL && iblockstate2.getCollisionBoundingBox(mc.world, blockpos) == Block.NULL_AABB) {
            continue;
        }
        if (block2.canCollideCheck(iblockstate2, stopOnLiquid) && !(block2 instanceof BlockWeb)) {
            return iblockstate2.collisionRayTrace(mc.world, blockpos, vec31, vec32);
        }
        raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
    }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }

    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
        List<BlockPos> squareBlocks = new ArrayList<>();
        int x1 = pos1.getX();
        int y1 = pos1.getY();
        int z1 = pos1.getZ();
        int x2 = pos2.getX();
        int y2 = pos2.getY();
        int z2 = pos2.getZ();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x += 1) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z += 1) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y += 1) {
                    squareBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return squareBlocks;
    }

    public static double[] calculateLookAt(double px, double py, double pz, Entity me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw, pitch};
    }

    public static boolean basicChecksEntity(Entity pl) {
        return pl.getName().equals(mc.player.getName()) || pl.isDead;
    }

    public static BlockPos getPosition(Entity pl) {
        return new BlockPos(Math.floor(pl.posX), Math.floor(pl.posY), Math.floor(pl.posZ));
    }

    public static List<BlockPos> getBlocksIn(Entity pl) {
        List<BlockPos> blocks = new ArrayList<>();
        AxisAlignedBB bb = pl.getEntityBoundingBox();
        for (double x = Math.floor(bb.minX); x < Math.ceil(bb.maxX); x++) {
            for (double y = Math.floor(bb.minY); y < Math.ceil(bb.maxY); y++) {
                for (double z = Math.floor(bb.minZ); z < Math.ceil(bb.maxZ); z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static void setTimer(float speed) {
        mc.timer.tickLength = 50.0f / speed;
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    public static double getDistance(double p_X, double p_Y, double p_Z, double x, double y, double z) {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    static {
        EXPLOSION_SOURCE = new DamageSource("explosion").setDifficultyScaled().setExplosion();
    }
}
