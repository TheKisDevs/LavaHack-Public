package com.kisman.cc.module.combat.autocrystal;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

/**
 * @author Halq
 * @apiNote bababoi
 * @since 28/02/22 20:32PM
 */

public class AutoCrystal extends Module implements Runnable {
    public static AutoCrystal instance = new AutoCrystal();

    public final Setting logic = new Setting("CaLogic", this, CaLogic.BreakPlace);
    public final Setting placeRange = new Setting("PlaceRange", this, 4, 1, 6, true);
    public final Setting placeWallRange = new Setting("Place Wall Range", this, 4.5f, 0, 6, false);
    public final Setting hand = new Setting("BreakHand", this, Hand.OffHand);
    public final Setting breakRange = new Setting("BreakRange", this, 4, 1, 6, true);
    public final Setting targetRange = new Setting("Target Range", this, 15, 1, 20, true);
    public final Setting packetBreak = new Setting("PacketBreak", this, false);
    public final Setting packetPlace = new Setting("PacketPlace", this, true);
    public final Setting minDMG = new Setting("MinDmg", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("MaxSelfDMG", this, 18, 0, 80, true);
    public final Setting antiSuicide = new Setting("AntiSuicide", this, false);
    public final Setting multiPlace = new Setting("MultiPlace", this, false);
    public final Setting raytrace = new Setting("Raytrace", this, true);
    public final Setting check = new Setting("CheckPlacements", this, true);
    public final Setting placeDelay = new Setting("PlaceDelay", this, 4, 0, 80, true);
    public final Setting breakDelay = new Setting("Break Delay", this, 4, 0, 80, true);
    public final Setting lethalMult = new Setting("Lethal Mult", this, 0, 0, 6, false);
    public final Setting switchMode = new Setting("Switch Mode", this, SwitchMode.None);
    public final Setting render = new Setting("CoolRender", this, true);
    private final Setting colorVal = new Setting("Color", this, "Color", new Colour(0, 0, 255));
    private final Setting sound = new Setting("Sound", this, false);
    private final Setting clientSide = new Setting("Client Side", this, false);
    private final Setting rotate = new Setting("Rotate", this, true);
    private final Setting rotateMode = new Setting("RotateMode", this, Rotate.Packet);
    private final Setting MultiThread = new Setting("MultiThread", this, true);
    private final Setting MultiThreadValue = new Setting("MultiThreadValue", this, 2, 1, 4, true);
    private final Setting MultiThreadDelay = new Setting("MultiThreadDelay", this, 0, 0, 60, true);


    static AI.HalqPos bestCrystalPos = new AI.HalqPos(BlockPos.ORIGIN, 0);

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSoundEffect && sound.getValBoolean()) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) if (e instanceof EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) e.setDead();
        }
    });
    private final Timer threadDelay = new Timer();

    private final Timer placeTimer = new Timer(), breakTimer = new Timer();
    public EntityPlayer target;
    Thread thread;
    private AutoCrystal autoCrystal;

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        placeTimer.reset();
        bestCrystalPos = new AI.HalqPos(BlockPos.ORIGIN, 0);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        target = null;
        super.setDisplayInfo("");
    }

    public AutoCrystal() {
        super("AutoHalq", Category.COMBAT);

        instance = this;

        setmgr.rSetting(logic);
        setmgr.rSetting(placeRange);
        setmgr.rSetting(placeWallRange);
        setmgr.rSetting(hand);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(packetBreak);
        setmgr.rSetting(packetPlace);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(antiSuicide);
        setmgr.rSetting(multiPlace);
        setmgr.rSetting(check);
        setmgr.rSetting(placeDelay);
        setmgr.rSetting(breakDelay);
        setmgr.rSetting(lethalMult);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(render);
        setmgr.rSetting(colorVal);
        setmgr.rSetting(sound);
        setmgr.rSetting(clientSide);
        setmgr.rSetting(rotate);
        setmgr.rSetting(rotateMode);
        setmgr.rSetting(MultiThread);
        setmgr.rSetting(MultiThreadValue);
        setmgr.rSetting(MultiThreadDelay);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (render.getValBoolean() && !bestCrystalPos.getBlockPos().equals(BlockPos.ORIGIN))
            RenderUtil.drawBlockESP(bestCrystalPos.getBlockPos(), colorVal.getColour().r1, colorVal.getColour().g1, colorVal.getColour().b1);
    }

    public AI.HalqPos placeCalculateAI() {
        AI.HalqPos posToReturn = new AI.HalqPos(BlockPos.ORIGIN, 0.5f);
        for (BlockPos pos : AIUtils.getSphere(placeRange.getValFloat())) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, target, true);
            float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, true);
            if (CrystalUtils.canPlaceCrystal(pos, check.getValBoolean(), true, multiPlace.getValBoolean(), false)) {
                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f) > MathUtil.square(placeRange.getValFloat())) continue;
                if (selfDamage > maxSelfDMG.getValFloat()) continue;
                if (targetDamage < minDMG.getValFloat()) continue;
                if (antiSuicide.getValBoolean()) if (selfDamage < 2F) continue;
                if (targetDamage > posToReturn.getTargetDamage()) posToReturn = new AI.HalqPos(pos, targetDamage);
            }
        }
        return posToReturn;
    }

    public void doBreak() {
        final EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
        if (crystal == null) return;
        breakProcess(crystal);
        mc.playerController.updateController();
    }

    public void oldRotate(float pitch, float yaw) {
        if (rotate.getValBoolean()) {
            if (rotateMode.checkValString("Normal")) {
                mc.player.rotationYaw = yaw;
                mc.player.rotationPitch = pitch;
            } else if (rotateMode.checkValString("Packet")) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, yaw, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationPitch, pitch, mc.player.onGround));
            }
        }
    }

    public void newRotate(float pitch, float yaw) {
        if (rotate.getValBoolean()) {
            if (rotateMode.checkValString("Normal")) {
                mc.player.rotationYaw = yaw;
                mc.player.rotationPitch = pitch;
            } else if (rotateMode.checkValString("Packet")) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, yaw, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationPitch, pitch, mc.player.onGround));
            }
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        bestCrystalPos = placeCalculateAI();
        float[] rot = RotationUtils.getRotationToPos(bestCrystalPos.getBlockPos());
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && rotateMode.checkValString("Spoof")) {
            ((CPacketPlayer) packet).yaw = rot[0];
            ((CPacketPlayer) packet).pitch = rot[1];
        }
    });

    private void breakProcess(Entity entity) {
        if (breakTimer.passedDms(breakDelay.getValDouble())) {
            if (mc.player.getDistance(entity) < breakRange.getValDouble()) {
                if (breakTimer.passedMs(breakDelay.getValLong())) {
                    float[] rot = RotationUtils.getRotationToPos(entity.getPosition());
                    newRotate(rot[1], rot[0]);
                    for (int i = 0; i <= 1; i++) {
                        if (packetBreak.getValBoolean()) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                            CPacketUseEntity packet = new CPacketUseEntity();
                            packet.entityId = entity.entityId;
                            packet.action = CPacketUseEntity.Action.ATTACK;
                            mc.player.connection.sendPacket(packet);
                        } else mc.playerController.attackEntity(mc.player, entity);
                    }
                    try {if (clientSide.getValBoolean()) mc.world.removeEntityFromWorld(entity.entityId);} catch (Exception ignored) {}
                    breakTimer.reset();
                }
                breakTimer.reset();
            }
            if (hand.getValString().equals("MainHand")) mc.player.swingArm(EnumHand.MAIN_HAND);
            else if (hand.getValString().equals("OffHand")) mc.player.swingArm(EnumHand.OFF_HAND);
            else mc.player.connection.sendPacket(new CPacketAnimation());
            breakTimer.reset();
        }
        breakTimer.reset();
    }

    public void doPlace() {
        bestCrystalPos = placeCalculateAI();

        float[] old = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        if (bestCrystalPos.getBlockPos() != BlockPos.ORIGIN) {
            if (placeTimer.passedDms(placeDelay.getValLong())) {
                int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9), oldSlot = mc.player.inventory.currentItem;
                boolean canSwitch = true, offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
                if (crystalSlot != mc.player.inventory.currentItem && switchMode.getValString().equalsIgnoreCase("None") && !offhand) return;
                if (crystalSlot == mc.player.inventory.currentItem || offhand) canSwitch = false;
                if (canSwitch) InventoryUtil.switchToSlot(crystalSlot, switchMode.getValString().equalsIgnoreCase("Silent"));
                if (placeTimer.passedMs(placeDelay.getValLong())) {
                    if (packetPlace.getValBoolean()) mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestCrystalPos.getBlockPos(), AI.getEnumFacing(raytrace.getValBoolean(), bestCrystalPos.getBlockPos()), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    else mc.playerController.processRightClickBlock(mc.player, mc.world, bestCrystalPos.getBlockPos(), AI.getEnumFacing(raytrace.getValBoolean(), bestCrystalPos.getBlockPos()), new Vec3d(0, 0, 0), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                    placeTimer.reset();
                }
                placeTimer.reset();
                if (switchMode.getValString().equalsIgnoreCase("Silent")) InventoryUtil.switchToSlot(oldSlot, true);
            }
            placeTimer.reset();
        }
        oldRotate(old[1], old[0]);
    }

    public static AutoCrystal get(AutoCrystal autoCrystal) {
        if (instance == null) {
            instance = new AutoCrystal();
            instance.autoCrystal = autoCrystal;
        }
        return instance;
    }

    public enum SwitchMode {None, Normal, Silent}

    public enum CaLogic {BreakPlace, PlaceBreak}

    public enum Hand {MainHand, OffHand, PacketSwing}

    public enum Rotate {Packet, Normal, Spoof}

    public void update() {
        if (mc.player == null || mc.world == null) return;
        target = EntityUtil.getTarget(targetRange.getValFloat());
        if (target == null) {
            super.setDisplayInfo("");
            return;
        }
        super.setDisplayInfo("[" + target.getName() + "]");
        if (logic.getValString().equals("BreakPlace")) {
            doBreak();
            doPlace();
        } else if (logic.getValString().equals("PlaceBreak")) {
            doPlace();
            doBreak();
        }
        if(MultiThread.getValBoolean()) newThread();
    }

    @Override
    public void run() {
        if(MultiThread.getValBoolean()) newThread();
    }

    public void newThread() {
        for (int i = 0; i <= MultiThreadValue.getValInt(); i++) {
            if (threadDelay.passedMs(MultiThreadDelay.getValLong())) {
                if (thread == null)
                    thread = new Thread(AutoCrystal.get(this));
                if (thread != null && (thread.isInterrupted() || thread.isAlive()))
                    thread = new Thread(AutoCrystal.get(this));
                if (thread != null && thread.getState().equals(Thread.State.NEW)) {
                    try {
                        thread.start();
                        thread.run();
                    } catch (Exception ignored) {}
                }
                threadDelay.reset();
            }
            threadDelay.reset();
        }
    }
}