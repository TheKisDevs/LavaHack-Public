package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MathUtil;

import me.zero.alpine.listener.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCamBypass extends Module {
    private final Setting speed = new Setting("Speed", this, 1, 0, 10, false);
    private final Setting cancelPackets = new Setting("Cancel Packets", this, true);
    private final Setting mode = new Setting("Mode", this, Mode.Normal);

    private Entity riding;
    private EntityOtherPlayerMP camera;
    private Vec3d position;
    private float yaw, pitch;

    public FreeCamBypass() {
        super("FreeCamBypass", Category.PLAYER);

        setmgr.rSetting(speed);
        setmgr.rSetting(cancelPackets);
        setmgr.rSetting(mode);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener2);
        Kisman.EVENT_BUS.subscribe(listener3);
        Kisman.EVENT_BUS.subscribe(listener4);

        if(mc.player == null || mc.world == null) return;

        if(mode.getValString().equals(Mode.Normal.name())) {
            riding = null;
            if(mc.player.getRidingEntity() != null) {
                riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
            }

            camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            camera.copyLocationAndAnglesFrom(mc.player);
            camera.prevRotationYaw = mc.player.rotationYaw;
            camera.rotationYawHead = mc.player.rotationYawHead;
            camera.inventory.copyInventory(mc.player.inventory);
            mc.world.addEntityToWorld(-69, camera);

            position = mc.player.getPositionVector();
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
           
            mc.player.noClip = true;
        } else {
            camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            camera.copyLocationAndAnglesFrom(mc.player);
            camera.prevRotationYaw = mc.player.rotationYaw;
            camera.rotationYawHead = mc.player.rotationYawHead;
            camera.inventory.copyInventory(mc.player.inventory);
            camera.noClip = true;
            mc.world.addEntityToWorld(-69, camera);
            mc.setRenderViewEntity(camera);
        }
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener2);
        Kisman.EVENT_BUS.unsubscribe(listener3);
        Kisman.EVENT_BUS.unsubscribe(listener4);

        if(mc.player == null || mc.world == null) return;

        if(mode.getValString().equals(Mode.Normal.name())) {
            if(riding != null) {
                mc.player.startRiding(riding, true);
                riding = null;
            }
            if(camera != null) mc.world.removeEntity(camera);
            if(position != null) mc.player.setPosition(position.x, position.y, position.z);
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
            mc.player.noClip = false;
            mc.player.setVelocity(0, 0, 0);
        } else {
            if(camera != null) mc.world.removeEntity(camera);
            mc.setRenderViewEntity(mc.player);
        }
    }

    @EventHandler
    private final Listener<EventPlayerMove> listener3 = new Listener<>(event -> {
        if(mode.getValString().equals(Mode.Normal.name())) mc.player.noClip = true;
    });

    @EventHandler
    private final Listener<EventSetOpaqueCube> listener4 = new Listener<>(event -> event.cancel());

    @EventHandler
    private final Listener<EventPlayerUpdate> listener2 = new Listener<>(event -> {
        if(mode.getValString().equals(Mode.Normal.name())) {
            mc.player.noClip = true;
            mc.player.setVelocity(0, 0, 0);

            double[] dir = MathUtil.directionSpeed(speed.getValFloat());

            if(mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX = dir[0];
                mc.player.motionZ = dir[1];
            } else mc.player.motionX = mc.player.motionZ = 0;
            
            mc.player.setSprinting(false);

            if(mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += speed.getValFloat();
            if(mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= speed.getValFloat();
        }
    });

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if(event.getEntity() == mc.player) super.setToggled(false);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(cancelPackets.getValBoolean() && mode.getValString().equals(Mode.Normal.name())) {
            if((event.getPacket() instanceof CPacketUseEntity) 
            || (event.getPacket() instanceof CPacketPlayerTryUseItem)
            || (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
            || (event.getPacket() instanceof CPacketVehicleMove)
            || (event.getPacket() instanceof CPacketChatMessage)
            ) event.cancel();
        }
    });

    public enum Mode {Normal, Camera}
}
