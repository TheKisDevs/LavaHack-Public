package com.kisman.cc.module;

import com.kisman.cc.Kisman;

import com.kisman.cc.settings.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Supplier;

public class Module {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected static SettingsManager setmgr;

	private String name, description, displayInfo;
	private int key;
	private int priority;
	private final Category category;
	public boolean toggled;
	public boolean subscribes;
	public boolean visible = true;
	public boolean hold = false;
	public boolean block = false;
	private Supplier<String> fun = null;

	public Module(String name, Category category) {this(name, "", category, 0, true);}
	public Module(String name, Category category, boolean subscribes) {this(name, "", category, 0, subscribes);}
	public Module(String name, String description, Category category) {this(name, description, category, 0, true);}

	public Module(String name, String description, Category category, int key, boolean subscribes) {
		this.name = name;
		this.description = description;
		this.displayInfo = "";
		this.key = key;
		this.category = category;
		this.toggled = false;
		this.subscribes = subscribes;
		this.priority = 1;

		setmgr = Kisman.instance.settingsManager;
	}

	public void setToggled(boolean toggled) {
		if(block) return;
		this.toggled = toggled;
		if (Kisman.instance.init && Kisman.instance.moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + getName() + TextFormatting.GRAY + " has been " + (isToggled() ? "enabled" : "disabled") + "!");
		if (this.toggled) onEnable();
		else onDisable();
	}

	public void toggle() {
		if(block) return;
		toggled = !toggled;
		if (Kisman.instance.init && Kisman.instance.moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + getName() + TextFormatting.GRAY + " has been " + (isToggled() ? "enabled" : "disabled") + "!");
		if (toggled) onEnable();
		else onDisable();
	}

	public Setting register(Setting set) {
		setmgr.rSetting(set);
		return set;
	}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public int getKey() {return key;}
	public int getPriority() {return priority;}
	public void setPriority(int priority) {this.priority = priority;}
	public void setKey(int key) {this.key = key;}
	public boolean isToggled() {return toggled;}
	public void onEnable() {if(subscribes) MinecraftForge.EVENT_BUS.register(this);}
	public void onDisable() {if(subscribes) MinecraftForge.EVENT_BUS.unregister(this);}
	public String getName() {return this.name;}
	public Category getCategory() {return this.category;}
	public String getCategoryName() {return this.category.name();} //lua
	public String getDisplayInfo() {return fun == null ? displayInfo : fun.get();}
	public void setDisplayInfo(String displayInfo) {this.displayInfo = displayInfo;}
	public void setDisplayInfo(Supplier<String> fun) {this.fun = fun;}
	public void update(){}
	public void render(){}
	public void key() {}
	public void key(int key) {}
	public void key(char typedChar, int key) {}
	@Override public String toString() {return getName();}
	public boolean isVisible() {return true;}
	public boolean isBeta() {return false;}
}
