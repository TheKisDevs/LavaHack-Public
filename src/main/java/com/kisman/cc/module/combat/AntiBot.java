package com.kisman.cc.module.combat;

import com.google.common.collect.Ordering;
import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.mixin.util.GuiPlayerTabOverlayUtil;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

import java.util.*;

public class AntiBot extends Module {
    public Setting mode = new Setting("Mode", this, "WellMore", Arrays.asList("Matrix 6.3", "Classic", "Vanish", "Zamorozka"));

    private List<EntityPlayer> bots = new ArrayList<>();
    public static AntiBot instance;
    private boolean clicked = false;

	public AntiBot() {
		super("AntiBot", "Prevents you from targetting bots", Category.COMBAT);

        instance = this;

        setmgr.rSetting(mode);
	}

	public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mode.checkValString("Zamorozka") && mc.currentScreen == null && Mouse.isButtonDown(0)) {
            if(!clicked) {
                clicked = true;
                RayTraceResult result = mc.objectMouseOver;
                if(result == null || result.typeOfHit != RayTraceResult.Type.ENTITY) return;
                Entity entity = mc.objectMouseOver.entityHit;
                if(entity == null || !(entity instanceof EntityPlayer)) return;
                Kisman.target_by_click = (EntityPlayer) entity;
                ChatUtils.complete("[AntiBot] Current target is " + entity.getName());
            } else clicked = false;
        }

        for (final EntityPlayer entity : mc.world.playerEntities) {
            if (entity != mc.player && !entity.isDead) {
                if(mode.getValString().equalsIgnoreCase("Classic")) {
                    List<EntityPlayer> tabList = getTabPlayerList();
                    if(!bots.contains(entity) && !tabList.contains(entity)) bots.add(entity);
                    else if(bots.contains(entity) && tabList.contains(entity)) bots.remove(entity);
                } else {
                    if(mode.getValString().equalsIgnoreCase("Matrix 6.3")) {
                        final boolean contains = RotationUtils.isInFOV(entity, mc.player, 100.0) && AntiBot.mc.player.getDistance(entity) <= 6.5 && entity.canEntityBeSeen(mc.player);
                        final boolean speedAnalysis = entity.getActivePotionEffect(MobEffects.SPEED) == null && entity.getActivePotionEffect(MobEffects.JUMP_BOOST) == null && entity.getActivePotionEffect(MobEffects.LEVITATION) == null && !entity.isInWater() && entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA && EntityUtil.getSpeedBPS(entity) >= 11.9;
                        if (!contains || !speedAnalysis || entity.isDead) continue;
                    } else if(!entity.isInvisible()) continue;
                    entity.isDead = true;
                    ChatUtils.complete(entity.getName() + " was been deleted!");
                }
            }
        }

        if(mode.getValString().equalsIgnoreCase("Classic")) for(EntityPlayer bot : bots) {
            bot.isDead = true;
            ChatUtils.complete(bot.getName() + " was been deleted!");
        }
	}

    public void onEnable() {
        clicked = false;
    }

    public void onDisable() {
        Kisman.target_by_click = null;
    }

    private List<EntityPlayer> getTabPlayerList() {
        final List<EntityPlayer> list = new ArrayList<>();
        final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = GuiPlayerTabOverlayUtil.getEntityOrdering();
        if (ENTRY_ORDERING == null) return list;
        final List<NetworkPlayerInfo> players = ENTRY_ORDERING.sortedCopy(mc.playerController.connection.getPlayerInfoMap());
        for (final NetworkPlayerInfo info : players) {
            if (info == null) continue;
            list.add(mc.world.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }
}