package com.kisman.cc.util.mixin;

import net.minecraft.client.Minecraft;

public class Mapping {
	public static String ENTRY_ORDERING = isNotObfuscated() ? "ENTRY_ORDERING" : "field_175252_a";

    public static boolean isNotObfuscated() {
        try { return Minecraft.class.getDeclaredField("instance") != null;
        } catch (Exception ex) { return false; }
    }
}
