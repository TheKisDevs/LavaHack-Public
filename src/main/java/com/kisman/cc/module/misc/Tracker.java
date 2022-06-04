package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;

public class Tracker extends Module {
    private Setting autoEnable = new Setting("AutoEnable", this, false);
    private Setting autoDisable = new Setting("AutoDisable", this, true);

    private final TimerUtils timer = new TimerUtils();
    private final Set<BlockPos> manuallyPlaced = new HashSet<>();
    private EntityPlayer trackedPlayer;
    private int usedExp = 0;
    private int usedStacks = 0;
    private int usedCrystals = 0;
    private int usedCStacks = 0;
    private boolean shouldEnable = false;

    public Tracker() {
        super("Tracker", "Tracks players in 1v1s. Only good in duels tho!", Category.MISC);

        Kisman.EVENT_BUS.subscribe(listener3);

        setmgr.rSetting(autoEnable);
        setmgr.rSetting(autoDisable);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
        Kisman.EVENT_BUS.subscribe(listener4);

        this.manuallyPlaced.clear();
        this.shouldEnable = false;
        this.trackedPlayer = null;
        this.usedExp = 0;
        this.usedStacks = 0;
        this.usedCrystals = 0;
        this.usedCStacks = 0;
    }

    public void onDisable() {
        this.manuallyPlaced.clear();
        this.shouldEnable = false;
        this.trackedPlayer = null;
        this.usedExp = 0;
        this.usedStacks = 0;
        this.usedCrystals = 0;
        this.usedCStacks = 0;

        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
        Kisman.EVENT_BUS.unsubscribe(listener4);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        trackedPlayer = AutoRer.currentTarget;

        if(trackedPlayer == null) return;

        if (this.usedStacks != this.usedExp / 64) {
            this.usedStacks = this.usedExp / 64;
            ChatUtils.message(this.trackedPlayer.getName() + " used: " + this.usedStacks + " Stacks of EXP.");
        }

        if (this.usedCStacks != this.usedCrystals / 64) {
            this.usedCStacks = this.usedCrystals / 64;
            ChatUtils.message(this.trackedPlayer.getName() + " used: " + this.usedCStacks + " Stacks of Crystals.");
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(autoDisable.getValBoolean()) {
            super.setToggled(false);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(event.getEntity().equals(mc.player) || event.getEntity().equals(trackedPlayer)) {
            this.usedExp = 0;
            this.usedStacks = 0;
            this.usedCrystals = 0;
            this.usedCStacks = 0;

            if(autoDisable.getValBoolean()) {
                super.setToggled(false);
            }
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (mc.player != null && mc.world != null && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (Tracker.mc.player.getHeldItem(packet.hand).getItem() == Items.END_CRYSTAL && !AntiTrap.instance.placedPos.contains(packet.position) && !AutoRer.instance.placedList.contains(packet.position)) {
                this.manuallyPlaced.add(packet.position);
            }
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener2 = new Listener<>(event -> {
        if (mc.player != null && mc.world != null && (this.autoEnable.getValBoolean() || this.autoDisable.getValBoolean()) && event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            final String message = packet.getChatComponent().getFormattedText();
            if (this.autoEnable.getValBoolean() && (message.contains("has accepted your duel request") || message.contains("Accepted the duel request from")) && !message.contains("<")) {
                ChatUtils.message("Tracker will enable in 5 seconds.");
                this.timer.reset();
                this.shouldEnable = true;
            }
            else if (this.autoDisable.getValBoolean() && message.contains("has defeated") && message.contains(mc.player.getName()) && !message.contains("<")) {
                super.setToggled(false);
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> listener3 = new Listener<>(event -> {
        if(shouldEnable && timer.passedSec(5L) && !super.isToggled()) {
            super.setToggled(true);
        }
    });

    @EventHandler
    private final Listener<EventSpawnEntity> listener4 = new Listener<>(event -> {
        if (event.entity instanceof EntityExpBottle && Objects.equals(mc.world.getClosestPlayerToEntity(event.entity, 3.0), this.trackedPlayer)) {
            ++this.usedExp;
        }

        if (event.entity instanceof EntityEnderCrystal) {
            if (AntiTrap.instance.placedPos.contains(event.entity.getPosition().down())) {
                AntiTrap.instance.placedPos.remove(event.entity.getPosition().down());
            } else if (this.manuallyPlaced.contains(event.entity.getPosition().down())) {
                this.manuallyPlaced.remove(event.entity.getPosition().down());
            } else if (!AutoRer.instance.placedList.contains(event.entity.getPosition().down())) {
                ++this.usedCrystals;
            }
        }
    });
}
