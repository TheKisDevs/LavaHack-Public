package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class Weather extends Module {

    private final Setting weatherMode = new Setting("WeatherMode", this, "Mode", Arrays.asList("Mode", "Custom"));
    private final Setting weather = new Setting("Weather", this, "Sunny", Arrays.asList("Default", "Sunny", "Rain", "Thunder")).setVisible(() -> weatherMode.getValString().equals("Mode"));
    private final Setting weatherSlider = new Setting("Weather", this, 0, 0, 2, false).setVisible(() -> weatherMode.getValString().equals("Custom"));

    public Weather(){
        super("Weather", Category.RENDER);
        setmgr.rSetting(weatherMode);
        setmgr.rSetting(weather);
        setmgr.rSetting(weatherSlider);
    }

    @Override
    public void update(){
        if(mc.world == null || mc.player == null)
            return;

        float strength = weather.getIndex() == 0 ? weather.getIndex() - 1 : weatherSlider.getValFloat();

        if(weatherMode.getValString().equals("Custom") || weather.getIndex() > 0){
            mc.world.setRainStrength(strength);
        }
    }
}
