package part.kotopushka.lavahack.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.kisman.cc.module.render.Breadcrumbs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class EntityHelper
        extends Breadcrumbs.Helper {
    static Minecraft mc = Minecraft.getMinecraft();
    public static List<Block> noSolidBlocks = Arrays.asList(Blocks.AIR, Blocks.LAVA, Blocks.WATER, Blocks.SNOW_LAYER, Blocks.FIRE, Blocks.TALLGRASS);
    public static List<Block> rightClickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.STONE_BUTTON, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.TRAPDOOR, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON);

    public EntityHelper(Vec3d vec) {
        super(vec);
    }

    public static double getDistance(double x, double y, double z, double x1, double y1, double z1) {
        double posX = x - x1;
        double posY = y - y1;
        double posZ = z - z1;
        return MathHelper.sqrt(posX * posX + posY * posY + posZ * posZ);
    }

    public static void damagePlayer(boolean groundCheck) {
        if (!groundCheck || EntityHelper.mc.player.onGround) {
            double x = EntityHelper.mc.player.posX;
            double y = EntityHelper.mc.player.posY;
            double z = EntityHelper.mc.player.posZ;
            double fallDistanceReq = 3.0;
            if (EntityHelper.mc.player.isPotionActive(Potion.getPotionById(8))) {
                int amplifier = EntityHelper.mc.player.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier();
                fallDistanceReq += (double)(amplifier + 1);
            }
            for (int i = 0; i < (int)Math.ceil(fallDistanceReq / 0.0624); ++i) {
                EntityHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.0624, z, false));
                EntityHelper.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
            }
            EntityHelper.mc.player.connection.sendPacket(new CPacketPlayer(true));
        }
    }

    public static boolean intersectsWith(BlockPos pos) {
        return !EntityHelper.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos), v -> v != null && !v.isDead).isEmpty();
    }

    public static int getBestWeapon() {
        int originalSlot = EntityHelper.mc.player.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        for (int slot = 0; slot < 9; slot = (int)((byte)(slot + 1))) {
            EntityHelper.mc.player.inventory.currentItem = slot;
            ItemStack itemStack = EntityHelper.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemSpade) continue;
            float damage = EntityHelper.getItemDamage(itemStack);
            if (!((damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED)) > weaponDamage)) continue;
            weaponDamage = damage;
            weaponSlot = slot;
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)EntityHelper.getDamageInPercent(stack);
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (float)EntityHelper.getItemDamage(stack) / (float)stack.getMaxDamage() * 100.0f;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                return true;
            }
            if (EntityHelper.getItemDamage(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = EntityHelper.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!EntityHelper.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(EntityHelper.mc.world.getBlockState(neighbour), false) || (blockState = EntityHelper.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || EntityHelper.mc.world.rayTraceBlocks(new Vec3d(EntityHelper.mc.player.posX, EntityHelper.mc.player.posY + (double)EntityHelper.mc.player.getEyeHeight(), EntityHelper.mc.player.posZ), new Vec3d(pos.getX(), (float)pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return EntityHelper.rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return EntityHelper.rayTracePlaceCheck(pos, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = EntityHelper.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!EntityHelper.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Entity entity : EntityHelper.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : EntityHelper.getPossibleSides(pos)) {
            if (!EntityHelper.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockHelper.getBlock(pos).canCollideCheck(BlockHelper.getState(pos), false);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f1 = (float)(vec.y - (double)pos.getY());
            float f2 = (float)(vec.z - (double)pos.getZ());
            EntityHelper.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            EntityHelper.mc.playerController.processRightClickBlock(EntityHelper.mc.player, EntityHelper.mc.world, pos, direction, vec, hand);
        }
        EntityHelper.mc.player.swingArm(EnumHand.MAIN_HAND);
        EntityHelper.mc.rightClickDelayTimer = 4;
    }

    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = EntityHelper.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = EntityHelper.mc.world.getBlockState(neighbour).getBlock();
        if (!EntityHelper.mc.player.isSneaking()) {
            EntityHelper.mc.player.connection.sendPacket(new CPacketEntityAction(EntityHelper.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        EntityHelper.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        EntityHelper.mc.player.swingArm(EnumHand.MAIN_HAND);
        EntityHelper.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return EntityHelper.getDistance(entity.prevPosX, entity.posY, entity.prevPosZ, entity.posX, entity.posY, entity.posZ) > 9.0E-4;
    }

    public static int getPotionModifier(EntityLivingBase entity, Potion potion) {
        PotionEffect effect = entity.getActivePotionEffect(potion);
        return effect != null ? effect.getAmplifier() + 1 : 0;
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        double d0 = x1 - x2;
        double d1 = z1 - z2;
        return MathHelper.sqrt(d0 * d0 + d1 * d1);
    }

    public static EntityLivingBase rayCast(Entity e, double range) {
        Vec3d vec = e.getPositionVector().add(new Vec3d(0.0, e.getEyeHeight(), 0.0));
        Vec3d vec1 = EntityHelper.mc.player.getPositionVector().add(new Vec3d(0.0, EntityHelper.mc.player.getEyeHeight(), 0.0));
        AxisAlignedBB axis = EntityHelper.mc.player.getEntityBoundingBox().offset(vec.x - vec1.x, vec.y - vec1.y, vec.z - vec1.z).expand(1.0, 1.0, 1.0);
        Entity nearst = null;
        for (Entity obj : EntityHelper.mc.world.getEntitiesWithinAABBExcludingEntity(EntityHelper.mc.player, axis)) {
            Vec3d vec2;
            Entity en = obj;
            if (!en.canBeCollidedWith() || !(en instanceof EntityLivingBase)) continue;
            float size = en.getCollisionBorderSize();
            AxisAlignedBB axis1 = en.getEntityBoundingBox().expand(size, size, size);
            RayTraceResult mop = axis1.calculateIntercept(vec1, vec);

            if (mop == null) continue;
            double dist = vec1.distanceTo(mop.hitVec);
            if (range != 0.0 && !(dist < range)) continue;
            nearst = en;
            vec2 = mop.hitVec;
            range = dist;
        }
        return (EntityLivingBase)nearst;
    }

    public static boolean isTeamWithYou(EntityLivingBase entity) {
        if (EntityHelper.mc.player != null && entity != null && EntityHelper.mc.player.getDisplayName() != null && entity.getDisplayName() != null) {
            if (EntityHelper.mc.player.getTeam() != null && entity.getTeam() != null && EntityHelper.mc.player.getTeam().isSameTeam(entity.getTeam())) {
                return true;
            }
            String targetName = entity.getDisplayName().getFormattedText().replace("\u00a7r", "");
            String clientName = EntityHelper.mc.player.getDisplayName().getFormattedText().replace("\u00a7r", "");
            return targetName.startsWith("\u00a7" + clientName.charAt(1));
        }
        return false;
    }

    public static boolean checkArmor(Entity entity) {
        return ((EntityLivingBase)entity).getTotalArmorValue() < 1;
    }

    public static int getPing(EntityPlayer entityPlayer) {
        if (entityPlayer == null) {
            return 0;
        }
        NetworkPlayerInfo networkPlayerInfo = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entityPlayer.getUniqueID());
        return networkPlayerInfo.getResponseTime();
    }

    public static String getName(NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    public static double getDistanceOfEntityToBlock(Entity entity, BlockPos pos) {
        return EntityHelper.getDistance(entity.posX, entity.posY, entity.posZ, pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(EntityHelper.getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return EntityHelper.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }
}


