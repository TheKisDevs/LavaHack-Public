package com.kisman.cc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean shouldBreakArmor(EntityLivingBase entity, int targetPercent) {
        for (ItemStack stack : entity.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR) {
                return true;
            }
            float armorPercent = (float)(stack.getMaxDamage() - stack.getItemDamage()) / (float)stack.getMaxDamage() * Float.intBitsToFloat(Float.floatToIntBits(0.11806387f) ^ 0x7F39CB78);
            if (!(targetPercent >= armorPercent) || stack.stackSize >= 2) continue;
            return true;
        }
        return false;
    }

    public static boolean isArmorLow(EntityPlayer player , int durability ) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if ( piece == null ) return true;
            else if ( getItemDamage ( piece ) < durability ) return true;
        }
        return false;
    }

    public static
    boolean isNaked ( EntityPlayer player ) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if ( piece == null || piece.isEmpty ( ) ) continue;
            return false;
        }
        return true;
    }

    public static int getItemDamage ( ItemStack stack ) {
        return stack.getMaxDamage ( ) - stack.getItemDamage ( );
    }

    public static float getDamageInPercent ( ItemStack stack ) {
        return ( getItemDamage ( stack ) / (float) stack.getMaxDamage ( ) ) * 100;
    }

    public static
    int getRoundedDamage ( ItemStack stack ) {
        return (int) getDamageInPercent ( stack );
    }

    public static boolean hasDurability ( ItemStack stack ) {
        Item item = stack.getItem ( );
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static boolean canBreakWeakness ( EntityPlayer player ) {
        int strengthAmp = 0;
        PotionEffect effect = mc.player.getActivePotionEffect ( MobEffects.STRENGTH );
        if ( effect != null ) strengthAmp = effect.getAmplifier ( );

        return ! mc.player.isPotionActive ( MobEffects.WEAKNESS ) || strengthAmp >= 1 || mc.player.getHeldItemMainhand ( ).getItem ( ) instanceof ItemSword || mc.player.getHeldItemMainhand ( ).getItem ( ) instanceof ItemPickaxe || mc.player.getHeldItemMainhand ( ).getItem ( ) instanceof ItemAxe || mc.player.getHeldItemMainhand ( ).getItem ( ) instanceof ItemSpade;
    }

    public static float calculateDamage ( double posX , double posY , double posZ , Entity entity ) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance ( posX , posY , posZ ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d ( posX , posY , posZ );
        double blockDensity = 0;
        try {blockDensity = entity.world.getBlockDensity ( vec3d , entity.getEntityBoundingBox ( ) );} catch ( Exception ignored ) {}
        double v = ( 1.0D - distancedsize ) * blockDensity;
        float damage = (float) ( (int) ( ( v * v + v ) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D ) );
        double finald = 1;
        if ( entity instanceof EntityLivingBase ) finald = getBlastReduction ( (EntityLivingBase) entity , getDamageMultiplied ( damage ) , new Explosion ( mc.world , null , posX , posY , posZ , 6F , false , true ) );
        return (float) finald;
    }

    public static float getBlastReduction ( EntityLivingBase entity , float damageI , Explosion explosion ) {
        float damage = damageI;
        if ( entity instanceof EntityPlayer ) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage ( explosion );
            damage = CombatRules.getDamageAfterAbsorb ( damage , (float) ep.getTotalArmorValue ( ) , (float) ep.getEntityAttribute ( SharedMonsterAttributes.ARMOR_TOUGHNESS ).getAttributeValue ( ) );

            int k = 0;
            try {k = EnchantmentHelper.getEnchantmentModifierDamage ( ep.getArmorInventoryList ( ) , ds );} catch ( Exception ignored ) {}
            float f = MathHelper.clamp ( k , 0.0F , 20.0F );
            damage = damage * ( 1.0F - f / 25.0F );

            if ( entity.isPotionActive ( MobEffects.RESISTANCE ) ) {
                damage = damage - ( damage / 4 );
            }

            damage = Math.max ( damage , 0.0F );
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb ( damage , (float) entity.getTotalArmorValue ( ) , (float) entity.getEntityAttribute ( SharedMonsterAttributes.ARMOR_TOUGHNESS ).getAttributeValue ( ) );
        return damage;
    }

    public static float getDamageMultiplied ( float damage ) {
        int diff = mc.world.getDifficulty().getDifficultyId();
        return damage * ( diff == 0 ? 0 : ( diff == 2 ? 1 : ( diff == 1 ? 0.5f : 1.5f ) ) );
    }

    public static float calculateDamage ( Entity crystal , Entity entity ) {
        return calculateDamage ( crystal.posX , crystal.posY , crystal.posZ , entity );
    }

    public static float calculateDamageAlt ( final Entity crystal , final Entity entity ) {
        final BlockPos cPos = new BlockPos ( crystal.posX , crystal.posY , crystal.posZ );
        return calculateDamage ( cPos.getX ( ) , cPos.getY ( ) , cPos.getZ ( ) , entity );
    }

    public static float calculateDamage ( BlockPos pos , Entity entity ) {
        return calculateDamage ( pos.getX ( ) + .5 , pos.getY ( ) + 1 , pos.getZ ( ) + .5 , entity );
    }

    public static boolean canTakeDamage ( boolean suicide ) {
        return ! mc.player.capabilities.isCreativeMode && ! suicide;
    }

    public static int getCooldownByWeapon ( EntityPlayer player ) {
        Item item = player.getHeldItemMainhand ( ).getItem ( );
        if ( item instanceof ItemSword ) {
            return 600;
        }

        if ( item instanceof ItemPickaxe ) {
            return 850;
        }

        if ( item == Items.IRON_AXE ) {
            return 1100;
        }

        if ( item == Items.STONE_HOE ) {
            return 500;
        }

        if ( item == Items.IRON_HOE ) {
            return 350;
        }

        if ( item == Items.WOODEN_AXE || item == Items.STONE_AXE ) {
            return 1250;
        }

        if ( item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE ) {
            return 1000;
        }

        return 250;
    }

}
