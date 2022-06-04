package com.kisman.cc.settings;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.lua.settings.LuaSetting;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.Module;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit him
 *
 *  @author HeroCode
 */
public class Setting {
	private Supplier<Boolean> visibleSuppliner = () -> true;
	private Colour colour;

	private Entity entity;

	private int index = 0;
	private int color;
	private int key = Keyboard.KEY_NONE;
	
	private String name;
	private Module parent;
	private Setting setparent;
	private HudModule hudParent;
	private String mode;

	private String title;

	private String sval;
	private String dString;
	private ArrayList<String> options;
	private Enum optionEnum;
	
	private boolean bval;
	private boolean rainbow;
	private boolean syns;
	private boolean hud = false;
	private boolean opening;
	private boolean onlyOneWord;
	private boolean onlyNumbers;
	private boolean minus;
	private boolean enumCombo = false;

	private double dval;
	private double min;
	private double max;

	private float[] colorHSB;
	private ItemStack[] items;

	private int r, g, b, a;

	private int x1, y1, x2, y2;

	private float red, green, blue, alpha;

	private boolean onlyint = false;

	private Slider.NumberType numberType = Slider.NumberType.DECIMAL;

	public Setting(String type) {mode = type;}

	public Setting(String name, Module parent, int key) {
		this.name = name;
		this.parent = parent;
		this.key = key;
		this.mode = "Bind";
	}

	public Setting(String name, Module parent, Setting setparent, String title) {
		this.name = name;
		this.parent = parent;
		this.setparent = setparent;
		this.title = title;
		this.mode = "CategoryLine";
	}

	public Setting(String name, Module parent, String title, boolean open) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.opening = open;
		this.mode = "Category";
	}

	public Setting(String name, Module parent, String sval, String dString, boolean opening) {
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.dString = dString;
		this.opening = opening;
		this.onlyOneWord = false;
		this.minus = true;
		this.onlyNumbers = false;
		this.mode = "String";
	}

	public Setting(String name, Module parent, String sval, String dString, boolean opening, boolean onlyOneWord) {
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.dString = dString;
		this.opening = opening;
		this.onlyOneWord = onlyOneWord;
		this.minus = true;
		this.onlyNumbers = false;
		this.mode = "String";
	}

	public Setting(String name, Module parent, String gays, String lgbtq) {
		this.name = name;
		this.parent = parent;
		this.title = gays;
		this.sval = lgbtq;
		this.mode = "yep";
	}

	public Setting(String name, Module parent, String title) {
		this.name = name;
		this.title = title;
		this.parent = parent;
		this.mode = "Line";
	}

	public Setting(String name, Module parent, String sval, ArrayList<String> options){
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.options = options;
		this.optionEnum = null;
		this.mode = "Combo";
	}

	public Setting(String name, Module parent, String sval, List<String> options){
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.options = new ArrayList<>(options);
		this.optionEnum = null;
		this.mode = "Combo";
	}

	public Setting(String name, Module parent, Enum options){
		this.name = name;
		this.parent = parent;
		this.sval = options.name();
		this.options = null;
		this.optionEnum = options;
		this.enumCombo = true;
		this.mode = "Combo";
	}
	
	public Setting(String name, Module parent, boolean bval){
		this.name = name;
		this.parent = parent;
		this.bval = bval;
		this.mode = "Check";
	}

	public Setting(String name, HudModule parent, boolean bval) {
		this.name = name;
		this.hudParent = parent;
		this.bval = bval;
		this.mode = "CheckHud";
		this.hud = true;
	}

	public Setting(String name, Module parent, float red, float green, float blue, float alpha) {
		this.name = name;
		this.parent = parent;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.mode = "ExampleColor";
	}

	public Setting(String name, Module parent, double dval, double min, double max, Slider.NumberType numberType){
		this.name = name;
		this.parent = parent;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = numberType.equals(Slider.NumberType.INTEGER);
		this.mode = "Slider";
		this.numberType = numberType;
	}

	public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint){
		this.name = name;
		this.parent = parent;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
		this.mode = "Slider";
		this.numberType = onlyint ? Slider.NumberType.INTEGER : Slider.NumberType.DECIMAL;
	}

	public Setting(String name, Module parent, String title, Colour colour) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.colour = colour;
		float[] color = Color.RGBtoHSB(colour.r, colour.g, colour.b, null);
		this.r = colour.r;
		this.g = colour.g;
		this.b = colour.b;
		this.a = colour.a;
		this.mode = "ColorPicker";
	}

	public Setting(String name, Module parent, String title, Entity entity) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.entity = entity;
		this.mode = "Preview";
	}

	public Setting(String name, Module parent, String title, ItemStack[] items) {
		this.name = name;
		this.parent = parent;
		this.title = title;
		this.items = items;
		this.mode = "Items";
	}

	public Enum getEnumByName() {
		if(optionEnum == null) return null;
		Enum enumVal = optionEnum;
		String[] values = Arrays.stream(enumVal.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
		return Enum.valueOf(enumVal.getClass(), values[index]);
	}

	@Override
	public boolean equals(Object obj) {
		if(isCombo()) return sval.equals(obj);
		return false;
	}

	@Override
	public String toString() {
		if(isCombo()) return getValString();
		if(isCheck()) return String.valueOf(getValBoolean());
		if(isSlider()) return String.valueOf(onlyint ? getValInt() : getValDouble());
		if(isString()) return getValString();
		if(isColorPicker()) return TextUtil.get32BitString((colour != null) ? colour.getRGB() : new Color(1f, 1f, 1f).getRGB()) + "-" + syns + "-" + rainbow;
		return super.toString();
	}

	public boolean isNoneKey() {return key == Keyboard.KEY_NONE;}

	public boolean checkValString(String str) {
		return sval.equalsIgnoreCase(str);
	}

	public boolean isVisible() {
		return visibleSuppliner.get();
	}

	public Setting setVisible(Supplier<Boolean> suppliner) {
		visibleSuppliner = suppliner;

		return this;
	}

	public Setting setVisible(boolean visible) {
		visibleSuppliner = () -> visible;
		return this;
	}

	public String[] getStringValues() {
		if(!enumCombo) return options.toArray(new String[options.size()]);
		else return Arrays.stream(optionEnum.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	public String getStringFromIndex(int index) {
		if(index != -1) return getStringValues()[index];
		else return "";
	}

	public int getSelectedIndex() {
		String[] modes = getStringValues();
		int object = 0;

		for(int i = 0; i < modes.length; i++) {
			String mode = modes[i];

			if(mode.equalsIgnoreCase(sval)) object = i;
		}

		return object;
	}

	public boolean isOnlyint() {
		return onlyint;
	}


	public Slider.NumberType getNumberType() {
		return numberType;
	}

	public void setNumberType(Slider.NumberType numberType) {
		this.numberType = numberType;
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public boolean isOnlyNumbers() {
		return onlyNumbers;
	}

	public Setting setOnlyNumbers(boolean onlyNumbers) {
		this.onlyNumbers = onlyNumbers;
		return this;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getValInt() {
		return (int) this.dval;
	}

	public boolean isOnlyOneWord() {
		return onlyOneWord;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
		float[] color = Color.RGBtoHSB(colour.r, colour.g, colour.b, null);
	}

	public Enum getValEnum() {
		return optionEnum.valueOf(optionEnum.getClass(), sval);
	}

	public Setting getSetparent() {
		return setparent;
	}

	public String getdString() {
		return dString;
	}

	public int getX1() {
		return this.x1;
	}
	
	public int getY1() {
		return this.y1;
	}
	
	public int getX2() {
		return this.x2;
	}
	
	public int getY2() {
		return this.y2;
	}

	public void setX1(int num) {
		this.x1 = num;
	}

	public void setY1(int num) {
		this.y1 = num;
	}

	public void setX2(int num) {
		this.x2 = num;
	}

	public void setY2(int num) {
		this.y2 = num;
	}

	public HudModule getParentHudModule() {
		return this.hudParent;
	}

	public boolean isHud() {
		return this.hud;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName(){
		return name;
	}

	public Setting setName(String name) {
		this.name = name;
		return this;
	}
	
	public Module getParentMod(){
		return parent;
	}
	
	public String getValString(){
		return this.sval;
	}
	
	public Setting setValString(String in){
		this.sval = in;
		return this;
	}
	
	public ArrayList<String> getOptions(){
		return this.options;
	}

	public Setting setOptions(String... options) {
		this.options = new ArrayList<>(Arrays.asList(options));
		return this;
	}

	public Setting setOptions(List<String> options) {
		this.options = new ArrayList<>(options);
		return this;
	}

	//#Lua
	//TODO: доделать
	public Setting build(Module module) {
		Kisman.instance.settingsManager.rSetting(this);
		return this;
	}
	
	public boolean getValBoolean(){
		return this.bval;
	}
	
	public Setting setValBoolean(boolean in){
		this.bval = in;
		return this;
	}
	
	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int) dval;
		}
		return this.dval;
	}

	public float getValFloat() {
		if(onlyint) {
			dval = (int) dval;
		}

		return (float) dval;
	}

	public long getValLong() {
		if(onlyint) {
			dval = (int) dval;
		}

		return (long) dval;
	}

	public Setting setValDouble(double in){
		this.dval = in;
		return this;
	}
	
	public double getMin(){
		return this.min;
	}
	
	public double getMax(){
		return this.max;
	}

	public Setting setMin(double min) {
		this.min = min;
		return this;
	}

	public Setting setMax(double max) {
		this.max = max;
		return this;
	}

	public Setting setType(String type) {
		numberType = LuaSetting.getNumberTypeByName(type);
		return this;
	}

	public boolean isRainbow() {
		return this.rainbow;
	}

	public void setRainbow(boolean rainbow) {
		this.rainbow = rainbow;
	}

	public boolean isItems() { return mode.equalsIgnoreCase("Items"); }

	public boolean isPreview() { return mode.equalsIgnoreCase("Preview"); }

	public boolean isBind() { return mode.equalsIgnoreCase("Bind"); }

	public boolean isCategory() { return this.mode.equalsIgnoreCase("Category"); }

	public boolean isString() { return this.mode.equalsIgnoreCase("String"); }

	public boolean isVoid() { return this.mode.equalsIgnoreCase("Void"); }
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo");
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check");
	}

	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider");
	}

	public boolean isLine() {
		return this.mode.equalsIgnoreCase("Line");
	}

	public boolean isColorPicker() {
		return this.mode.equalsIgnoreCase("ColorPicker");
	}

	public boolean onlyInt(){
		return this.onlyint;
	}
}