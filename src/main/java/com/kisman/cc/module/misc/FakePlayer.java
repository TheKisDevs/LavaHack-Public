package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

import java.util.UUID;

public class FakePlayer extends Module {
    private Setting name = new Setting("Name", this, "FinLicorice", "FinLicorice", true);

    public FakePlayer() {
        super("FakePlayer", "FakePlayer", Category.MISC);
        super.setDisplayInfo("[" + name.getValString() + TextFormatting.GRAY + "]");

        setmgr.rSetting(name);
    }

    public void onEnable() {
        if(mc.player == null || mc.world == null) {
            super.setToggled(false);
            return;
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), name.getValString()));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-1337, clonedPlayer);
        clonedPlayer.onLivingUpdate();
    }

    public void update() {
        if(mc.player == null || mc.world == null) super.setToggled(false);
    }

    public void onDisable() {
        if(mc.world == null || mc.player == null) return;
        mc.world.removeEntityFromWorld(-1337);
    }
}
