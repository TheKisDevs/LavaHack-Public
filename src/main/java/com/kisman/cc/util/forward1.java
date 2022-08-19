package com.kisman.cc.util;

import net.minecraft.client.Minecraft;

public class forward1 {
    static Minecraft mc;
    public static float forward = mc.player.movementInput.moveForward;
    public  void forward(){
        float forward = mc.player.movementInput.moveForward;
    }
}
