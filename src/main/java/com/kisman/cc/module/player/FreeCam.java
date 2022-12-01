package com.kisman.cc.module.player;


import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import part.kotopushka.lavahack.utils.*;



public class FreeCam
        extends Module {
    public Setting speed = new Setting("Flying Speed",this, 1.0f, 0.1f, 5.0f, false);
    public Setting disableOnDamage = new Setting("Disable on damage",this,false);
    public Setting clipOnDisable = new Setting("Clip on disable", this,false);
    public Setting autoTeleportDisable = new Setting("Auto teleport disable",this,false);
    public Setting reallyWorld = new Setting("Really World",this,false);
    private double oldX;
    private double oldY;
    private double oldZ;
    private int oldPosY;

    public FreeCam() {
        super("FreeCam", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043b\u0435\u0442\u0430\u0442\u044c \u0432 \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u043e\u0439 \u043a\u0430\u043c\u0435\u0440\u0435", Category.MISC);
        setmgr.rSetting(speed);
        setmgr.rSetting(reallyWorld);
        setmgr.rSetting(autoTeleportDisable);
        setmgr.rSetting(clipOnDisable);
        setmgr.rSetting(disableOnDamage);
    }

    @Override
    public void onEnable() {
        this.oldX = FreeCam.mc.player.posX;
        this.oldY = FreeCam.mc.player.posY;
        this.oldZ = FreeCam.mc.player.posZ;
        this.oldPosY = (int)FreeCam.mc.player.posY;
        FreeCam.mc.player.noClip = true;
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(FreeCam.mc.world, FreeCam.mc.player.getGameProfile());
        fakePlayer.copyLocationAndAnglesFrom(FreeCam.mc.player);
        fakePlayer.posY -= 0.0;
        fakePlayer.rotationYawHead = FreeCam.mc.player.rotationYawHead;
        FreeCam.mc.world.addEntityToWorld(-69, fakePlayer);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
        if (FreeCam.mc.player == null || FreeCam.mc.world == null || FreeCam.mc.player.ticksExisted < 1) {
            if (this.autoTeleportDisable.getValBoolean()) {
                this.toggle();
                Kisman.EVENT_BUS.unsubscribe(listener1);
                Kisman.EVENT_BUS.unsubscribe(listener2);

            }
            return;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.clipOnDisable.getValBoolean()) {
            this.oldX = FreeCam.mc.player.posX;
            this.oldY = FreeCam.mc.player.posY;
            this.oldZ = FreeCam.mc.player.posZ;
        }
        FreeCam.mc.player.capabilities.isFlying = false;
        FreeCam.mc.world.removeEntityFromWorld(-69);
        FreeCam.mc.player.motionZ = 0.0;
        FreeCam.mc.player.motionX = 0.0;
        FreeCam.mc.player.noClip = true;
        FreeCam.mc.player.setPositionAndRotation(this.oldX, this.oldY, this.oldZ, FreeCam.mc.player.rotationYaw, FreeCam.mc.player.rotationPitch);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
        if (FreeCam.mc.player == null || FreeCam.mc.world == null || FreeCam.mc.player.ticksExisted < 1) {
            if (this.autoTeleportDisable.getValBoolean()) {
                this.toggle();
                Kisman.EVENT_BUS.unsubscribe(listener1);
                Kisman.EVENT_BUS.unsubscribe(listener2);

            }
            return;
        }
        super.onDisable();
    }

    @EventHandler
    private final Listener<EventReceivePacket> listener1 = new Listener<>(event -> {
        if (!this.reallyWorld.getValBoolean()) {
            return;
        }

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            event.setCancelled(true);
        }
    });





    @EventHandler
    private final Listener<EventReceivePacket> listener2 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketEntityAction) {
            event.setCancelled(true);
        }
    });

    @EventTarget
    public void onPreMotion(EventPreMotion event) {
        event.setCancelled(true);
    }

    @EventTarget
    public void onFullCube(EventFullCube event) {
        event.setCancelled(true);
    }

    @EventTarget
    public void onPush(EventPush event) {
        event.setCancelled(true);
    }



    @EventTarget
    public void onUpdate(EventUpdateLiving event) {
        if (FreeCam.mc.player == null || FreeCam.mc.world == null) {
            return;
        }
        if (this.disableOnDamage.getValBoolean()) {
            if (FreeCam.mc.player.hurtTime <= 8) {
                FreeCam.mc.player.noClip = true;
                FreeCam.mc.player.capabilities.isFlying = true;
                if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                    FreeCam.mc.player.motionY = this.speed.getValFloat() / 1.5f;
                }
                if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    FreeCam.mc.player.motionY = -this.speed.getValFloat() / 1.5f;
                }
                MovementHelper.setSpeed(this.speed.getValFloat());
            } else if (!MovementHelper.isUnderBedrock()) {
                FreeCam.mc.player.capabilities.isFlying = false;
                FreeCam.mc.renderGlobal.loadRenderers();
                FreeCam.mc.player.noClip = false;
                FreeCam.mc.player.setPositionAndRotation(this.oldX, this.oldY, this.oldZ, FreeCam.mc.player.rotationYaw, FreeCam.mc.player.rotationPitch);
                FreeCam.mc.world.removeEntityFromWorld(-69);
                FreeCam.mc.player.motionZ = 0.0;
                FreeCam.mc.player.motionX = 0.0;
                this.toggle();
            }
        } else {
            FreeCam.mc.player.noClip = true;
            FreeCam.mc.player.onGround = false;
            if (FreeCam.mc.gameSettings.keyBindJump.isKeyDown()) {
                FreeCam.mc.player.motionY = this.speed.getValFloat() / 1.5f;
            }
            if (FreeCam.mc.gameSettings.keyBindSneak.isKeyDown()) {
                FreeCam.mc.player.motionY = -this.speed.getValFloat() / 1.5f;
            }
            MovementHelper.setSpeed(this.speed.getValFloat());
            FreeCam.mc.player.capabilities.isFlying = true;
        }
        if (this.clipOnDisable.getValBoolean()) {
            this.oldX = FreeCam.mc.player.posX;
            this.oldY = FreeCam.mc.player.posY;
            this.oldZ = FreeCam.mc.player.posZ;
        }
    }
}

