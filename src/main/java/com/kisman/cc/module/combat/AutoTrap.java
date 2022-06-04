package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class AutoTrap extends Module {
    public static AutoTrap instance;
    protected final Setting targetRange = new Setting("Target Range", this, 10, 1, 20, true);
    protected final Setting disableOnComplete = new Setting("Disable On Complete", this, false);
    protected final Setting placeDelay = new Setting("Delay", this, 50, 0, 100, true);
    protected final Setting rotate = new Setting("Rotate", this, true);
    protected final Setting blocksPerTick = new Setting("Blocks Per Tick", this, 8, 1, 30, true);
    protected final Setting antiScaffold = new Setting("Anti Scaffold", this, false);
    protected final Setting antiStep = new Setting("Anti Step", this, false);
    protected final Setting surroundPlacing = new Setting("Surround Placing", this, true);
    protected final Setting range = new Setting("Range", this, 4, 1, 5, false);
    protected final Setting raytrace = new Setting("RayTrace", this, false);
    protected final Setting packet = new Setting("Packet Place", this, true);
    protected final Setting rewrite = new Setting("Rewrite", this, false);
    protected final Setting dynamic = new Setting("Rewrite Dynamic", this, false);
    protected final Setting supportBlocks = new Setting("Rewrite Support Blocks", this, RewriteSupportModes.Dynamic);
    protected final Setting rewriteRetries = new Setting("Rewrite Retries", this, 0, 0, 20, true);
    protected final Setting switch_ = new Setting("Rewrite Switch Mode", this, RewriteSwitchModes.Silent);
    protected final Setting rotateMode = new Setting("Rewrite Rotate Mode", this, RewriteRotateModes.Silent);

    protected TimerUtils timer = new TimerUtils();
    protected Map<BlockPos, Integer> retries = new HashMap<>();
    protected int tries;
    protected TimerUtils retryTimer = new TimerUtils();
    public EntityPlayer target;
    protected boolean didPlace = false;
    protected boolean isSneaking;
    protected int oldSlot;
    protected int placements = 0;
    protected int rewrPlacements = 0;
    private boolean smartRotate = false;
    protected BlockPos startPos = null;

    public AutoTrap() {
        super("AutoTrap", "trapping all players", Category.COMBAT);
        super.setToggled(false);

        instance = this;

        setmgr.rSetting(targetRange);
        setmgr.rSetting(disableOnComplete);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(rotate);
        setmgr.rSetting(blocksPerTick);
        setmgr.rSetting(antiScaffold);
        setmgr.rSetting(antiStep);
        setmgr.rSetting(surroundPlacing);
        setmgr.rSetting(range);
        setmgr.rSetting(raytrace);
        setmgr.rSetting(packet);
        setmgr.rSetting(rewrite);
        setmgr.rSetting(dynamic);
        setmgr.rSetting(supportBlocks);
        setmgr.rSetting(rewriteRetries);
        setmgr.rSetting(rotateMode);
    }

    /**
     * see <code>com.kisman.cc.module.combat.SelfTrap
     */
    public AutoTrap(String name, Category category) {
        super(name, category);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) return;

        startPos = EntityUtil.getRoundedBlockPos(mc.player);
        oldSlot = mc.player.inventory.currentItem;
        retries.clear();
    }

    public void update() {
        if(mc.player == null ||  mc.world == null) return;

        if(!rewrite.getValBoolean()) {
            smartRotate = false;
            doTrap();
        } else doRewriteTrap();
    }

    protected void doRewriteTrap() {
        target = EntityUtil.getTarget(targetRange.getValFloat());

        if(target == null) return;

        int blockSlot;
        int oldSlot = mc.player.inventory.currentItem;
        if(InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else if(InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9) != -1) blockSlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        else return;

        InventoryUtil.switchToSlot(blockSlot, switch_.getValString().equalsIgnoreCase("Silent"));
        for(BlockPos pos : getPosList()) {
            if(!BlockUtil2.isPositionPlaceable(pos, true, true, tries <= rewriteRetries.getValInt())) continue;
            place(pos);
            tries++;
        }
        rewrPlacements = 0;
        if(switch_.getValString().equalsIgnoreCase(RewriteSwitchModes.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
        if(!getPosList().isEmpty()) return;
        tries = 0;
        if(disableOnComplete.getValBoolean()) setToggled(false);
    }

    protected ArrayList<BlockPos> getPosList() {
        List<BlockPos> startPosList = getUnsafeBlocks();
        ArrayList<BlockPos> finalPosList = new ArrayList<>();

        for(BlockPos pos : startPosList) {
            if(!supportBlocks.getValString().equalsIgnoreCase(Surround.SupportModes.None.name())) if(BlockUtil.getPlaceableSide(pos) == null || supportBlocks.getValString().equalsIgnoreCase(Surround.SupportModes.Static.name()) && BlockUtil2.isPositionPlaceable(pos, true, true)) finalPosList.add(pos.down());
            if(surroundPlacing.getValBoolean()) finalPosList.add(pos);
        }

        for(BlockPos pos : getAroundOffset()) {
            finalPosList.add(pos.up());
            if(antiStep.getValBoolean()) finalPosList.add(pos.up().up());
        }

        for(BlockPos pos : getOverlapPositions()) {
            if(antiStep.getValBoolean()) finalPosList.add(pos.up().up().up());
            if(antiScaffold.getValBoolean()) finalPosList.add(pos.up().up().up().up());
        }

        return  finalPosList;
    }

    protected void place(BlockPos posToPlace) {
        if(rewrPlacements < blocksPerTick.getValInt()) {
            float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};
            if(!rotateMode.getValString().equalsIgnoreCase(RewriteRotateModes.None.name())) {
                float[] rots = RotationUtils.getRotationToPos(posToPlace);
                mc.player.rotationYaw = rots[0];
                mc.player.rotationPitch = rots[1];
            }
            BlockUtil2.placeBlock(posToPlace, EnumHand.MAIN_HAND, packet.getValBoolean());
            rewrPlacements++;
            if(rotateMode.getValString().equalsIgnoreCase(RewriteRotateModes.Silent.name())) {
                mc.player.rotationYaw = oldRots[0];
                mc.player.rotationPitch = oldRots[1];
            }
        }
    }

    private void doTrap() {
        if(check()) return;
        doStaticTrap();
        if(didPlace) timer.reset();
    }

    private void doStaticTrap() {
        final List<Vec3d> placeTargets = BlockUtil.targets(target.getPositionVector(), antiScaffold.getValBoolean(), antiStep.getValBoolean(), surroundPlacing.getValBoolean(), false, false, this.raytrace.getValBoolean());
        placeList(placeTargets);
    }

    private void placeList(final List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValBoolean());
            if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
                this.placeBlock(position);
                this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                this.retryTimer.reset();
            } else {
                if (placeability != 3) continue;
                this.placeBlock(position);
            }
        }
    }

    protected boolean check() {
        if(mc.player == null || startPos == null) return false;

        didPlace = false;
        placements = 0;
        final int obbySlot2 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (obbySlot2 == -1) setToggled(false);
        final int obbySlot3 = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
        if (!super.isToggled()) return true;
        if (!startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            setToggled(false);
            return true;
        }
        if (retryTimer.passedMillis(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot3 == -1) {
            ChatUtils.error(ChatFormatting.RED + "No Obsidian in hotbar, AutoTrap disabling...");
            setToggled(false);
            return true;
        }
        if (mc.player.inventory.currentItem != this.oldSlot && mc.player.inventory.currentItem != obbySlot3) this.oldSlot = mc.player.inventory.currentItem;
        isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        target = EntityUtil.getTarget(targetRange.getValFloat());
        return target == null || !timer.passedMillis(placeDelay.getValInt());
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

    private List<BlockPos> getAroundOffset() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        if (dynamic.getValBoolean()) {
            int z;
            int x;
            double decimalX = Math.abs(target.posX) - Math.floor(Math.abs(target.posX));
            double decimalZ = Math.abs(target.posZ) - Math.floor(Math.abs(target.posZ));
            int lengthX = calculateLength(decimalX, false);
            int negativeLengthX = calculateLength(decimalX, true);
            int lengthZ = calculateLength(decimalZ, false);
            int negativeLengthZ = calculateLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<>();
            for (x = 1; x < lengthX + 1; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
            }
            for (x = 0; x <= negativeLengthX; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
            }
            for (z = 1; z < lengthZ + 1; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
            }
            for (z = 0; z <= negativeLengthZ; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
            }
            offsets.addAll(tempOffsets);
        } else for (EnumFacing side : EnumFacing.HORIZONTALS) offsets.add(getPlayerPosition().add(side.getFrontOffsetX(), 0, side.getFrontOffsetZ()));
        return offsets;
    }

    private List<BlockPos> getOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        if (dynamic.getValBoolean()) {
            int z;
            int x;
            double decimalX = Math.abs(target.posX) - Math.floor(Math.abs(target.posX));
            double decimalZ = Math.abs(target.posZ) - Math.floor(Math.abs(target.posZ));
            int lengthX = calculateLength(decimalX, false);
            int negativeLengthX = calculateLength(decimalX, true);
            int lengthZ = calculateLength(decimalZ, false);
            int negativeLengthZ = calculateLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<>();
            offsets.addAll(getOverlapPositions());
            for (x = 1; x < lengthX + 1; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
            }
            for (x = 0; x <= negativeLengthX; ++x) {
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
                tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
            }
            for (z = 1; z < lengthZ + 1; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
            }
            for (z = 0; z <= negativeLengthZ; ++z) {
                tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
                tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
            }
            offsets.addAll(tempOffsets);
        } else for (EnumFacing side : EnumFacing.HORIZONTALS) offsets.add(getPlayerPosition().add(side.getFrontOffsetX(), 0, side.getFrontOffsetZ()));
        return offsets;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(target.posX, target.posY - Math.floor(target.posY) > Double.longBitsToDouble(Double.doubleToLongBits(19.39343307331816) ^ 0x7FDAFD219E3E896DL) ? Math.floor(target.posY) + Double.longBitsToDouble(Double.doubleToLongBits(4.907271931218261) ^ 0x7FE3A10BE4A4A510L) : Math.floor(target.posY), target.posZ);
    }

    private List<BlockPos> getOverlapPositions() {
        ArrayList<BlockPos> positions = new ArrayList<>();
        int offsetX = calculateOffset(target.posX - Math.floor(target.posX));
        int offsetZ = calculateOffset(target.posZ - Math.floor(target.posZ));
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

    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValInt() && mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
            final int originalSlot = mc.player.inventory.currentItem;
            final int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
            final int eChestSot = InventoryUtil.findBlock(Blocks.ENDER_CHEST, 0, 9);

            if (obbySlot == -1 && eChestSot == -1) this.toggle();
            if (this.smartRotate) {
                mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), true, isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            } else {
                mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, this.rotate.getValBoolean(), rotate.getValBoolean(), isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            }

            this.didPlace = true;
            ++this.placements;
        }
    }

    public enum RewriteSwitchModes {Normal, Silent}
    public enum RewriteSupportModes {None, Dynamic, Static}
    public enum RewriteRotateModes {None, Normal, Silent}
}
