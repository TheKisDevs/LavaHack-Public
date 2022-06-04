package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class AntiTrap extends Module {
    public static AntiTrap instance;

    private final Setting mode = new Setting("Mode", this, "MotionTick", new ArrayList<>(Arrays.asList("MotionTick", "ClientTick")));
    private final Setting delay = new Setting("Delay", this, 400, 0, 1000, true);
    private final Setting switchMode = new Setting("SwitchMode", this, SwitchModes.None);
    private final Setting rotate = new Setting("Rotate", this, Rotate.NONE);
    private final Setting sortY = new Setting("SortY", this, true);
    private final Setting onlyInHole = new Setting("OnlyInHole", this, true);

    private final TimerUtils timer = new TimerUtils();

    public Set<BlockPos> placedPos = new HashSet<>();
    private final Vec3d[] surroundTargets = new Vec3d[] { new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 1.0, 1.0) };

    private boolean offhand = false;

    public AntiTrap() {
        super("AntiTrap", "", Category.COMBAT);

        instance = this;

        setmgr.rSetting(mode);
        setmgr.rSetting(delay);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(rotate);
        setmgr.rSetting(sortY);
        setmgr.rSetting(onlyInHole);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);

        if(mc.player == null && mc.world == null) super.setToggled(false);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        if((mc.player == null && mc.world == null)) return;

        switchItem();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        super.setDisplayInfo("[" + mode.getValString() + "]");
        if(mode.getValString().equalsIgnoreCase("ClientTick")) doAntiTrap();
    }

    @EventHandler private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {if(event.getEra() == Event.Era.PRE && mode.getValString().equalsIgnoreCase("MotionTick")) doAntiTrap();});

    private void doAntiTrap() {
        if(timer.passedMillis(delay.getValInt())) timer.reset();
        else return;

        if(onlyInHole.getValBoolean() && !isBlockHole(mc.player.getPosition())) return;

        this.offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (!this.offhand && InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9) == -1) return;
        final ArrayList<Vec3d> targets = new ArrayList<>();
        Collections.addAll(targets, BlockUtil.convertVec3ds(mc.player.getPositionVector(), this.surroundTargets));
        final EntityPlayer closestPlayer = EntityUtil.getTarget(5);
        if (closestPlayer != null) {
            targets.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
            if (sortY.getValBoolean()) targets.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        }
        for (final Vec3d vec3d3 : targets) {
            final BlockPos pos = new BlockPos(vec3d3);
            if (!CrystalUtils.canPlaceCrystal(pos)) continue;

            placeCrystal(pos);
            super.onDisable();
            break;
        }
    }

    private void placeCrystal(final BlockPos pos) {
        final boolean mainhand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        if (!mainhand && !this.offhand && !this.switchItem()) return;

        final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final float[] angle = AngleUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5f), (pos.getY() - 0.5f), (pos.getZ() + 0.5f)));
        switch (this.rotate.getValString()) {
            case "NORMAL": {
                RotationUtils.setPlayerRotations(angle[0], angle[1]);
                break;
            }
            case "PACKET": {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], (float) MathHelper.normalizeAngle((int)angle[1], 360), AntiTrap.mc.player.onGround));
                break;
            }
        }
        placedPos.add(pos);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        this.timer.reset();
    }

    private boolean switchItem() {
        if (offhand) return true;

        InventoryUtil.switchToSlot(InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9), this.switchMode.getValString().equals("Silent"));
        return true;
    }

    private boolean isBlockHole(BlockPos blockpos) {
        int holeblocks = 0;
        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;

        return holeblocks >= 9;
    }

    public enum Rotate {
        NONE,
        NORMAL,
        PACKET
    }

    public enum SwitchModes {
        None,
        Normal,
        Silent
    }
}
