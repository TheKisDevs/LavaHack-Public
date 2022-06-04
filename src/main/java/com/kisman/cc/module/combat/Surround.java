package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.mixin.mixins.accessor.ICPacketPlayer;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.Rotation;
import com.kisman.cc.util.Rotation.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.kisman.cc.module.combat.Surround.Center.*;

public class Surround extends Module {
    private Setting surroundVec = new Setting("SurroundVec", this, SurroundVectors.BASE);
    private Setting completion = new Setting("Completion", this, Completion.AIR);
    private Setting center = new Setting("Center", this, TELEPORT);
    private Setting switch_ = new Setting("Switch", this, SwitchModes.Silent);
    private Setting hand = new Setting("Hand", this, PlayerUtil.Hand.MAINHAND);
    private Setting blocksPerTick = new Setting("BlocksPerTick", this, 4, 0, 10, true);
    private Setting raytrace = new Setting("RayTrace", this, false);
    private Setting packet = new Setting("Packet", this, false);
    private Setting confirm = new Setting("Confirm", this, false);
    private final Setting noInteract = new Setting("No Interact", this, false);
    private Setting rewrite = new Setting("Rewrite", this, false);
    private Setting dynamic = new Setting("Rewrite Dynamic", this, false);
    private Setting support = new Setting("Rewrite Support", this, SupportModes.None);
    private Setting retries = new Setting("Rewrite Retries", this, 5, 0, 20, true);
    private Setting protectRetries = new Setting("Rewrite Protect Retries", this, 5, 0, 20, true);
    private Setting rewriteRotate = new Setting("Rewrite Rotate", this, RotateModes.Silent);
    private Setting crystalBreaker = new Setting("Rewrite Crystal Breaker", this, true);
    private Setting breakDelay = new Setting("Rewrite Break Delay", this, 10, 0, 100, Slider.NumberType.TIME);
    private Setting breakRange = new Setting("Rewrite Break Range", this, 5, 1, 6, false);
    private Setting rewriteProtected = new Setting("Rewrite Protect Vec", this, false);

    private Setting rotate = new Setting("Rotate", this, Rotation.Rotate.NONE);
    private Setting rotateCenter = new Setting("RotateCenter", this, false);
    private Setting rotateRandom = new Setting("RotateRandom", this, false);

    public static Surround instance;

    private int oldSlot = -1;
    private int placement;
    private int tries, tries2;
    private int surroundPlaced = 0;
    private BlockPos oldPos = BlockPos.ORIGIN;
    private BlockPos surroundPosition = BlockPos.ORIGIN;
    private Rotation surroundRotation = new Rotation(Float.NaN, Float.NaN, (Rotate) rotate.getValEnum());
    private TimerUtils breakTimer = new TimerUtils();
    private ArrayList<BlockPos> protectOffsets = new ArrayList<>();

    public Surround() {
        super("Surround", "Surround", Category.COMBAT);

        instance = this;

        setmgr.rSetting(surroundVec);
        setmgr.rSetting(completion);
        setmgr.rSetting(center);
        setmgr.rSetting(switch_);
        setmgr.rSetting(hand);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(packet);
        setmgr.rSetting(confirm);
        setmgr.rSetting(noInteract);
        setmgr.rSetting(rewrite);
        setmgr.rSetting(dynamic);
        setmgr.rSetting(support);
        setmgr.rSetting(retries);
        setmgr.rSetting(protectRetries);
        setmgr.rSetting(rewriteRotate);
        setmgr.rSetting(crystalBreaker);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(rewriteProtected);

        setmgr.rSetting(rotate);
        setmgr.rSetting(rotateCenter);
        setmgr.rSetting(rotateRandom);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);

        oldPos = new BlockPos(new Vec3d(MathUtil.roundFloat(mc.player.getPositionVector().x, 0), MathUtil.roundFloat(mc.player.getPositionVector().y, 0), MathUtil.roundFloat(mc.player.getPositionVector().z, 0)));

        switch (center.getValString()) {
            case "TELEPORT": {
                double xPosition = mc.player.getPositionVector().x;
                double zPosition = mc.player.getPositionVector().z;

                if (Math.abs((oldPos.getX() + 0.5) - mc.player.getPositionVector().x) >= 0.2) {
                    int xDirection = (oldPos.getX() + 0.5) - mc.player.getPositionVector().x > 0 ? 1 : -1;
                    xPosition += 0.3 * xDirection;
                }

                if (Math.abs((oldPos.getZ() + 0.5) - mc.player.getPositionVector().z) >= 0.2) {
                    int zDirection = (oldPos.getZ() + 0.5) - mc.player.getPositionVector().z > 0 ? 1 : -1;
                    zPosition += 0.3 * zDirection;
                }

                TeleportUtil.teleportPlayer(xPosition, mc.player.posY, zPosition);
                break;
            }
            case "MOTION": {
                mc.player.motionX = ((Math.floor(mc.player.posX) + 0.5) - mc.player.posX) / 2;
                mc.player.motionZ = ((Math.floor(mc.player.posZ) + 0.5) - mc.player.posZ) / 2;
                break;
            }
        }
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        super.setDisplayInfo("[" + (rewrite.getValBoolean() ? (dynamic.getValBoolean() ? "Dynamic" : "Rewrite") : surroundVec.getValString()) + "]");

        surroundPlaced = 0;

        switch (completion.getValString()) {
            case "ToggleAfterComplete": break;
            case "AIR": {
                if (!oldPos.equals(new BlockPos(new Vec3d(MathUtil.roundFloat(mc.player.getPositionVector().x, 0), MathUtil.roundFloat(mc.player.getPositionVector().y, 0), MathUtil.roundFloat(mc.player.getPositionVector().z, 0)))) || mc.player.posY > oldPos.getY()) {
                    super.setToggled(false);
                    return;
                }
                break;
            }
            case "SURROUNDED": {
                if (isInHole(mc.player)) {
                    super.setToggled(false);
                    return;
                }
                break;
            }
        }

        handleSurround();
    }

    private SurroundVectors getEnumByName(String name) {
        switch (name) {
            case "BASE": return SurroundVectors.BASE;
            case "STANDART": return SurroundVectors.STANDARD;
            case "PROTECT": return SurroundVectors.PROTECT;
            case "PROTECTplus": return SurroundVectors.PROTECTplus;
        }

        return SurroundVectors.BASE;
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer && !Float.isNaN(surroundRotation.getYaw()) && !Float.isNaN(surroundRotation.getPitch())) {
            ((ICPacketPlayer) event.getPacket()).setYaw(surroundRotation.getYaw());
            ((ICPacketPlayer) event.getPacket()).setPitch(surroundRotation.getPitch());
        }
    });

    public void handleSurround() {
        oldSlot = mc.player.inventory.currentItem;

        if (!isInHole(mc.player) && !rewrite.getValBoolean()) {
            InventoryUtil.switchToSlot(InventoryUtil.findItem(Item.getItemFromBlock(Blocks.OBSIDIAN), 0, 9), switch_.getValString().equals("Silent"));

            try {placeSurround();} catch (Exception ignored) {}

            if(switch_.getValString().equals("Silent")) InventoryUtil.switchToSlot(oldSlot, true);
        } else if(rewrite.getValBoolean()) placeSurround();
    }

    public void placeSurround() {
        if(!rewrite.getValBoolean()) {
            for (Vec3d surroundVectors : getEnumByName(surroundVec.getValString()).vectors) {
                if (Objects.equals(BlockUtil.getBlockResistance(new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ)))), BlockUtil.BlockResistance.BLANK) && surroundPlaced <= blocksPerTick.getValDouble()) {
                    surroundPosition = new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ)));

                    if (RaytraceUtil.raytraceBlock(surroundPosition, RaytraceUtil.Raytrace.NORMAL) && raytrace.getValBoolean()) return;
                    if (surroundPosition != BlockPos.ORIGIN) {
                        if (!rotate.getValString().equals(Rotate.NONE.name())) {
                            float[] surroundAngles = rotateCenter.getValBoolean() ? AngleUtil.calculateCenter(surroundPosition) : AngleUtil.calculateAngles(surroundPosition);
                            surroundRotation = new Rotation((float) (surroundAngles[0] + (rotateRandom.getValBoolean() ? ThreadLocalRandom.current().nextDouble(-4, 4) : 0)), (float) (surroundAngles[1] + (rotateRandom.getValBoolean() ? ThreadLocalRandom.current().nextDouble(-4, 4) : 0)), (Rotate) rotate.getValEnum());
                            if (!Float.isNaN(surroundRotation.getYaw()) && !Float.isNaN(surroundRotation.getPitch())) surroundRotation.updateModelRotations();
                        }
                    }

                    for (Entity item : mc.world.loadedEntityList) {
                        if (item instanceof EntityItem && ((EntityItem) item).getItem().getItem().equals(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
                            item.setDead();
                            mc.world.removeEntityFromWorld(item.getEntityId());
                        }
                    }

                    boolean sneak = mc.player.isSneaking();
                    if(noInteract.getValBoolean()) mc.player.setSneaking(true);
                    BlockUtil.placeBlock(new BlockPos(surroundVectors.add(new Vec3d(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ))), packet.getValBoolean(), confirm.getValBoolean());
                    if(noInteract.getValBoolean()) mc.player.setSneaking(sneak);
                    PlayerUtil.swingArm((PlayerUtil.Hand) hand.getValEnum());
                    surroundPlaced++;
                }
            }
        } else {
            if(!getUnsafeBlocks().isEmpty()) {
                int blockSlot;
                if(InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
                else if(InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
                else return;

                InventoryUtil.switchToSlot(blockSlot, switch_.getValString().equals("Silent"));
                float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
                for(BlockPos pos : getUnsafeBlocks()) {
                    if(!rewriteRotate.getValString().equalsIgnoreCase(RotateModes.None.name())) {
                        float[] rots = RotationUtils.getRotationToPos(pos);
                        mc.player.rotationYaw = rots[0];
                        mc.player.rotationPitch = rots[1];
                    }
                    if(!support.getValString().equalsIgnoreCase(SupportModes.None.name())) if(BlockUtil.getPlaceableSide(pos) == null || support.getValString().equalsIgnoreCase(SupportModes.Static.name()) && BlockUtil2.isPositionPlaceable(pos, true, true)) {
                        if(crystalBreaker.getValBoolean()) doCrystalBreaker(pos.down());
                        place(pos.down());
                    }
                    if(!BlockUtil2.isPositionPlaceable(pos, true, true, tries <= retries.getValInt())) continue;
                    if(crystalBreaker.getValBoolean()) doCrystalBreaker(pos);
                    place(pos);
                    tries++;
                }
                block0: {
                    if(protectOffsets.isEmpty() || !rewriteProtected.getValBoolean()) break block0;
                    for(BlockPos pos : protectOffsets) {
                        if(!BlockUtil2.isPositionPlaceable(pos, true, true, tries2 <= protectRetries.getValInt())) continue;
                        if(crystalBreaker.getValBoolean()) doCrystalBreaker(pos);
                        place(pos);
                    }
                }
                if(switch_.getValString().equals("Silent")) InventoryUtil.switchToSlot(oldSlot, true);
                if(rewriteRotate.getValString().equalsIgnoreCase(RotateModes.Silent.name())) {
                    mc.player.rotationYaw = oldRots[0];
                    mc.player.rotationPitch = oldRots[1];
                }
            }
            placement = 0;
            if(!getUnsafeBlocks().isEmpty()) return;
            if(protectOffsets.isEmpty()) tries2 = 0;
            tries = 0;
            if(completion.getValString().equalsIgnoreCase(Completion.ToggleAfterComplete.name())) setToggled(false);
        }
    }

    private void doCrystalBreaker(BlockPos pos) {
        for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if(entity instanceof EntityEnderCrystal) {
                if(!rewriteRotate.getValString().equalsIgnoreCase(RotateModes.None.name())) {
                    float[] rots = RotationUtils.getRotationToPos(pos);
                    mc.player.rotationYaw = rots[0];
                    mc.player.rotationPitch = rots[1];
                }
                mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
        }
    }

    private void place(BlockPos posToPlace) {
        if(placement < blocksPerTick.getValInt()) {
            boolean sneak = mc.player.isSneaking();
            if(noInteract.getValBoolean()) mc.player.setSneaking(true);
            BlockUtil2.placeBlock(posToPlace, EnumHand.MAIN_HAND, packet.getValBoolean());
            if(noInteract.getValBoolean()) mc.player.setSneaking(sneak);
            placement++;
        }
    }

    public enum SurroundVectors {
        BASE(new ArrayList<>(Arrays.asList(new Vec3d(0, -1, 0), new Vec3d(1, -1, 0), new Vec3d(0, -1, 1), new Vec3d(-1, -1, 0), new Vec3d(0, -1, -1), new Vec3d(1, 0, 0), new Vec3d(0, 0, 1), new Vec3d(-1, 0, 0), new Vec3d(0, 0, -1)))),
        STANDARD(new ArrayList<>(Arrays.asList(new Vec3d(0, -1, 0), new Vec3d(1, 0, 0), new Vec3d(-1, 0, 0), new Vec3d(0, 0, 1), new Vec3d(0, 0, -1)))),
        PROTECT(new ArrayList<>(Arrays.asList(
                new Vec3d(0, -1, 0),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(3, 0, 0),
                new Vec3d(-3, 0, 0),
                new Vec3d(0, 0, 3),
                new Vec3d(0, 0, -3)
        ))),
        PROTECTplus(new ArrayList<>(Arrays.asList(
                new Vec3d(0, -1, 0),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(0, 0, -1),
                new Vec3d(2, 0, 0),
                new Vec3d(-2, 0, 0),
                new Vec3d(0, 0, 2),
                new Vec3d(0, 0, -2),
                new Vec3d(3, 0, 0),
                new Vec3d(-3, 0, 0),
                new Vec3d(0, 0, 3),
                new Vec3d(0, 0, -3)
        )));


        private final List<Vec3d> vectors;

        SurroundVectors(List<Vec3d> vectors) {
            this.vectors = vectors;
        }

        public List<Vec3d> getVectors() {
            return this.vectors;
        }
    }

    public enum Center {TELEPORT, MOTION, NONE}
    public enum Completion {AIR, SURROUNDED, PERSISTENT, ToggleAfterComplete}

    public static boolean isInHole(Entity entity) {
        return isObsidianHole(new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ)) || isBedRockHole(new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ));
    }

    public static boolean isObsidianHole(BlockPos blockPos) {
        return !(BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) != BlockUtil.BlockResistance.BLANK || isBedRockHole(blockPos) || BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) != BlockUtil.BlockResistance.UNBREAKABLE || BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) != BlockUtil.BlockResistance.BLANK || BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockUtil.BlockResistance.RESISTANT && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) != BlockUtil.BlockResistance.UNBREAKABLE);
    }

    public static boolean isBedRockHole(BlockPos blockPos) {
        return BlockUtil.getBlockResistance(blockPos.add(0, 1, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 2, 0)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, 0, -1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(-1, 0, 0)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0, 0, 1)) == BlockUtil.BlockResistance.UNBREAKABLE && BlockUtil.getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockUtil.BlockResistance.BLANK && BlockUtil.getBlockResistance(blockPos.add(0, -1, 0)) == BlockUtil.BlockResistance.UNBREAKABLE;
    }

    private List<BlockPos> getUnsafeBlocks() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        for(BlockPos pos :  getOffsets()) {
            if(isSafe(pos)) continue;
            positions.add(pos);
        }
        return positions;
    }

    private boolean isSafe(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos);
    }

    private List<BlockPos> getOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        protectOffsets.clear();
        if (dynamic.getValBoolean()) {
            int z;
            int x;
            double decimalX = Math.abs(mc.player.posX) - Math.floor(Math.abs(mc.player.posX));
            double decimalZ = Math.abs(mc.player.posZ) - Math.floor(Math.abs(mc.player.posZ));
            int lengthX = calculateLength(decimalX, false);
            int negativeLengthX = calculateLength(decimalX, true);
            int lengthZ = calculateLength(decimalZ, false);
            int negativeLengthZ = calculateLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<>();
            offsets.addAll(getOverlapPositions());
            for (x = 1; x < lengthX + 1; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), x, 1 + lengthZ), x, 2 + lengthZ));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), x, 1 + lengthZ), x, 3 + lengthZ));                
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)), x, -(2 + negativeLengthZ)));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)), x, -(3 + negativeLengthZ)));

            }
            for (x = 0; x <= negativeLengthX; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -x, 1 + lengthZ), -x, 2 + lengthZ));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -x, 1 + lengthZ), -x, 3 + lengthZ));                
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)), -x, -(2 + negativeLengthZ)));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)), -x, -(3 + negativeLengthZ)));
            }
            for (z = 1; z < lengthZ + 1; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), 1 + lengthX, z), 2 + lengthX, z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), 1 + lengthX, z), 3 + lengthX, z));                
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z), -(2 + negativeLengthX), z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z), -(3 + negativeLengthX), z));
            }
            for (z = 0; z <= negativeLengthZ; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), 1 + lengthX, z), 2 + lengthX, -z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), 1 + lengthX, z), 3 + lengthX, z));                
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z), -(2 + negativeLengthX), -z));
                protectOffsets.add(addToPosition(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z), -(3 + negativeLengthX), -z));
            }
            offsets.addAll(tempOffsets);
        } else for (EnumFacing side : EnumFacing.HORIZONTALS) offsets.add(getPlayerPosition().add(side.getFrontOffsetX(), 0, side.getFrontOffsetZ()));
        return offsets;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(mc.player.posX, mc.player.posY - Math.floor(mc.player.posY) > Double.longBitsToDouble(Double.doubleToLongBits(19.39343307331816) ^ 0x7FDAFD219E3E896DL) ? Math.floor(mc.player.posY) + Double.longBitsToDouble(Double.doubleToLongBits(4.907271931218261) ^ 0x7FE3A10BE4A4A510L) : Math.floor(mc.player.posY), mc.player.posZ);
    }

    private List<BlockPos> getOverlapPositions() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        int offsetX = calculateOffset(mc.player.posX - Math.floor(mc.player.posX));
        int offsetZ = calculateOffset(mc.player.posZ - Math.floor(mc.player.posZ));
        positions.add(getPlayerPosition());
        for (int x = 0; x <= Math.abs(offsetX); ++x) {
            for (int z = 0; z <= Math.abs(offsetZ); ++z) {
                int properX = x * offsetX;
                int properZ = z * offsetZ;
                positions.add(getPlayerPosition().add(properX, -1, properZ));
            }
        }
        return positions;
    }

    private int calculateOffset(double dec) {
        return dec >= Double.longBitsToDouble(Double.doubleToLongBits(22.19607388697261) ^ 0x7FD05457839243F9L) ? 1 : (dec <= Double.longBitsToDouble(Double.doubleToLongBits(7.035587642812949) ^ 0x7FCF1742257B24DBL) ? -1 : 0);
    }

    private int calculateLength(double decimal, boolean negative) {
        if (negative) return decimal <= Double.longBitsToDouble(Double.doubleToLongBits(30.561776836994962) ^ 0x7FEDBCE3A865B81CL) ? 1 : 0;
        return decimal >= Double.longBitsToDouble(Double.doubleToLongBits(22.350511399288944) ^ 0x7FD03FDD7B12B45DL) ? 1 : 0;
    }

    private BlockPos addToPosition(BlockPos pos, double x, double z) {
        block1: {
            if (pos.getX() < 0) x = -x;
            if (pos.getZ() >= 0) break block1;
            z = -z;
        }
        return pos.add(x, Double.longBitsToDouble(Double.doubleToLongBits(1.4868164896774578E308) ^ 0x7FEA7759ABE7F7C1L), z);
    }

    public enum SupportModes {None, Dynamic, Static}
    public enum SwitchModes {Normal, Silent}
    public enum RotateModes {None, Normal, Silent}
}
