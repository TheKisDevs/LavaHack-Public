package com.kisman.cc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static List<Block> valid = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);

    public static boolean canSeePos(BlockPos pos) {
        return CrystalUtils.mc.world.rayTraceBlocks(new Vec3d(CrystalUtils.mc.player.posX, CrystalUtils.mc.player.posY + (double)CrystalUtils.mc.player.getEyeHeight(), CrystalUtils.mc.player.posZ), new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), false, true, false) == null;
    }

    public static boolean isEntityMoving(EntityLivingBase entityLivingBase) {
        return entityLivingBase.motionX > Double.longBitsToDouble(Double.doubleToLongBits(0.5327718501168097) ^ 0x7FE10C778D0F6544L) || entityLivingBase.motionY > Double.longBitsToDouble(Double.doubleToLongBits(0.07461435496686485) ^ 0x7FB319ED266512E7L) || entityLivingBase.motionZ > Double.longBitsToDouble(Double.doubleToLongBits(0.9006325807477794) ^ 0x7FECD1FB6B00C2E7L);
    }

    public static boolean canPlaceCrystal(final BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

            if (floor == Blocks.AIR && ceil == Blocks.AIR) return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty();
        }

        return false;
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean check, boolean entity, boolean multiPlace, boolean firePlace) {
        if(mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) {
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !(firePlace && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.FIRE))) return false;
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) return false;
            BlockPos boost = pos.add(0, 1, 0);
            return !entity || mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost.getX(), boost.getY(), boost.getZ(), boost.getX() + 1, boost.getY() + (check ? 2 : 1), boost.getZ() + 1), e -> !(e instanceof EntityEnderCrystal) || multiPlace).size() == 0;
        }
        return false;
    }

    /// Returns a BlockPos object of player's position floored.
    public static BlockPos GetPlayerPosFloored(final EntityPlayer p_Player) {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY), Math.floor(p_Player.posZ));
    }

    public static List<BlockPos> findCrystalBlocks(final EntityPlayer p_Player, float p_Range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(
                BlockInteractionHelper.getSphere(GetPlayerPosFloored(p_Player), p_Range, (int) p_Range, false, true, 0)
                        .stream().filter(CrystalUtils::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(EntityPlayer target, float range, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int x = target.getPosition().getX() - (int)range;
        while ((float)x <= (float) target.getPosition().getX() + range) {
            int z = target.getPosition().getZ() - (int)range;
            while ((float)z <= (float) target.getPosition().getZ() + range) {
                int y;
                int n = y = !sphere ? target.getPosition().getY() - (int)range : target.getPosition().getY();
                while ((float)y < (float) target.getPosition().getY() + range) {
                    double distance = (target.getPosition().getX() - x) * (target.getPosition().getX() - x) + (target.getPosition().getZ() - z) * (target.getPosition().getZ() - z) + (!sphere ? (target.getPosition().getY() - y) * (target.getPosition().getY() - y) : 0);
                    if (distance < (double)(range * range) && (!hollow || distance >= ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(638.4060856917202) ^ 0x7F73F33FA9DAEA7FL)) * ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(13.015128470890444) ^ 0x7FDA07BEEB3F6D07L)))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return blocks;
    }

    public static List<BlockPos> getSphere(float radius, boolean ignoreAir) {
        ArrayList<BlockPos> sphere = new ArrayList<>();
        BlockPos pos = new BlockPos(mc.player.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = ( int ) radius;
        int x = posX - radiuss;
        while (( float ) x <= ( float ) posX + radius) {
            int z = posZ - radiuss;
            while (( float ) z <= ( float ) posZ + radius) {
                int y = posY - radiuss;
                while (( float ) y < ( float ) posY + radius) {
                    if (( float ) ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return sphere;
    }

    public static List<BlockPos> getSphere(float range, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int x = mc.player.getPosition().getX() - (int)range;
        while ((float)x <= (float) mc.player.getPosition().getX() + range) {
            int z = mc.player.getPosition().getZ() - (int)range;
            while ((float)z <= (float) mc.player.getPosition().getZ() + range) {
                int y;
                int n = y = sphere != false ? mc.player.getPosition().getY() - (int)range : mc.player.getPosition().getY();
                while ((float)y < (float) mc.player.getPosition().getY() + range) {
                    double distance = ( mc.player.getPosition().getX() - x) * (mc.player.getPosition().getX() - x) + (mc.player.getPosition().getZ() - z) * (mc.player.getPosition().getZ() - z) + (sphere != false ? (mc.player.getPosition().getY() - y) * (mc.player.getPosition().getY() - y) : 0);
                    if (distance < (double)(range * range) && (hollow == false || distance >= ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(638.4060856917202) ^ 0x7F73F33FA9DAEA7FL)) * ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(13.015128470890444) ^ 0x7FDA07BEEB3F6D07L)))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return blocks;
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, boolean terrain) {
        return calculateDamage(world, posX, posY, posZ, entity, 0, terrain);
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, int interlopedAmount) {
        return calculateDamage(world, posX, posY, posZ, entity, interlopedAmount, false);
    }

    public static float calculateDamage(BlockPos pos, Entity entity, boolean terrain) {
        return calculateDamage(mc.world, pos.getX(), pos.getY(), pos.getZ(), entity, terrain);
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, int interlopedAmount, boolean terrain) {
        if (entity == mc.player) if (mc.player.capabilities.isCreativeMode) return 0.0f;

        float doubleExplosionSize = 12.0F;
        double dist = entity.getDistance(posX, posY, posZ);
        
        if (dist > doubleExplosionSize) return 0f;

        if (interlopedAmount > 0) {
            Vec3d l_Interloped = EntityUtil.getInterpolatedAmount(entity, interlopedAmount);
            dist = EntityUtil.getDistance(l_Interloped.x, l_Interloped.y, l_Interloped.z, posX, posY, posZ);
        }

        double distancedsize = dist / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity =  0;

        try {
            if(terrain) blockDensity = getBlockDensity(vec3d, entity.getEntityBoundingBox());
            else blockDensity =  entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignored) {}

        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase) finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(world, damage), new Explosion(world, null, posX, posY, posZ, 6F, false, true));
        return (float) finald;
    }

    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        final double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        final double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        final double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        final double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        float j2 = 0.0f;
        float k2 = 0.0f;
        for (float f = 0.0f; f <= 1.0f; f += ( float ) d0) {
            for (float f2 = 0.0f; f2 <= 1.0f; f2 += ( float ) d2) {
                for (float f3 = 0.0f; f3 <= 1.0f; f3 += ( float ) d3) {
                    final double d6 = bb.minX + (bb.maxX - bb.minX) * f;
                    final double d7 = bb.minY + (bb.maxY - bb.minY) * f2;
                    final double d8 = bb.minZ + (bb.maxZ - bb.minZ) * f3;
                    if (rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec, false, true, false) == null) {
                        ++j2;
                    }
                    ++k2;
                }
            }
        }
        return j2 / k2;
    }

    //moneymod
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = mc.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (!valid.contains(block)) {
                    block = Blocks.AIR;
                    iblockstate = Blocks.AIR.getBlockState().getBaseState();
                }

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = ( double ) l + 1.0D;
                    } else if (i < l) {
                        d0 = ( double ) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = ( double ) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = ( double ) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = ( double ) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = ( double ) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!valid.contains(block1)) {
                        block1 = Blocks.AIR;
                        iblockstate1 = Blocks.AIR.getBlockState().getBaseState();
                    }

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {

                            return iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
                    (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage -= damage / 4;
            }
            // damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(final World p_World, float damage) {
        int diff = p_World.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(final World world, EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(world, crystal.posX, crystal.posY, crystal.posZ, entity, 0);
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean check) {
        return canPlaceCrystal(blockPos, check, true);
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean check, final boolean entity) {
        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) return false;
        final BlockPos boost = blockPos.add(0, 1, 0);
        return mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() == Blocks.AIR && (!entity || mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost.getX(), boost.getY(), boost.getZ(), (boost.getX() + 1), (boost.getY() + (check ? 2 : 1)), (boost.getZ() + 1)), e -> !(e instanceof EntityEnderCrystal)).size() == 0);
    }

    public static float calculateDamage(World world, BlockPos pos, Entity entity) {
        return calculateDamage(world, pos.getX(), pos.getY(), pos.getZ(), entity, 0);
    }

    public static float calculateDamage(@Nullable WorldClient world, float fl, int i, double d, @NotNull Entity target, boolean terrain, boolean kotlin) {
        return calculateDamage(world, fl, i, d, target, terrain);
    }
}