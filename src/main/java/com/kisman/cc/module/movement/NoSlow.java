package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.console.GuiConsole;
import com.kisman.cc.console.rewrite.ConsoleGui;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.ClickGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import me.zero.alpine.listener.*;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    private final Setting mode = new Setting("Mode", this, Mode.None);

    private final Setting invMove = new Setting("InvMove", this, true);
    private final Setting items = new Setting("Items", this, true);
    private final Setting ncpStrict = new Setting("NCPStrict", this, true);
    private final Setting slimeBlocks = new Setting("SlimeBlocks", this, true);

    private final Setting ignoreChat = new Setting("IgnoreChat", this, true).setVisible(invMove::getValBoolean);
    private final Setting ignoreConsole = new Setting("IgnoreConsole", this, true).setVisible(invMove::getValBoolean);
    private final Setting ignoreClickGui = new Setting("IgnoreClickGui", this, false).setVisible(invMove::getValBoolean);

    public static NoSlow instance;

    public NoSlow() {
        super("NoSlow", "NoSlow", Category.MOVEMENT);

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(invMove);
        setmgr.rSetting(items);
        setmgr.rSetting(ncpStrict);
        setmgr.rSetting(slimeBlocks);

        setmgr.rSetting(ignoreChat);
        setmgr.rSetting(ignoreConsole);
        setmgr.rSetting(ignoreClickGui);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.isHandActive() && items.getValBoolean()) if (mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof ItemShield) if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0 && mc.player.getItemInUseMaxCount() >= 8) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));

        if(slimeBlocks.getValBoolean()) {
            if(mc.player.getRidingEntity() != null) Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8f);
            else Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.6f);
        }

        if (mc.player.isHandActive() && !mc.player.isRiding() && mc.player.fallDistance > 0.7 && PlayerUtil.isMoving(mc.player) && mode.getValString().equals("None")) {
            mc.player.motionX *= 0.9;
            mc.player.motionZ *= 0.9;
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding() && mode.getValString().equals("Sunrise")) {
            mc.player.movementInput.moveStrafe *= 0.2F;
            mc.player.movementInput.moveForward *= 0.2F;
            mc.player.sprintToggleTimer = 0;
        }
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            if (mc.player.ticksExisted % 2 == 0) {
                if (mc.player.onGround) {
                    if (!mc.player.isSprinting()) MovementUtil.setMotion(MovementUtil.WALK_SPEED - 0.2);
                    else MovementUtil.setMotion(MovementUtil.WALK_SPEED - 0.21);
                } else {
                    mc.player.motionX *= 0.9f;
                    mc.player.motionZ *= 0.9f;
                }

            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdateMoveState> listener = new Listener<>(event -> {
        if (invMove.getValBoolean() && mc.currentScreen != null) {
            if(mc.currentScreen instanceof GuiChat && ignoreChat.getValBoolean()) return;
            if((mc.currentScreen instanceof GuiConsole || mc.currentScreen instanceof ConsoleGui) && ignoreConsole.getValBoolean()) return;
            if(mc.currentScreen instanceof ClickGui && ignoreClickGui.getValBoolean()) return;

            mc.player.movementInput.moveStrafe = 0.0F;
            mc.player.movementInput.moveForward = 0.0F;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                ++mc.player.movementInput.moveForward;
                mc.player.movementInput.forwardKeyDown = true;
            } else mc.player.movementInput.forwardKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                --mc.player.movementInput.moveForward;
                mc.player.movementInput.backKeyDown = true;
            } else mc.player.movementInput.backKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
                ++mc.player.movementInput.moveStrafe;
                mc.player.movementInput.leftKeyDown = true;
            } else mc.player.movementInput.leftKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
                --mc.player.movementInput.moveStrafe;
                mc.player.movementInput.rightKeyDown = true;
            } else mc.player.movementInput.rightKeyDown = false;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        }
    });

    @EventHandler
    private final Listener<EventPlayerUpdateMoveState> listener1 = new Listener<>(event -> {
        if(items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= 0.2;
            mc.player.movementInput.moveStrafe /= 0.2;
        }
    });

    @EventHandler private final Listener<PacketEvent.PostSend> listener2 = new Listener<>(event -> {if(event.getPacket() instanceof CPacketPlayer) if(ncpStrict.getValBoolean()) if(items.getValBoolean() && mc.player.isHandActive() && !mc.player.isRiding()) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, PlayerUtil.GetLocalPlayerPosFloored(), EnumFacing.DOWN));});

    public enum Mode {None, Sunrise}
}
