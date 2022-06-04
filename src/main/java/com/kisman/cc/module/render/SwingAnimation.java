package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class SwingAnimation extends Module {
    public static SwingAnimation instance;

    public Setting mode = new Setting("Mode", this, "Strong", new ArrayList<>(Arrays.asList("Hand", "Strong")));


    private Setting simpleLine = new Setting("SimpleLine", this, "Hand");

    private Setting strongLine = new Setting("StrongLine", this, "Strong");
    public Setting ignoreEating = new Setting("IgnoreEating", this, true);

    public Setting strongMode = new Setting("StrongMode", this, StrongMode.Blockhit1);

    public Setting ifKillAura = new Setting("If KillAura", this, true);

    private ArrayList<String> swingMode = new ArrayList<>(Arrays.asList("1", "2", "3"));

    private String swingModeString;

    public SwingAnimation() {
        super("SwingAnimation", "SwingAnimation", Category.RENDER);

        instance = this;

        setmgr.rSetting(mode);

        setmgr.rSetting(simpleLine);
        Kisman.instance.settingsManager.rSetting(new Setting("SwingMode", this, "1", swingMode));

        setmgr.rSetting(strongLine);
        setmgr.rSetting(strongMode);
        setmgr.rSetting(ignoreEating);
        setmgr.rSetting(ifKillAura);

        super.setDisplayInfo(() -> "[" + (mode.getValString().equalsIgnoreCase("Hand") ? Kisman.instance.settingsManager.getSettingByName(this, "SwingMode").getValString() : strongMode.getValString()) + "]");
    }

    public void update() {
        this.swingModeString = Kisman.instance.settingsManager.getSettingByName(this, "SwingMode").getValString();
    }

    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
        if(mode.getValString().equalsIgnoreCase("Hand")) {
            if (event.getSwingProgress() > 0) {
                final float angle = (1f - event.getSwingProgress()) * 360f;

                switch (swingModeString) {
                    case "1":
                        glRotatef(angle, 1, 0, 0);
                        break;
                    case "2":
                        glRotatef(angle, 0, 1, 0);
                        break;
                    case "3":
                        glRotatef(angle, 0, 0, 1);
                        break;
                }
            }
        }
    }

    public enum StrongMode {
        Blockhit1,
        Blockhit2,
        Knife
    }
}
