package com.kisman.cc.command;

import com.kisman.cc.Kisman;
import com.kisman.cc.console.rewrite.ConsoleGui;
import com.kisman.cc.event.events.client.console.ConsoleMessageEvent;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public abstract class Command {
	protected static Minecraft mc = Minecraft.getMinecraft();
	
	private String command;
	private String execute;
	private int key;
	
	public Command(String command) {
		this.command = command;
		this.key = -1;
	}

	public abstract void runCommand(String s, String[] args);
	public abstract String getDescription();
	public abstract String getSyntax();

	public String getCommand() {
		return command;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getExecute() {
		return execute;
	}
	public void setExecute(String execute) {
		this.execute = execute;
	}
	public static void message(String message) {
		if(mc.currentScreen instanceof ConsoleGui) new ConsoleMessageEvent(TextFormatting.GRAY + "[" + TextFormatting.WHITE + Kisman.getName() + TextFormatting.GRAY + "] " + message).post();
		else ChatUtils.message(message);
	}
	public static void warning(String message) {
		if(mc.currentScreen instanceof ConsoleGui) new ConsoleMessageEvent(TextFormatting.GRAY + "[" + TextFormatting.GOLD + Kisman.getName() + TextFormatting.GRAY + "] " + message).post();
		else ChatUtils.warning(message);
	}
	public static void complete(String message) {
		if(mc.currentScreen instanceof ConsoleGui) new ConsoleMessageEvent(TextFormatting.GRAY + "[" + TextFormatting.LIGHT_PURPLE + Kisman.getName() + TextFormatting.GRAY + "] " + message).post();
		else ChatUtils.complete(message);
	}
	public static void error(String message) {
		if(mc.currentScreen instanceof ConsoleGui) new ConsoleMessageEvent(TextFormatting.GRAY + "[" + TextFormatting.RED + Kisman.getName() + TextFormatting.GRAY + "] " + message).post();
		else ChatUtils.error(message);
	}
	public static void print(String message) {
		if(mc.currentScreen instanceof ConsoleGui) new ConsoleMessageEvent(message).post();
		else ChatUtils.simpleMessage(message);
	}
}
