package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;

import com.kisman.cc.util.manager.Managers;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.*;

public class Anchor extends Module {
    private final Setting mode = new Setting("Mode", this, Mode.Motion);
    private final Setting movementStop = new Setting("Movement Stop", this, false).setVisible(!mode.checkValString(Mode.Teleport.name()));
    private final Setting timer = new Setting("Timer", this, false);
    private final Setting timerValue = new Setting("Timer Value", this, 5, 0.1f, 20, false).setVisible(timer::getValBoolean);
    private final Setting disableAfterComplete = new Setting("Disable After Complete", this, false);
    private final Setting fastFall = new Setting("Fast Fall", this, false);
    private final Setting fastFallMotion = new Setting("Fast Fall Motion", this, 10, 1, 10, false).setVisible(fastFall::getValBoolean);
    private final Setting useLagTime = new Setting("Use Fast Fall Lag Time", this, false);
    private final Setting lagTime = new Setting("Fast Fall Lag Time", this, 500, 0, 1000, Slider.NumberType.TIME);

    private boolean using = false;
    private double[] oneblockPositions = new double[] { 0.42, 0.75 };
    private int packets;
    private boolean jumped = false;

    public Anchor() {
        super("Anchor", "help with holes", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        setmgr.rSetting(mode);
        setmgr.rSetting(movementStop);
        setmgr.rSetting(timer);
        setmgr.rSetting(timerValue);
        setmgr.rSetting(disableAfterComplete);
        setmgr.rSetting(fastFall);
        setmgr.rSetting(fastFallMotion);
        Kisman.instance.settingsManager.rSetting(new Setting("Pitch", this, 60, 0, 90, false));
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

    private Vec3d center = Vec3d.ZERO;

    private Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    public void onDisable() {
        using = false;
    }

    public void update() {
        if (mc.world == null && mc.player == null) return;
        super.setDisplayInfo("[" + mode.getValString() + "]");
        if (mc.player.posY < 0) return;

        double pitch = Kisman.instance.settingsManager.getSettingByName(this, "Pitch").getValDouble();

        if (mc.player.rotationPitch >= pitch) {
            if (isBlockHole(PlayerUtil.getPlayerPos().down(1)) || isBlockHole(PlayerUtil.getPlayerPos().down(2)) || isBlockHole(PlayerUtil.getPlayerPos().down(3)) || isBlockHole(PlayerUtil.getPlayerPos().down(4))) {
                if(mode.getValString().equals(Mode.Motion.name())) {
                    center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);

                    double xDiff = Math.abs(center.x - mc.player.posX);
                    double zDiff = Math.abs(center.z - mc.player.posZ);

                    if (xDiff <= 0.1 && zDiff <= 0.1) center = Vec3d.ZERO;
                    else {
                        double motionX = center.x - mc.player.posX;
                        double motionZ = center.z - mc.player.posZ;

                        mc.player.motionX = motionX / 2;
                        mc.player.motionZ = motionZ / 2;
                    }
                    if(fastFall.getValBoolean() && !lagTimeCheck()) mc.player.motionY = -fastFallMotion.getValDouble();
                    using = true;
                } else if(mode.getValString().equals(Mode.Teleport.name())) {
                    if (!mc.player.onGround) this.jumped = mc.gameSettings.keyBindJump.isKeyDown();
        
                    if (!this.jumped && mc.player.fallDistance < 0.5 && BlockUtil.isInHole() && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 1.125 && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 0.95 && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
                        if (!mc.player.onGround) ++this.packets;
                        if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
                            final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                            for (double position : oneblockPositions) mc.player.connection.sendPacket(new CPacketPlayer.Position((blockPos.getX() + 0.5f), mc.player.posY - position, (blockPos.getZ() + 0.5f), true));
                            mc.player.setPosition((blockPos.getX() + 0.5f), BlockUtil.getNearestBlockBelow() + 0.1, (blockPos.getZ() + 0.5f));
                            this.packets = 0;
                        }
                    }

                    if(fastFall.getValBoolean() && !lagTimeCheck()) mc.player.motionY = -fastFallMotion.getValDouble();
                }
            } else using = false;
        }

        if(isBlockHole(PlayerUtil.getPlayerPos())) using = false;

        if(using && timer.getValBoolean()) EntityUtil.setTimer(timerValue.getValFloat());
        else EntityUtil.resetTimer();

        if(isBlockHole(PlayerUtil.getPlayerPos())) {
            if(disableAfterComplete.getValBoolean()) super.setToggled(false);
            if(using) using = false;
        }
    }

    private boolean lagTimeCheck() {
        return useLagTime.getValBoolean() && Managers.instance.passed(lagTime.getValInt());
    }

    public enum Mode {MovementStop, Motion, Teleport}
}
