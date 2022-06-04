package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.render.ArrayListModule;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;

import java.util.*;

public class HUD extends Module {
	public static HUD instance;

	public Setting astolfoColor = new Setting("Astolfo Color", this, false);
	public Setting offsets = new Setting("Offsets", this, 2, 0, 10, true);
	public Setting glow = new Setting("Glow", this, false);
	public Setting glowV2 = new Setting("Second glow", this, false);
	public Setting glowOffset = new Setting("Glow Offset", this, 5, 1, 20, true);
	public Setting glowRadius = new Setting("Glow Radius", this, 0, 0, 20, true);
	public Setting glowAlpha = new Setting("Glow Alpha", this, 255, 0, 255, true);
	public Setting background = new Setting("Background", this, false);
	public Setting bgAlpha = new Setting("Bg Alpha", this, 255, 0, 255, true);

	private final Setting arrLine = new Setting("ArrLine", this, "ArrayList").setVisible(() -> ArrayListModule.instance.isToggled());
	public Setting arrMode = new Setting("ArrayList Mode", this, "RIGHT", new ArrayList<>(Arrays.asList("LEFT", "RIGHT"))).setVisible(() -> ArrayListModule.instance.isToggled());
	public Setting arrY = new Setting("ArrayList Y", this, 150, 0, mc.displayHeight, true).setVisible(() -> ArrayListModule.instance.isToggled());
	public Setting arrColor = new Setting("ArrayList Color", this, " ArrayList Color", new Colour(255, 0, 0, 255)).setVisible(() -> ArrayListModule.instance.isToggled());
	public Setting arrGradient = new Setting("Array Gradient", this, Gradient.None).setVisible(() -> ArrayListModule.instance.isToggled());
	public Setting arrGradientDiff = new Setting("Array Gradient Diff", this, 200, 0, 1000, Slider.NumberType.TIME).setVisible(() -> ArrayListModule.instance.isToggled() && !arrGradient.getValString().equalsIgnoreCase(Gradient.None.name()));
	public Setting arrGlowBackground = new Setting("Array Glow Background", this, false).setVisible(() -> ArrayListModule.instance.isToggled());

	private final Setting welLine = new Setting("WelLine", this, "Welcomer");
	public Setting welColor = new Setting("WelColor", this, "WelcomerColor", new Colour(255, 0, 0, 255));

	private final Setting pvpLine = new Setting("PvpLine", this, "PvpInfo");
	public Setting pvpY = new Setting("PvpInfo Y", this, 200, 0, mc.displayHeight, true);

	private final Setting armLine = new Setting("ArmLine", this, "Armor");
	public Setting armExtra = new Setting("Extra Info", this, false);
	public Setting armDmg = new Setting("Damage", this, false);

	private final Setting radarLine = new Setting("RadarLine", this, "Radar");
	public Setting radarDist = new Setting("Max Distance", this, 50, 10, 50, true);

	private final Setting speedLine = new Setting("SpeedLine", this, "Speed");
	public Setting speedMode = new Setting("Speed Mode", this, "km/h", new ArrayList<>(Arrays.asList("b/s", "km/h")));

	private final Setting logoLine = new Setting("LogoLine", this, "Logo");
	public Setting logoMode = new Setting("Logo Mode", this, LogoMode.Simple);
	public Setting logoImage = new Setting("Logo Image", this, LogoImage.New).setVisible(() -> logoMode.checkValString("Image"));
	public Setting logoGlow = new Setting("Glow", this, false);
	public Setting logoBold = new Setting("Name Bold", this, false);

	private Setting indicLine = new Setting("IndicLine", this, "Indicators");
	public Setting indicThemeMode = new Setting("Indicators Theme", this, IndicatorsThemeMode.Default);
	public Setting indicShadowSliders = new Setting("Indicators Shadow Sliders", this, false);

	private Setting thudLine = new Setting("ThudLine", this, "TargetHud");
	public Setting thudTheme = new Setting("TargetHud Theme", this, TargetHudThemeMode.Vega);
	public Setting thudShadowSliders = new Setting("TargetHud Shadow Sliders", this, false);

	private Setting crystalpsLine = new Setting("CrystalPRLine", this, "Crystal Per Second");
	public Setting crystalpsY = new Setting("Crystal Per Second Y", this, 50, 0, mc.displayHeight, true); 

	public HUD() {
		super("HudEditor", "hud editor", Category.CLIENT);

		instance = this;

		setmgr.rSetting(astolfoColor);
		setmgr.rSetting(offsets);
		setmgr.rSetting(glow);
		setmgr.rSetting(glowV2);
		setmgr.rSetting(glowOffset);
		setmgr.rSetting(glowRadius);
		setmgr.rSetting(glowAlpha);
		setmgr.rSetting(background);
		setmgr.rSetting(bgAlpha);

		setmgr.rSetting(arrLine);
		setmgr.rSetting(arrMode);
		setmgr.rSetting(arrY);
		setmgr.rSetting(arrColor);
		setmgr.rSetting(arrGradient);
		setmgr.rSetting(arrGradientDiff);
		setmgr.rSetting(arrGlowBackground);

		setmgr.rSetting(welLine);
		setmgr.rSetting(welColor);

		setmgr.rSetting(pvpLine);
		setmgr.rSetting(pvpY);

		setmgr.rSetting(armLine);
		setmgr.rSetting(armExtra);
		setmgr.rSetting(armDmg);

		setmgr.rSetting(radarLine);
		setmgr.rSetting(radarDist);

		setmgr.rSetting(speedLine);
		setmgr.rSetting(speedMode);

		setmgr.rSetting(logoLine);
		setmgr.rSetting(logoMode);
		setmgr.rSetting(logoImage);
		setmgr.rSetting(logoGlow);
		setmgr.rSetting(logoBold);

		setmgr.rSetting(indicLine);
		setmgr.rSetting(indicThemeMode);
		setmgr.rSetting(indicShadowSliders);

		setmgr.rSetting(thudLine);
		setmgr.rSetting(thudTheme);
		setmgr.rSetting(thudShadowSliders);

		setmgr.rSetting(crystalpsLine);
		setmgr.rSetting(crystalpsY);
	}

	public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.hudGui);
		super.setToggled(false);
	}

	public enum LogoMode {Simple, CSGO, Image, GishCode}
	public enum LogoImage {Old, New}
	public enum Gradient {None, Rainbow, Astolfo, Pulsive}
	public enum IndicatorsThemeMode {Default, Rewrite}
	public enum TargetHudThemeMode {Vega, Rewrite, NoRules, Simple, Astolfo}
}
