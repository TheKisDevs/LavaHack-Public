package com.kisman.cc.util.manager;

import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import java.util.ArrayList;

public class ColorManager {
    public ArrayList<Setting> colorSettingsList = new ArrayList<>();

    public void update() {
        ArrayList<Setting> settingsWithRainbow = new ArrayList<>();
        colorSettingsList.stream().filter(Setting::isRainbow).forEach(settingsWithRainbow::add);
        if(settingsWithRainbow.isEmpty()) return;
        settingsWithRainbow.forEach(setting -> {
            float[] hsb = setting.getColour().RGBtoHSB();
            double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
            rainbowState %= 360.0;
            hsb[0] = (float) (rainbowState / 360.0);
            setting.setColour(Colour.fromHSB(hsb, setting.getColour().a));
        });
    }
}
