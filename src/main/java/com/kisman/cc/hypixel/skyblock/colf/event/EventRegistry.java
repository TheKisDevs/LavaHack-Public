package com.kisman.cc.hypixel.skyblock.colf.event;

import java.time.LocalDateTime;

import com.kisman.cc.hypixel.skyblock.colf.MainColf;
import com.kisman.cc.hypixel.skyblock.colf.commands.Command;
import com.kisman.cc.hypixel.skyblock.colf.commands.CommandType;
import com.kisman.cc.hypixel.skyblock.colf.commands.models.AuctionData;
import com.kisman.cc.hypixel.skyblock.colf.network.WSClient;
import com.mojang.realmsclient.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventRegistry {

	@SubscribeEvent
	public void onDisconnectedFromServerEvent(ClientDisconnectionFromServerEvent event) {
		if(MainColf.wrapper.isRunning) {
			System.out.println("Disconnected from server");
			MainColf.wrapper.stop();
			System.out.println("CoflSky stopped");
		}
	}

	public static String ExtractUuidFromInventory(IInventory inventory) {
		ItemStack stack = inventory.getStackInSlot(13);
		if (stack != null) {
			try {
				String uuid = stack.serializeNBT().getCompoundTag("tag").getCompoundTag("ExtraAttributes")
						.getString("uuid");
				if (uuid.length() == 0) {
					throw new Exception();
				}
				System.out.println("Item has the UUID: " + uuid);
				return uuid;
			} catch (Exception e) {
				System.out.println("Clicked item " + stack.getDisplayName() + " has the following meta: "
						+ stack.serializeNBT().toString());
			}
		}
		return "";
	}

	public static ItemStack GOLD_NUGGET = new ItemStack(Items.GOLD_NUGGET);

	public static final Pair<String, Pair<String, LocalDateTime>> EMPTY = Pair.of(null, Pair.of("",LocalDateTime.MIN));
	public static Pair<String, Pair<String, LocalDateTime>> last = EMPTY;
	
	@SubscribeEvent
	public void HandleChatEvent(ClientChatReceivedEvent sce) {
		if(MainColf.wrapper.isRunning && last.first() != null) {
			if(sce.getMessage().getUnformattedText().startsWith("You claimed ")) {
				AuctionData ad = new AuctionData();
				ad.setItemId(last.second().first());
				ad.setAuctionId("");
				Command<AuctionData> data = new Command<>(CommandType.PurchaseConfirm, ad);
				MainColf.wrapper.SendMessage(data);
				System.out.println("PurchaseConfirm");
				last = EMPTY;
			} else if(last.second().second().plusSeconds(10).isBefore(LocalDateTime.now())) {
				last = EMPTY;
			}
		}
	}
	
	public static long lastStartTime = Long.MAX_VALUE;
		
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void OnGuiClick(GuiScreenEvent.MouseInputEvent mie) {
		if (MainColf.wrapper.isRunning) {
			if (mie.getGui() instanceof GuiChest) { // verify that it's really a chest
				ContainerChest chest = (ContainerChest) ((GuiChest) mie.getGui()).inventorySlots;

				IInventory inv = chest.getLowerChestInventory();
				if (inv.hasCustomName()) { // verify that the chest actually has a custom name
					String chestName = inv.getName();

					if (chestName.equalsIgnoreCase("BIN Auction View") || chestName.equalsIgnoreCase("Ekwav")) {
					
						ItemStack heldItem = Minecraft.getMinecraft().player.inventory.getItemStack();
						
						if (heldItem != null) {
							System.out.println("Clicked on: " + heldItem.getItem().getRegistryName());
							String itemUUID = ExtractUuidFromInventory(inv);
							if((System.currentTimeMillis()+200) < lastStartTime) {
								if (heldItem.isItemEqual(GOLD_NUGGET)) {
									AuctionData ad = new AuctionData();
									ad.setItemId(itemUUID);
									ad.setAuctionId("");
									Command<AuctionData> data = new Command<>(CommandType.PurchaseStart, ad);
									MainColf.wrapper.SendMessage(data);
									System.out.println("PurchaseStart");
									last = Pair.of("You claimed ", Pair.of(itemUUID, LocalDateTime.now()));
									lastStartTime = System.currentTimeMillis();
								} 
							}
						}
					}
				}
			}
		}
	}
}
