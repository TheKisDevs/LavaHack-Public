package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import com.kisman.cc.hypixel.util.ApiHandler;
import com.kisman.cc.hypixel.util.ConfigHandler;
import com.kisman.cc.hypixel.util.Utils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.config.Configuration;

import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Flip extends Command {
    public static LinkedHashMap<String, Double> initialDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> secondDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> namedDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Double> avgDataset = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> demandDataset = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, Long> updatedDataset = new LinkedHashMap<>();
    public static ArrayList<String> ignoredUUIDs = new ArrayList<>();
    public static double purse;
    public static ArrayList<String> commands = new ArrayList<>();
    public static ArrayList<String> rawNames = new ArrayList<>();
    public static ArrayList<Double> percentageProfit = new ArrayList<>();
    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(ConfigHandler.threads);
    private static int auctionPages = 0;

    private static int cycle = 0;
    private static Timer timer = new Timer();

    public Flip() {
        super("Flip");
    }

    public void runCommand(String s, String[] args) {
        if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")) {
            if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
                ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                        "Flip",
                        "false"
                );
            } else if(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("false")) {
                ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                        "Flip",
                        "true"
                );
            }
        } else {
            ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL,
                    "Flip",
                    "true"
            );
        }

        flip(mc.player);
    }

    public String getDescription() {
        return "";
    }

    public String getSyntax() {
        return "flip";
    }

    public static void flip(EntityPlayer sender) {
        timer.cancel();
        timer.purge();
        timer = new Timer();
        if (ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "Flip").equals("true")) {
            message(
                    TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" +
                            TextFormatting.GREEN + " Flipper alerts enabled"
            );

            ApiHandler.getBins(initialDataset);
            ApiHandler.itemIdsToNames(initialDataset);

            timer.scheduleAtFixedRate(
                    new TimerTask() {
                        @Override
                        public void run() {
                            auctionPages = ApiHandler.getNumberOfPages() - 1;
                            if (cycle == auctionPages) {
                                cycle = 0;
                            }

                            try {
                                ApiHandler.updatePurseCoins(ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "ApiKey"), mc.player.getName());
                            } catch (Exception e) {
                                error(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" + "Could not load purse.");
                            }

                            String name = sender.getName();
                            String id = ConfigHandler.getString(Configuration.CATEGORY_GENERAL, "APIKey");
                            try {
                                ApiHandler.updatePurseCoins(id, name);
                            } catch (Exception e) {
                                error(
                                        TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" +
                                                TextFormatting.RED + "Could not load purse."
                                );
                            }
                            ApiHandler.getFlips(secondDataset, cycle, commands);
                            if (namedDataset.size() > 0) {
                                purse = Math.round(purse);
	                  /*ChatComponentText runtext = new ChatComponentText(
	                  	EnumChatFormatting.GOLD + ("NEC ") + EnumChatFormatting.AQUA + ("Suggested Flips:")
	                  );
	                  sender.addChatMessage(runtext);
	                  if (!enable) {
	                  	return;
	                  }
	                  sender.addChatMessage(
	                  		new ChatComponentText(
	                  			EnumChatFormatting.GOLD + "Your Budget: " + EnumChatFormatting.WHITE + (long) purse + "\n"
	                  		)
	                  	);*/
                                int count = 0;
                                int demand = demandDataset.getOrDefault(rawNames.get(count), 0);

                                for (Map.Entry<String, Double> entry : namedDataset.entrySet()) {
                                    long profit = Math.abs(entry.getValue().longValue());
                                    complete(
                                            TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "] " +
                                                    TextFormatting.YELLOW + entry.getKey() + " " +
                                                    (profit > 200_000 || purse / 5 < 100_000 ?
                                                            TextFormatting.GREEN :
                                                            profit > 100_000 || purse / 5 < 200_000 ?
                                                                    TextFormatting.GOLD :
                                                                    TextFormatting.YELLOW) +
                                                    "+$" +
                                                    Utils.formatValue(profit) + " " + TextFormatting.GOLD + "PP:" + " "
                                                    + TextFormatting.GREEN + percentageProfit.get(count).intValue() + "%" + " "
                                                    + TextFormatting.GOLD
                                                    + (demand == 0 ? "Sales:" + " " + TextFormatting.GREEN + demand + "/day" : "")
                                    );
                                    count++;
                                }
                            }
                            namedDataset.clear();
                            commands.clear();
                            rawNames.clear();
                            percentageProfit.clear();
                            cycle++;
                        }
                    },
                    40,
                    40);
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            auctionPages = ApiHandler.getNumberOfPages() -1;
                            try {
                                ApiHandler.getBins(initialDataset);
                                ApiHandler.itemIdsToNames(initialDataset);
                            } catch (Exception e) {
                                error(
                                        TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" +
                                                TextFormatting.RED + " Could not load BINs."
                                );
                            }
                        }
                    },
                    60000,
                    60000);
        } else {
            message(
                    TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" +
                            TextFormatting.RED + " Flipper alerts disabled"
            );
            timer.cancel();
            timer.purge();
            timer = new Timer(); // ладно
        }
    }
}
