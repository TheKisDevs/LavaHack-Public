package com.kisman.cc.module.chat.totempopcounter;

import net.minecraft.entity.player.EntityPlayer;

public class Totem {
    public EntityPlayer player;
    public int popsCount;

    public Totem(EntityPlayer player) {
        this.player = player;
        this.popsCount = 0;
    }

    public void addPop() {
        popsCount++;
    }
}
