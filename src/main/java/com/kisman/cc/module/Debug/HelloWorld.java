package com.kisman.cc.module.Debug;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import com.kisman.cc.util.ChatLoggerUtil;
import net.minecraftforge.common.MinecraftForge;


/**
 * Test module, print "Hello, World" into the chat every 5 seconds
 *
 * @author lava_frai
 */
public class HelloWorld extends Module {
    private final ChatLoggerUtil Logger;
    public HelloWorld() {
        super("HelloWorld", "Test module, print \"Hello, World\" into the chat every 5 seconds", Category.DEBUG);

        Logger = new ChatLoggerUtil(getName());
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        if(mc.player == null || mc.world == null) return;
        Logger.Log("Debug module enabled");
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        if(mc.player == null || mc.world == null) return;
        Logger.Error("Debug module disabled");
    }

    @Override
    public void update()
    {
        if(mc.player == null || mc.world == null) return;
        counter++;
        if (counter > 20 * 5)
        {
            Logger.LogLackluster("Hello, World!");
            counter = 0;
        }
    }

    private int counter = 0;
}
