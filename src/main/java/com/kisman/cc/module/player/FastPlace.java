package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RotationUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class FastPlace extends Module {
    public static FastPlace instance;

    private final Setting all = new Setting("All", this, false);

    private final Setting obby = new Setting("Obby", this, false);
    private final Setting enderChest = new Setting("EnderChest", this, false);
    private final Setting crystal = new Setting("Crystal", this, true);
    private final Setting exp = new Setting("Exp", this, true);
    private final Setting minecart = new Setting("Minecart", this, false);
    private final Setting feetExp = new Setting("FeetExp", this, false);
    private final Setting fastCrystal = new Setting("PacketCrystal", this, false);

    private BlockPos mousePos = null;

    public FastPlace() {
        super("FastPlace", "FastPlace", Category.PLAYER);

        instance = this;

        setmgr.rSetting(all);
        setmgr.rSetting(obby);
        setmgr.rSetting(enderChest);
        setmgr.rSetting(crystal);
        setmgr.rSetting(exp);
        setmgr.rSetting(minecart);
        setmgr.rSetting(feetExp);
        setmgr.rSetting(fastCrystal);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        try {
            if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem().equals(Items.EXPERIENCE_BOTTLE) && this.exp.getValBoolean()) mc.rightClickDelayTimer = 0;
            if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem().equals(Blocks.OBSIDIAN) && this.obby.getValBoolean()) mc.rightClickDelayTimer = 0;
            if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem().equals(Blocks.ENDER_CHEST) && this.enderChest.getValBoolean()) mc.rightClickDelayTimer = 0;
            if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem().equals(Items.MINECART) && this.minecart.getValBoolean()) mc.rightClickDelayTimer = 0;
            if (this.all.getValBoolean()) mc.rightClickDelayTimer = 0;
            if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem().equals(Items.END_CRYSTAL) && (this.crystal.getValBoolean() || this.all.getValBoolean())) mc.rightClickDelayTimer = 0;
        } catch(ArrayIndexOutOfBoundsException ignored) {}

        if (this.fastCrystal.getValBoolean() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            final boolean offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
            if (offhand || mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                final RayTraceResult result = mc.objectMouseOver;
                if (result == null) {
                    return;
                }
                switch (result.typeOfHit) {
                    case MISS: {
                        this.mousePos = null;
                        break;
                    }
                    case BLOCK: {
                        this.mousePos = mc.objectMouseOver.getBlockPos();
                        break;
                    }
                    case ENTITY: {
                        final Entity entity;
                        if (this.mousePos == null || (entity = result.entityHit) == null) {
                            break;
                        }
                        if (!this.mousePos.equals(new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ))) {
                            break;
                        }
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener = new Listener<>(event -> {
        if (event.getEra().equals(Event.Era.PRE) && this.feetExp.getValBoolean()) {
            final boolean mainHand = mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE;
            final boolean offHand = (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE);
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && ((mc.player.getActiveHand() == EnumHand.MAIN_HAND && mainHand) || (mc.player.getActiveHand() == EnumHand.OFF_HAND && offHand))) {
                RotationUtils.lookAtVec3d(mc.player.getPositionVector());
            }
        }
    });
}
