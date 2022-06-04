package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerTravel;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketEntityAction;

public class ElytraFly extends Module {
    private Setting mode = new Setting("Mode", this, Mode.Control);

    private Setting speedLine = new Setting("SpeedLine", this, "Speed");
    private Setting speed = new Setting("Speed", this, 1.82, 0, 10, false);
    private Setting upSpeed = new Setting("UpSpeed", this, 2, 0, 10, false);
    private Setting downSpeed = new Setting("DownSpeed", this, 1.82, 0, 10, false);
    private Setting glideSpeed = new Setting("GlideSpeed", this, 1, 0, 10, false);

    private Setting cancelLine = new Setting("CancelLine", this, "Cancel");
    private Setting cancelInWater = new Setting("CancelInWater", this, true);
    private Setting cancelAtHeight = new Setting("CancelAtHeight", this, 5, 0, 10, true);

    private Setting otherLine = new Setting("OtherLine", this, "Other");
    private Setting instantFly = new Setting("InstantFly", this, false);
    private Setting equipElytra = new Setting("EquipElytra", this, true);
    private Setting pitchSpoof = new Setting("PitchSpoof", this, false);

    private final TimerUtils instantFlyTimer = new TimerUtils();
    public static ElytraFly instance;
    private int elytraSlot = -1;

    public ElytraFly() {
        super("ElytraFly", "ElytraFly", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "|" + speed.getValInt() + "]");

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(speedLine);
        setmgr.rSetting(speed);
        setmgr.rSetting(upSpeed);
        setmgr.rSetting(downSpeed);
        setmgr.rSetting(glideSpeed);

        setmgr.rSetting(cancelLine);
        setmgr.rSetting(cancelInWater);
        setmgr.rSetting(cancelAtHeight);

        setmgr.rSetting(otherLine);
        setmgr.rSetting(instantFly);
        setmgr.rSetting(equipElytra);
        setmgr.rSetting(pitchSpoof);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);

        elytraSlot = -1;

        if(mc.player == null && mc.world == null) return;

        if(equipElytra.getValBoolean()) {
            if(mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) elytraSlot = InventoryUtil.findItem(Items.ELYTRA, 0, 36);
            if(elytraSlot != -1) {
                boolean armorOnChest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
                if(armorOnChest) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        if(mc.player != null && elytraSlot != -1 && equipElytra.getValBoolean()) {
            boolean hasItem = !mc.player.inventory.getStackInSlot(elytraSlot).isEmpty() || mc.player.inventory.getStackInSlot(elytraSlot).getItem() != Items.AIR;
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, elytraSlot, 0, ClickType.PICKUP, mc.player);
            if (hasItem) mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
        }
    }

    @EventHandler
    private final Listener<EventPlayerTravel> listener = new Listener<>(event -> {
        if (mc.player == null) return;
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) return;

        if (!mc.player.isElytraFlying()) {
            if (!mc.player.onGround && instantFly.getValBoolean()) {
                if (!instantFlyTimer.passedMillis(1000)) return;

                instantFlyTimer.reset();

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }

            return;
        }

        switch (mode.getValString()) {
            case "Normal":
                handleImmediateModeElytra(event);
                break;
            case "Control":
                handleControlMode(event);
                break;
            case "NormalPacket":
                handleNormalPacketModeElytra(event);
                break;
        }
    });

    private void handleNormalPacketModeElytra(EventPlayerTravel travel) {
        double[] dir = MathUtil.directionSpeedNoForward(speed.getValDouble());

        if (mc.player.movementInput.jump) mc.player.motionY = speed.getValDouble();
        if (mc.player.movementInput.sneak) mc.player.motionY = -speed.getValDouble();
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }

    private void handleImmediateModeElytra(EventPlayerTravel travel) {
        if (mc.player.movementInput.jump) {
            double motionSq = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);

            if (motionSq > 1.0) return;
            else {
                double[] dir = MathUtil.directionSpeedNoForward(speed.getValDouble());

                mc.player.motionX = dir[0];
                mc.player.motionY = -(glideSpeed.getValDouble() / 10000f);
                mc.player.motionZ = dir[1];
            }

            travel.cancel();
            return;
        }

        mc.player.setVelocity(0, 0, 0);

        travel.cancel();

        double[] dir = MathUtil.directionSpeed(speed.getValDouble());

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionY = -(glideSpeed.getValDouble() / 10000f);
            mc.player.motionZ = dir[1];
        }

        if (mc.player.movementInput.sneak)
            mc.player.motionY = -downSpeed.getValDouble();

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }

    private void handleControlMode(EventPlayerTravel event) {
        final double[] dir = MathUtil.directionSpeed(speed.getValDouble());

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];

            mc.player.motionX -= (mc.player.motionX * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionX;
            mc.player.motionZ -= (mc.player.motionZ * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionZ;
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        mc.player.motionY = (-MathUtil.degToRad(mc.player.rotationPitch)) * mc.player.movementInput.moveForward;

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;

        event.cancel();
    }

    private enum Mode {
        Normal, Control, NormalPacket
    }
}