package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BurrowCounter extends Module {
    private final ConcurrentHashMap<EntityPlayer, Integer> players = new ConcurrentHashMap<>();
    private final List<EntityPlayer> anti_spam = new ArrayList<>();

    public BurrowCounter() {
        super("BurrowCounter", "BurrowCounter", Category.MISC);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (anti_spam.contains(player)) continue;
            BlockPos pos = new BlockPos(player.posX, player.posY + 0.2D, player.posZ);
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) {
                add_player(player);
                anti_spam.add(player);
            }
        }
    }

    private void add_player(EntityPlayer player) {
        if (player == null) return;
        if (players.containsKey(player)) {
            int value = players.get(player) + 1;
            players.put(player, value);
            ChatUtils.warning(player.getName() + TextFormatting.DARK_RED + " has burrowed " + value + " times");
        } else {
            players.put(player, 1);
            ChatUtils.warning(player.getName() + TextFormatting.DARK_RED + " has burrowed");
        }
    }
}
