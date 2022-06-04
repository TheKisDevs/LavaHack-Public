package com.kisman.cc.gui;

import com.kisman.cc.util.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ClickGui extends GuiScreen {
	public static boolean line = false;
	public static boolean rainbowLine = false;
	public static boolean rainbowBackground = false;

	public static LineMode lineMode = LineMode.LEFT;
	public static LineMode setLineMode = LineMode.SETTINGDEFAULT;

	public static int RLine = 255;
	public static int GLine = 0;
	public static int BLine = 0;
	public static int ALine = 150;

	public static int RBackground = 80;
	public static int GBackground = 75;
	public static int BBackground = 75;
	public static int ABackground = 150;

	public static int RABackground = 136;
	public static int GABackground = 189;
	public static int BABackground = 189;
	public static int AABackground = 255;

	public static int RHoveredModule = 95;
	public static int GHoveredModule = 95;
	public static int BHoveredModule = 87;
	public static int AHoveredModule = 150;

	public static int RNoHoveredModule = 14;
	public static int GNoHoveredModule = 14;
	public static int BNoHoveredModule = 14;
	public static int ANoHoveredModule = 255;


	public static int RText = 166;
	public static int GText = 161;
	public static int BText = 160;
	public static int AText = 255;

	public static int RActiveText = 255;
	public static int GActiveText = 255;
	public static int BActiveText = 255;
	public static int AActiveText = 255;

	public static LineMode getSetLineMode() {
		return setLineMode;
	}

	public static void setSetLineMode(LineMode setLineMode) {
		ClickGui.setLineMode = setLineMode;
	}

	public static int getRABackground() {
		return RABackground;
	}

	public static void setRABackground(int RABackground) {
		ClickGui.RABackground = RABackground;
	}

	public static int getGABackground() {
		return GABackground;
	}

	public static void setGABackground(int GABackground) {
		ClickGui.GABackground = GABackground;
	}

	public static int getBABackground() {
		return BABackground;
	}

	public static void setBABackground(int BABackground) {
		ClickGui.BABackground = BABackground;
	}

	public static int getAABackground() {
		return AABackground;
	}

	public static void setAABackground(int AABackground) {
		ClickGui.AABackground = AABackground;
	}

	public static boolean isRainbowBackground() {
		return rainbowBackground;
	}

	public static void setRainbowBackground(boolean rainbowBackground) {
		ClickGui.rainbowBackground = rainbowBackground;
	}

	public static boolean isRainbowLine() {
		return rainbowLine;
	}

	public static void setRainbowLine(boolean rainbowLine) {
		ClickGui.rainbowLine = rainbowLine;
	}

	public static int getRHoveredModule() {
		return RHoveredModule;
	}

	public static void setRHoveredModule(int RHoveredModule) {
		ClickGui.RHoveredModule = RHoveredModule;
	}

	public static int getGHoveredModule() {
		return GHoveredModule;
	}

	public static void setGHoveredModule(int GHoveredModule) {
		ClickGui.GHoveredModule = GHoveredModule;
	}

	public static int getBHoveredModule() {
		return BHoveredModule;
	}

	public static void setBHoveredModule(int BHoveredModule) {
		ClickGui.BHoveredModule = BHoveredModule;
	}

	public static int getAHoveredModule() {
		return AHoveredModule;
	}

	public static void setAHoveredModule(int AHoveredModule) {
		ClickGui.AHoveredModule = AHoveredModule;
	}

	public static int getRNoHoveredModule() {
		return RNoHoveredModule;
	}

	public static void setRNoHoveredModule(int RNoHoveredModule) {
		ClickGui.RNoHoveredModule = RNoHoveredModule;
	}

	public static int getGNoHoveredModule() {
		return GNoHoveredModule;
	}

	public static void setGNoHoveredModule(int GNoHoveredModule) {
		ClickGui.GNoHoveredModule = GNoHoveredModule;
	}

	public static int getBNoHoveredModule() {
		return BNoHoveredModule;
	}

	public static void setBNoHoveredModule(int BNoHoveredModule) {
		ClickGui.BNoHoveredModule = BNoHoveredModule;
	}

	public static int getANoHoveredModule() {
		return ANoHoveredModule;
	}

	public static void setANoHoveredModule(int ANoHoveredModule) {
		ClickGui.ANoHoveredModule = ANoHoveredModule;
	}

	public static int getRActiveText() {
		return RActiveText;
	}

	public static void setRActiveText(int RActiveText) {
		ClickGui.RActiveText = RActiveText;
	}

	public static int getGActiveText() {
		return GActiveText;
	}

	public static void setGActiveText(int GActiveText) {
		ClickGui.GActiveText = GActiveText;
	}

	public static int getBActiveText() {
		return BActiveText;
	}

	public static void setBActiveText(int BActiveText) {
		ClickGui.BActiveText = BActiveText;
	}

	public static int getAActiveText() {
		return AActiveText;
	}

	public static void setAActiveText(int AActiveText) {
		ClickGui.AActiveText = AActiveText;
	}

	public static int getRText() {
		return RText;
	}

	public static void setRText(int RText) {
		ClickGui.RText = RText;
	}

	public static int getGText() {
		return GText;
	}

	public static void setGText(int GText) {
		ClickGui.GText = GText;
	}

	public static int getBText() {
		return BText;
	}

	public static void setBText(int BText) {
		ClickGui.BText = BText;
	}

	public static int getAText() {
		return AText;
	}

	public static void setAText(int AText) {
		ClickGui.AText = AText;
	}

	public static LineMode getLineMode() {
		return lineMode;
	}

	public static void setLineMode(LineMode lineMode) {
		ClickGui.lineMode = lineMode;
	}

	public static int getALine() {
		return ALine;
	}

	public static void setALine(int ALine) {
		ClickGui.ALine = ALine;
	}

	public static int getABackground() {
		return ABackground;
	}

	public static void setABackground(int ABackground) {
		ClickGui.ABackground = ABackground;
	}

	public static boolean isLine() {
		return line;
	}

	public static void setLine(boolean line) {
		ClickGui.line = line;
	}

	public static int getRLine() {
		return RLine;
	}

	public static void setRLine(int RLine) {
		ClickGui.RLine = RLine;
	}

	public static int getGLine() {
		return GLine;
	}

	public static void setGLine(int GLine) {
		ClickGui.GLine = GLine;
	}

	public static int getBLine() {
		return BLine;
	}

	public static void setBLine(int BLine) {
		ClickGui.BLine = BLine;
	}

	public static int getRBackground() {
		return RBackground;
	}

	public static void setRBackground(int RBackground) {
		ClickGui.RBackground = RBackground;
	}

	public static int getGBackground() {
		return GBackground;
	}

	public static void setGBackground(int GBackground) {
		ClickGui.GBackground = GBackground;
	}

	public static int getBBackground() {
		return BBackground;
	}

	public static void setBBackground(int BBackground) {
		ClickGui.BBackground = BBackground;
	}
}