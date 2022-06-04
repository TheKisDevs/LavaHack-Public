package com.kisman.cc.util;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void swingArm(Hand hand) {
        switch (hand) {
            case MAINHAND:
                mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            case OFFHAND:
                mc.player.swingArm(EnumHand.OFF_HAND);
                break;
            case PACKET:
                mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                break;
            case NONE:
                break;
        }
    }

    public static void fakeJump(int packets) {
        if (packets > 0 && packets != 5) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
        if (packets > 1) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + .419999986887, mc.player.posZ, true));
        if (packets > 2) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + .7531999805212, mc.player.posZ, true));
        if (packets > 3) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0013359791121, mc.player.posZ, true));
        if (packets > 4) mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1661092609382, mc.player.posZ, true));
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static List<EntityPlayer> getPlayersInRadius(double range) {
        return getPlayersInRadius(mc.player.getPositionVector(), range);
    }

    public static List<EntityPlayer> getPlayersInRadius(Vec3d center, double range) {
        return getEntitiesInRadius(EntityPlayer.class, center, range);
    }

    public static <T extends Entity> List<T> getEntitiesInRadius(Class<T> entityClass, Vec3d center, double range) {
        List<T> entity = new ArrayList<>();

        for(Entity entity1 : mc.world.loadedEntityList) {
            if(entity1.getDistance(mc.player) <= range) {
                entity.add((T) entity1);
            }
        }

        return entity;
    }

    // Find closest target
    // 0b00101010: replaced getDistance with getDistanceSq as speeds up calculation
    public static EntityPlayer findClosestTarget(double rangeMax, EntityPlayer aimTarget) {
        rangeMax *= rangeMax;
        List<EntityPlayer> playerList = mc.world.playerEntities;
        EntityPlayer closestTarget = null;

        for (EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity(entityPlayer)) continue;

            if (aimTarget == null && mc.player.getDistanceSq(entityPlayer) <= rangeMax) {
                closestTarget = entityPlayer;
                continue;
            }
            if (aimTarget != null && mc.player.getDistanceSq(entityPlayer) <= rangeMax && mc.player.getDistanceSq(entityPlayer) < mc.player.getDistanceSq(aimTarget)) {
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }

    // 0b00101010: replaced getDistance with getDistanceSq as speeds up calculation
    public static EntityPlayer findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;
        EntityPlayer closestTarget = null;

        for (EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity(entityPlayer)) continue;

            if (closestTarget == null) {
                closestTarget = entityPlayer;
                continue;
            }
            if (mc.player.getDistanceSq(entityPlayer) < mc.player.getDistanceSq(closestTarget)) {
                closestTarget = entityPlayer;
            }
        }

        return closestTarget;
    }

    public static double getEyeY(EntityPlayer player) {
        return player.getPositionVector().y + player.getEyeHeight();
    }

    // Find player you are looking
    public static EntityPlayer findLookingPlayer(double rangeMax) {
        // Get player
        ArrayList<EntityPlayer> listPlayer = new ArrayList<>();
        // Only who is in a distance of enemyRange
        for (EntityPlayer playerSin : mc.world.playerEntities) {
            if (EntityUtil.basicChecksEntity(playerSin))
                continue;
            if (mc.player.getDistance(playerSin) <= rangeMax) {
                listPlayer.add(playerSin);
            }
        }

        EntityPlayer target = null;
        // Get coordinate eyes + rotation
        Vec3d positionEyes = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        Vec3d rotationEyes = mc.player.getLook(mc.getRenderPartialTicks());
        // Precision
        int precision = 2;
        // Iterate for every blocks
        for (int i = 0; i < (int) rangeMax; i++) {
            // Iterate for the precision
            for (int j = precision; j > 0; j--) {
                // Iterate for all players
                for (EntityPlayer targetTemp : listPlayer) {
                    // Get box of the player
                    AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    // Get coordinate of the vec3d
                    double xArray = positionEyes.x + (rotationEyes.x * i) + rotationEyes.x / j;
                    double yArray = positionEyes.y + (rotationEyes.y * i) + rotationEyes.y / j;
                    double zArray = positionEyes.z + (rotationEyes.z * i) + rotationEyes.z / j;
                    // If it's inside
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray
                            && playerBox.maxX >= xArray && playerBox.minX <= xArray
                            && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        // Get target
                        target = targetTemp;
                    }
                }
            }
        }

        return target;
    }

    // TechAle: Return the health of the player
    public static float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static void centerPlayer(Vec3d centeredBlock) {
        double xDeviation = Math.abs(centeredBlock.x - mc.player.posX);
        double zDeviation = Math.abs(centeredBlock.z - mc.player.posZ);

        if (xDeviation <= 0.1 && zDeviation <= 0.1) centeredBlock = Vec3d.ZERO;
        else {
            double newX = -2;
            double newZ = -2;
            int xRel = (mc.player.posX < 0 ? -1 : 1);
            int zRel = (mc.player.posZ < 0 ? -1 : 1);
            if (BlockUtil.getBlock(mc.player.posX, mc.player.posY - 1, mc.player.posZ) instanceof BlockAir) {
                if (Math.abs((mc.player.posX % 1)) * 1E2 <= 30) newX = Math.round(mc.player.posX - (0.3 * xRel)) + 0.5 * -xRel;
                else if (Math.abs((mc.player.posX % 1)) * 1E2 >= 70) newX = Math.round(mc.player.posX + (0.3 * xRel)) - 0.5 * -xRel;
                if (Math.abs((mc.player.posZ % 1)) * 1E2 <= 30) newZ = Math.round(mc.player.posZ - (0.3 * zRel)) + 0.5 * -zRel;
                else if (Math.abs((mc.player.posZ % 1)) * 1E2 >= 70) newZ = Math.round(mc.player.posZ + (0.3 * zRel)) - 0.5 * -zRel;
            }

            if (newX == -2)
                if (mc.player.posX > Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) + 0.5;
                }
                // (mc.player.posX % 1)*1E2 < 30
                else if (mc.player.posX < Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) - 0.5;
                } else {
                    newX = mc.player.posX;
                }

            if (newZ == -2)
                if (mc.player.posZ > Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) + 0.5;
                } else if (mc.player.posZ < Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) - 0.5;
                } else {
                    newZ = mc.player.posZ;
                }

            mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, mc.player.posY, newZ, true));
            mc.player.setPosition(newX, mc.player.posY, newZ);
        }
    }

    public static double[] forward(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0 || entity.moveStrafing != 0;
    }

    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        double[] dir = forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static boolean IsEating() {
        return mc.player != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand().equals(EnumHand.MAIN_HAND);
    }

    public static boolean isEatingOffhand() {
        return mc.player != null && mc.player.getHeldItemOffhand().getItem() instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand().equals(EnumHand.OFF_HAND);
    }

    public static boolean isCurrentViewEntity() {
        return mc.getRenderViewEntity() == mc.player;
    }

    public static boolean CanSeeBlock(BlockPos p_Pos) {
        if (mc.player == null) return false;

        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) == null;
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static BlockPos GetPlayerPosFloored(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static BlockPos entityPosToFloorBlockPos(Entity e) {
        return new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
    }

    public static int GetItemSlot(Item input) {
        if (mc.player == null) return 0;

        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;

            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty()) continue;
            if (s.getItem() == input) return i;
        }
        return -1;
    }

    public static int GetRecursiveItemSlot(Item input) {
        if (mc.player == null) return 0;

        for (int i = mc.player.inventoryContainer.getInventory().size() - 1; i > 0; --i) {
            if (i == 5 || i == 6 || i == 7 || i == 8) continue;

            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

            if (s.isEmpty()) continue;
            if (s.getItem() == input) return i;
        }
        return -1;
    }

    public static void packetFacePitchAndYaw(float p_Pitch, float p_Yaw) {
        boolean l_IsSprinting = mc.player.isSprinting();

        if (l_IsSprinting != mc.player.serverSprintState) {
            if (l_IsSprinting) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            else mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            mc.player.serverSprintState = l_IsSprinting;
        }

        boolean l_IsSneaking = mc.player.isSneaking();

        if (l_IsSneaking != mc.player.serverSneakState) {
            if (l_IsSneaking) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            else mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.serverSneakState = l_IsSneaking;
        }

        if (PlayerUtil.isCurrentViewEntity()) {
            float l_Pitch = p_Pitch;
            float l_Yaw = p_Yaw;

            AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
            double l_PosXDifference = mc.player.posX - mc.player.lastReportedPosX;
            double l_PosYDifference = axisalignedbb.minY - mc.player.lastReportedPosY;
            double l_PosZDifference = mc.player.posZ - mc.player.lastReportedPosZ;
            double l_YawDifference = l_Yaw - mc.player.lastReportedYaw;
            double l_RotationDifference = l_Pitch - mc.player.lastReportedPitch;
            ++mc.player.positionUpdateTicks;
            boolean l_MovedXYZ = l_PosXDifference * l_PosXDifference + l_PosYDifference * l_PosYDifference + l_PosZDifference * l_PosZDifference > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
            boolean l_MovedRotation = l_YawDifference != 0.0D || l_RotationDifference != 0.0D;

            if (mc.player.isRiding()) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, l_Yaw, l_Pitch, mc.player.onGround));
                l_MovedXYZ = false;
            } else if (l_MovedXYZ && l_MovedRotation) {
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, axisalignedbb.minY, mc.player.posZ, l_Yaw, l_Pitch, mc.player.onGround));
            } else if (l_MovedXYZ) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, axisalignedbb.minY, mc.player.posZ, mc.player.onGround));
            } else if (l_MovedRotation) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(l_Yaw, l_Pitch, mc.player.onGround));
            } else if (mc.player.prevOnGround != mc.player.onGround) {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }

            if (l_MovedXYZ) {
                mc.player.lastReportedPosX = mc.player.posX;
                mc.player.lastReportedPosY = axisalignedbb.minY;
                mc.player.lastReportedPosZ = mc.player.posZ;
                mc.player.positionUpdateTicks = 0;
            }

            if (l_MovedRotation) {
                mc.player.lastReportedYaw = l_Yaw;
                mc.player.lastReportedPitch = l_Pitch;
            }

            mc.player.prevOnGround = mc.player.onGround;
            mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
        }
    }

    public static boolean isPlayerTrapped() {
        BlockPos playerPos = GetLocalPlayerPosFloored();

        final BlockPos[] trapPos = {
                playerPos.down(),
                playerPos.up().up(),
                playerPos.north(),
                playerPos.south(),
                playerPos.east(),
                playerPos.west(),
                playerPos.north().up(),
                playerPos.south().up(),
                playerPos.east().up(),
                playerPos.west().up(),
        };

        for (BlockPos pos : trapPos) {
            IBlockState state = mc.world.getBlockState(pos);

            if (state.getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) return false;
        }

        return true;
    }

    public static boolean isEntityTrapped(Entity e) {
        BlockPos playerPos = entityPosToFloorBlockPos(e);

        final BlockPos[] l_TrapPositions = {
                playerPos.up().up(),
                playerPos.north(),
                playerPos.south(),
                playerPos.east(),
                playerPos.west(),
                playerPos.north().up(),
                playerPos.south().up(),
                playerPos.east().up(),
                playerPos.west().up(),
        };

        for (BlockPos l_Pos : l_TrapPositions) {
            IBlockState l_State = mc.world.getBlockState(l_Pos);

            if (l_State.getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(l_Pos).getBlock() != Blocks.BEDROCK) return false;
        }

        return true;
    }

    public enum Hand {
        MAINHAND, OFFHAND, PACKET, NONE
    }
}
