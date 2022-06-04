package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

public class Scaffold extends Module {
    private Setting placeLogic = new Setting("Place Logic", this, PlaceLogic.Predict);
    private final Setting distance = new Setting("Distance", this, 2, 0, 20, true);
    private final Setting towerMode = new Setting("Tower Mode", this, TowerMode.None);
    private final Setting downSpeed = new Setting("Down Speed", this, 0, 0, 0.2f, false);
    private final Setting jumpDelay = new Setting("Jump Delay", this, 2, 1, 10, Slider.NumberType.TIME);
    private final Setting rotate = new Setting("Rotate", this, false);
    private final Setting sneakTest = new Setting("Sneak Test", this, false);

    private int timer;
    private int oldSlot;
    private int newSlot;
    private double oldTower;
    private EntityPlayer predictPlayer;
    private BlockPos scaffold;
    private BlockPos towerPos;
    private BlockPos downPos;
    private TimerUtils cancelTimer = new TimerUtils();
    private CPacketPlayer.Rotation rotVec = null;

    public Scaffold() {
        super("Scaffold", "Scaffold", Category.MOVEMENT);

        setmgr.rSetting(placeLogic);
        setmgr.rSetting(distance);
        setmgr.rSetting(towerMode);
        setmgr.rSetting(downSpeed);
        setmgr.rSetting(jumpDelay);
        setmgr.rSetting(rotate);
        setmgr.rSetting(sneakTest);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
        timer = 0;
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        super.setDisplayInfo("[" + placeLogic.getValString() + "]");
        if(mc.player.onGround) timer = 0;
        else timer++;
        if (timer == jumpDelay.getValInt() && mc.gameSettings.keyBindJump.isKeyDown() && !cancelTimer.hasReached(1200)) {
            mc.player.jump();
            timer = 0;
        }
    }

    @EventHandler
    private final Listener<EventPlayerJump> listener = new Listener<>(event -> {
        if(towerMode.checkValString(TowerMode.FakeJump.name())) event.cancel();
    });

    @EventHandler private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer.PositionRotation && rotate.getValBoolean() && rotVec != null) {
            CPacketPlayer.PositionRotation e = (CPacketPlayer.PositionRotation) event.getPacket();
            e.pitch = rotVec.pitch;
            e.yaw = rotVec.yaw;
        }
    });

    @EventHandler
    private final Listener<EventPlayerMove> listener2 = new Listener<>(event -> {
        oldSlot = mc.player.inventory.currentItem;
        towerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
        downPos = new BlockPos(mc.player.posX, mc.player.posY - 2, mc.player.posZ);
        if (placeLogic.getValString().equalsIgnoreCase("Predict")) {
            PredictUtil.PredictSettings predictSettings = new PredictUtil.PredictSettings((distance.getValInt()), false, 0, 0, 0, 0, 0, 0, false, 0, false, false, false, false, false, 0, 696969);
            predictPlayer = PredictUtil.predictPlayer(mc.player, predictSettings);
            scaffold = (new BlockPos(predictPlayer.posX, predictPlayer.posY - 1, predictPlayer.posZ));
        } else if (placeLogic.getValString().equalsIgnoreCase("Player")) {
            double[] dir = MovementUtil.forward(MovementUtil.getMotion(mc.player) * distance.getValDouble());
            scaffold = new BlockPos(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1]).down();
        }
        newSlot = -1;
        if (!Block.getBlockFromItem(mc.player.getHeldItemMainhand().item).getDefaultState().isFullBlock()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
                if (!Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock()) continue;
                if (((ItemBlock) stack.getItem()).getBlock() instanceof BlockFalling) if (mc.world.getBlockState(scaffold).getMaterial().isReplaceable()) continue;
                newSlot = i;
                break;
            }
        } else newSlot = mc.player.inventory.currentItem;

        if (newSlot == -1) {
            newSlot = 1;
            ChatUtils.error("[Scaffold] Out of valid blocks. Disabling!");
            super.setToggled(false);
        }

        switch (towerMode.getValString()) {
            case "Jump": {
                if (mc.player.onGround) {
                    oldTower = mc.player.posY;
                    mc.player.jump();
                }
                if (Math.floor(mc.player.posY) == oldTower + 1 && !mc.player.onGround) mc.player.motionY = -(mc.player.posY - Math.floor(mc.player.posY));
                placeBlockPacket(towerPos, false);
                break;
            }
            case "FakeJump": {
                if (mc.player.ticksExisted % jumpDelay.getValFloat() == 0 && mc.player.onGround && mc.gameSettings.keyBindJump.isKeyDown()) {
                    PlayerUtil.fakeJump(3);
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0013359791121, mc.player.posZ);
                    placeBlockPacket(towerPos, false);
                    break;
                }
            }
        }


        if (mc.gameSettings.keyBindJump.isKeyDown()) placeBlockPacket(towerPos, false);
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSprint.isKeyDown()) placeBlockPacket(scaffold, true);
        double[] dir = MovementUtil.forward(downSpeed.getValDouble());
        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
            placeBlockPacket(downPos, false);
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        }
    });

    public boolean placeBlockPacket(BlockPos pos, boolean allowSupport) {
        mc.player.rotationYaw += mc.player.ticksExisted % 2 == 0 ? 0.00001 : -0.00001; // force rotation packet
        boolean shouldPlace = mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) && BlockUtil.getPlaceableSide(pos) != null;
        if (shouldPlace) {
            boolean swap = newSlot != mc.player.inventory.currentItem;
            if (swap) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
                mc.player.inventory.currentItem = newSlot;
            }
            rotVec = BlockUtil2.placeBlockGetRotate(pos, EnumHand.MAIN_HAND, false, null, false, sneakTest.getValBoolean());
            if (swap) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                mc.player.inventory.currentItem = oldSlot;
            }
            return rotVec != null;
        } else if (allowSupport && BlockUtil.getPlaceableSide(pos) == null) clutch();
        return false;
    }

    public void clutch() {
        BlockPos xpPos = new BlockPos(mc.player.posX + 1, mc.player.posY - 1, mc.player.posZ);
        BlockPos xmPos = new BlockPos(mc.player.posX - 1, mc.player.posY - 1, mc.player.posZ);
        BlockPos zpPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ + 1);
        BlockPos zmPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ - 1);
        if (!placeBlockPacket(xpPos, false)) if (!placeBlockPacket(xmPos, false)) if (!placeBlockPacket(zpPos, false)) placeBlockPacket(zmPos, false);
    }

    public enum PlaceLogic {Predict, Player}
    public enum TowerMode {Jump, Motion, FakeJump, None}
    public enum Modes {Tower, Normal}
}
