package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;

import java.util.*;

public class HoleSnap extends Module {
    private Setting speedValue = new Setting("Speed", this, 0, 0, 2, false);
    private Setting range = new Setting("Range", this, 4, 0, 10, true);
    private Setting disableIfNoHole = new Setting("Disable If No Hole", this, true);

    private BlockPos hole;
    private double yawRad,speed, lastDist;
    private BlockPos distPos;

    public HoleSnap() {
        super("HoleSnap", Category.MOVEMENT);

        setmgr.rSetting(speedValue);
        setmgr.rSetting(range);
        setmgr.rSetting(disableIfNoHole);
    }

    public void onEnable() {
        hole = null;
        hole = findHoles();
        if(hole == null && disableIfNoHole.getValBoolean()) super.setToggled(false);
    }

    public void onDisable() {
        hole = null;
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        super.setDisplayInfo("[" + speedValue.getValInt() + "]");
        hole = findHoles();
        if(hole == null) return;

        if (mc.gameSettings.keyBindSneak.isKeyDown() || HoleUtil.isInHole(mc.player, true, false)) {
            PlayerUtil.centerPlayer(mc.player.getPositionVector());
            if(disableIfNoHole.getValBoolean()) {
                super.setToggled(false);
                return;
            }
        }

        yawRad = RotationUtils.getRotationTo(mc.player.getPositionVector().add(new Vec3d(-0.5, 0, -0.5)), new Vec3d(hole)).x * Math.PI / 180;
        double dist = mc.player.getPositionVector().distanceTo(new Vec3d(hole.getX(), hole.getY(), hole.getZ()));

        if (mc.player.onGround) speed = Math.min(MovementUtil.getBaseMoveSpeed(), Math.abs(dist) / 2); // divide by 2 because motion
        else speed = Math.min((Math.abs(mc.player.motionX) + Math.abs(mc.player.motionZ)), Math.abs(dist) / 2);

        speed *= speedValue.getValDouble();
        mc.player.motionX = -Math.sin(yawRad) * speed;
        mc.player.motionZ = Math.cos(yawRad) * speed;
    }

    private BlockPos findHoles() {
        NonNullList<BlockPos> holes = NonNullList.create();

        //from old HoleFill module, really good way to do this
        List<BlockPos> blockPosList = CrystalUtils.getSphere(mc.player, range.getValFloat(), false, true);

        blockPosList.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, true, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null) return;
                if (holeType == HoleUtil.HoleType.SINGLE && mc.world.isAirBlock(pos) && mc.world.isAirBlock(pos.add(0, 1, 0)) && mc.world.isAirBlock(pos.add(0, 2, 0)) && pos.getY() <= mc.player.posY) {
                    holes.add(pos);
                }
            }
        });

        distPos = new BlockPos(Double.POSITIVE_INFINITY, 69, 429);
        lastDist = (int) Double.POSITIVE_INFINITY;

        for (BlockPos blockPos : holes) {
            if (mc.player.getDistanceSq(blockPos) < lastDist) {
                distPos = blockPos;
                lastDist = mc.player.getDistanceSq(blockPos);
            }
        }

        if (!distPos.equals(new BlockPos(Double.POSITIVE_INFINITY, 69, 429))) return distPos;
        else return null;
    }
}
