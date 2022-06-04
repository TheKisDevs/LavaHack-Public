package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.file.*;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Config extends Module {
    public static Config instance;

    public Setting astolfoColorMode = new Setting("Astolfo Color Mode", this, AstolfoColorMode.Old);
    public Setting friends = new Setting("Friends", this, true);
    public Setting nameMode = new Setting("Name Mode", this, NameMode.kismancc);
    public Setting customName = new Setting("Custom Name", this, "kisman.cc", "kisman.cc", true).setVisible(() -> nameMode.getValBoolean());
    public Setting scrollSpeed = new Setting("Scroll Speed", this, 15, 0, 100, Slider.NumberType.PERCENT);
    public Setting horizontalScroll = new Setting("Horizontal Scroll", this, false);
    public Setting keyForHorizontalScroll = new Setting("Key for Horizontal Scroll", this, Keyboard.KEY_LSHIFT).setVisible(() -> horizontalScroll.getValBoolean());
    public Setting guiGlow = new Setting("Gui Glow", this, false);
    public Setting glowOffset = new Setting("Glow Offset", this, 6, 1, 20, true).setVisible(() -> guiGlow.getValBoolean());
    public Setting glowRadius = new Setting("Glow Radius", this, 15, 0, 20, true).setVisible(() -> guiGlow.getValBoolean());
    public Setting glowBoxSize = new Setting("Glow Box Size", this, 0, 0, 20, true).setVisible(() -> guiGlow.getValBoolean());
    public Setting guiGradient = new Setting("Gui Gradient", this, HUD.Gradient.None);
    public Setting guiGradientDiff = new Setting("Gui Gradient Diff", this, 1, 0, 1000, Slider.NumberType.TIME);
    public Setting guiDesc = new Setting("Gui Desc", this, false);
    public Setting guiParticles = new Setting("Gui Particles", this, true);
    public Setting guiOutline = new Setting("Gui Outline", this, true);
    public Setting guiAstolfo = new Setting("Gui Astolfo", this, false);
    public Setting guiRenderSize = new Setting("Gui Render Size", this, false);
    public Setting guiBetterCheckBox = new Setting("Gui Better CheckBox", this, false);
    public Setting guiBlur = new Setting("Gui Blur", this, true);
    public Setting guiVisualPreview = new Setting("Gui Visual Preview", this, false);
    public Setting guiShowBinds = new Setting("Gui Show Binds", this, false);
    public Setting pulseMin = new Setting("Pulse Min", this, 255, 0, 255, true);
    public Setting pulseMax = new Setting("Pulse Max", this, 110, 0, 255, true);
    public Setting pulseSpeed = new Setting("Pulse Speed", this, 1.5, 0.1, 10, false);
    public Setting saveConfig = new Setting("Save Config", this, false);
    public Setting loadConfig = new Setting("Load Config", this, false);
    public Setting configurate = new Setting("Configurate", this, true);
    public Setting particlesColor = new Setting("Particles Color", this, "Particles Dots Color", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean());

    public Setting particlesRenderLine = new Setting("Particles Render Lines", this, true);

    public Setting particlesGradientMode = new Setting("Particles Gradient Mode", this, ParticlesGradientMode.None).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean());

    public Setting particlesGStartColor = new Setting("Particles Gradient StartColor", this, "Particles Gradient StartColor", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean());
    public Setting particlesGEndColor = new Setting("Particles Gradient EndColor", this, "Particles Gradient EndColor", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean());

    public Setting particlesWidth = new Setting("Particles Width", this, 0.5, 0.0, 5, false).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean());

    public Setting particleTest = new Setting("Particle Test", this, true).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean());

    public Setting slowRender = new Setting("Slow Render", this, false);
    public Setting depthFix = new Setting("Depth Fix", this, true);
    public Setting antiOpenGLCrash = new Setting("Anti OpenGL Crash", this, false);


    public Config() {
        super("Config", Category.CLIENT, false);

        instance = this;

        setmgr.rSetting(friends);
        setmgr.rSetting(nameMode);
        setmgr.rSetting(customName);
        setmgr.rSetting(scrollSpeed);
        setmgr.rSetting(horizontalScroll);
        setmgr.rSetting(keyForHorizontalScroll);
        setmgr.rSetting(guiGlow);
        setmgr.rSetting(glowOffset);
        setmgr.rSetting(glowRadius);
        setmgr.rSetting(glowBoxSize);
        setmgr.rSetting(guiGradient);
        setmgr.rSetting(guiGradientDiff);
        setmgr.rSetting(guiDesc);
        setmgr.rSetting(guiParticles);
        setmgr.rSetting(guiOutline);
        setmgr.rSetting(guiAstolfo);
        setmgr.rSetting(guiRenderSize);
        setmgr.rSetting(guiBetterCheckBox);
        setmgr.rSetting(guiBlur);
        setmgr.rSetting(guiVisualPreview);
        setmgr.rSetting(guiShowBinds);
        setmgr.rSetting(pulseMin);
        setmgr.rSetting(pulseMax);
        setmgr.rSetting(pulseSpeed);
        setmgr.rSetting(saveConfig);
        setmgr.rSetting(loadConfig);
        setmgr.rSetting(configurate);
        setmgr.rSetting(particlesColor);
        setmgr.rSetting(particlesGradientMode);
        setmgr.rSetting(particlesGStartColor);
        setmgr.rSetting(particlesGEndColor);
        setmgr.rSetting(particlesWidth);
        setmgr.rSetting(particleTest);
        setmgr.rSetting(slowRender);
        setmgr.rSetting(depthFix);
        setmgr.rSetting(antiOpenGLCrash);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if(saveConfig.getValBoolean()) {
            try {
                Kisman.instance.configManager.getSaver().init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveConfig.setValBoolean(false);
            if(mc.player != null && mc.world != null) ChatUtils.complete("Config saved");
        }

        if(loadConfig.getValBoolean()) {
            LoadConfig.init();
            try {
                Kisman.instance.configManager.getLoader().init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig.setValBoolean(false);
            if(mc.player != null && mc.world != null) ChatUtils.complete("Config loaded");
        }
    }

    public enum NameMode {kismancc, LavaHack, TheKisDevs, kidman, TheClient, BloomWare, custom}
    public enum ParticlesGradientMode {None, TwoGradient, ThreeGradient, Syns}
    public enum AstolfoColorMode {Old, Impr}
}
