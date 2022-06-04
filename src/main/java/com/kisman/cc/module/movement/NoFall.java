package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import me.zero.alpine.listener.*;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

public class NoFall extends Module {
    private Setting mode = new Setting("Mode", this, Mode.Packet);

    private TimerUtils timer = new TimerUtils();

    public NoFall() {
        super("NoFall", Category.MOVEMENT);

        setmgr.rSetting(mode);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener2 = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            switch(mode.getValString()) {
                case "Packet":
                    if (mc.player.fallDistance > 3.0F) {
                        packet.onGround = true;
                        return;
                    }
                    break;
                case "Anti":
                    if (mc.player.fallDistance > 3.0F) {
                        packet.y = mc.player.posY + 0.10000000149011612;
                        return;
                    }
                    break;
                case "AAC":
                    if (mc.player.fallDistance > 3.0F) {
                        mc.player.onGround = true;
                        mc.player.capabilities.isFlying = true;
                        mc.player.capabilities.allowFlying = true;
                        packet.onGround = true;
                        mc.player.velocityChanged = true;
                        mc.player.capabilities.isFlying = false;
                        mc.player.jump();
                    }
                    break;
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener1 = new Listener<>(event -> {
        if(mode.getValString().equalsIgnoreCase(Mode.Bucket.name())) {
            int bucketSlot = InventoryUtil.findItem(Items.WATER_BUCKET, 0, 9);
            int oldSlot = mc.player.inventory.currentItem;

            if(bucketSlot != -1) {
                Vec3d positionVector = mc.player.getPositionVector();
                RayTraceResult rayTraceBlocks = mc.world.rayTraceBlocks(positionVector, new Vec3d(positionVector.x, positionVector.y - 3.0, positionVector.z), true);

                if (mc.player.fallDistance < 5.0f || rayTraceBlocks == null || rayTraceBlocks.typeOfHit != RayTraceResult.Type.BLOCK || mc.world.getBlockState(rayTraceBlocks.getBlockPos()).getBlock() instanceof BlockLiquid || EntityUtil.isInLiquid() || EntityUtil.isInLiquid(true)) return;
                if (event.getEra() == Event.Era.PRE) event.setPitch(90.0f);
                else {
                    RayTraceResult rayTraceBlocks2 = mc.world.rayTraceBlocks(positionVector, new Vec3d(positionVector.x, positionVector.y - 5.0, positionVector.z), true);
                    if (rayTraceBlocks2 != null && rayTraceBlocks2.typeOfHit == RayTraceResult.Type.BLOCK && !(mc.world.getBlockState(rayTraceBlocks2.getBlockPos()).getBlock() instanceof BlockLiquid) && timer.passedMillis(1000)) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                            InventoryUtil.switchToSlot(bucketSlot, true);
                            mc.playerController.processRightClick(mc.player, mc.world, bucketSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                        });

                        InventoryUtil.switchToSlot(oldSlot, true);
                        timer.reset();
                    }
                }
            }
        }
    });

    public enum Mode {Packet, AAC, Anti, Bucket}
}
