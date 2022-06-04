package com.kisman.cc.module.client;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.modules.CustomMainMenu;

public class CustomMainMenuModule extends Module {
    public Setting watermark = new Setting("WaterMark", this, true);
    public Setting customSplashText = new Setting("Custom Splash Text", this, true);
    public Setting customSplashFont = new Setting("Custom Splash Font", this, true).setVisible(() -> customSplashText.getValBoolean());
    public Setting particles = new Setting("Particles", this, true);

    public static CustomMainMenuModule instance;

    public CustomMainMenuModule() {
        super("CustomMainMenu", Category.CLIENT);

        instance = this;

        setmgr.rSetting(watermark);
        setmgr.rSetting(customSplashText);
        setmgr.rSetting(customSplashFont);
        setmgr.rSetting(particles);
    }

    public void update() {
        CustomMainMenu.WATERMARK = watermark.getValBoolean();
        CustomMainMenu.CUSTOM_SPLASH_TEXT = customSplashText.getValBoolean();
        CustomMainMenu.CUSTOM_SPLASH_FONT = customSplashFont.getValBoolean();
        CustomMainMenu.PARTICLES = particles.getValBoolean();
    }

    public void onDisable() {
        CustomMainMenu.WATERMARK = false;
        CustomMainMenu.CUSTOM_SPLASH_TEXT = false;
        CustomMainMenu.CUSTOM_SPLASH_FONT = false;
        CustomMainMenu.PARTICLES = false;
    }
}
