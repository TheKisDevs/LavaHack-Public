package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Comparator;

public class ArrayListModule extends HudModule {
    public static ArrayListModule instance = new ArrayListModule();

    public ArrayListModule() {
        super("ArrayList", "arrList", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        java.util.ArrayList<Module> mods = new java.util.ArrayList<>();
        ScaledResolution sr = new ScaledResolution(mc);

        for(Module mod : Kisman.instance.moduleManager.modules) if(mod != null && mod.isToggled() && mod.visible) mods.add(mod);

        Comparator<Module> comparator = (first, second) -> {
            String firstName = first.getName() + (first.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + first.getDisplayInfo());
            String secondName = second.getName() + (second.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + second.getDisplayInfo());
            float dif = CustomFontUtil.getStringWidth(secondName) - CustomFontUtil.getStringWidth(firstName);
            return (dif != 0) ? ((int) dif) : secondName.compareTo(firstName);
        };

        mods.sort(comparator);

        int count = 0;
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : HUD.instance.arrColor.getColour().getRGB();
        double heigth = CustomFontUtil.getFontHeight() + HUD.instance.offsets.getValDouble() + 1;
        float[] hsb = Color.RGBtoHSB(ColorUtils.getRed(color), ColorUtils.getGreen(color), ColorUtils.getBlue(color), null);

        for(int j = 0; j < mods.size(); j++) {
            Module mod = mods.get(j);
            if(mod != null && mod.isToggled() && mod.visible) {
                String name = mod.getName() + (mod.getDisplayInfo().equalsIgnoreCase("") ? "" : " " + TextFormatting.GRAY + mod.getDisplayInfo());

                switch (HUD.instance.arrGradient.getValString()) {
                    case "None": {
                        if(HUD.instance.background.getValBoolean()) {
                            double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                            drawBackground((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset, HUD.instance.arrY.getValDouble() + (heigth * count) - offset, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset, HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(HUD.instance.arrGlowBackground.getValBoolean()) {
                            double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1), HUD.instance.arrY.getValDouble() + (heigth * count), color);

                        if(HUD.instance.glow.getValBoolean()) {
                            double offset = HUD.instance.glowOffset.getValDouble() + HUD.instance.offsets.getValDouble() / 2;
                            if(HUD.instance.glowV2.getValBoolean()) {
                                double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(color, HUD.instance.glowAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count) - offset, ((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)) + CustomFontUtil.getStringWidth(name)), offset + HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(color, HUD.instance.glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Rainbow": {
                        if(HUD.instance.background.getValBoolean()) {
                            double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                            drawBackground((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset, HUD.instance.arrY.getValDouble() + (heigth * count) - offset, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset, HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(HUD.instance.arrGlowBackground.getValBoolean()) {
                            double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1), HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.rainbow(count * HUD.instance.arrGradientDiff.getValInt(), hsb[1], 1f), 255).getRGB());

                        if(HUD.instance.glow.getValBoolean()) {
                            double offset = HUD.instance.glowOffset.getValDouble() + HUD.instance.offsets.getValDouble() / 2;
                            if(HUD.instance.glowV2.getValBoolean()) {
                                double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.rainbow(count * HUD.instance.arrGradientDiff.getValInt(), hsb[1], 1f), HUD.instance.glowAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count) - offset, ((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)) + CustomFontUtil.getStringWidth(name)), offset + HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.rainbow(count * HUD.instance.arrGradientDiff.getValInt(), hsb[1], 1f), HUD.instance.glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Astolfo": {
                        if(HUD.instance.background.getValBoolean()) {
                            double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                            drawBackground((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset, HUD.instance.arrY.getValDouble() + (heigth * count) - offset, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset, HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(HUD.instance.arrGlowBackground.getValBoolean()) {
                            double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1), HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * HUD.instance.arrGradientDiff.getValInt()), 255).getRGB());

                        if(HUD.instance.glow.getValBoolean()) {
                            double offset = HUD.instance.glowOffset.getValDouble() + HUD.instance.offsets.getValDouble() / 2;
                            if(HUD.instance.glowV2.getValBoolean()) {
                                double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * HUD.instance.arrGradientDiff.getValInt()), HUD.instance.glowAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count) - offset, ((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)) + CustomFontUtil.getStringWidth(name)), offset + HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.getAstolfoRainbow(count * HUD.instance.arrGradientDiff.getValInt()), HUD.instance.glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Pulsive": {
                        if(HUD.instance.background.getValBoolean()) {
                            double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                            drawBackground((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset, HUD.instance.arrY.getValDouble() + (heigth * count) - offset, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset, HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        if(HUD.instance.arrGlowBackground.getValBoolean()) {
                            double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                            Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                        }

                        CustomFontUtil.drawStringWithShadow(name, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1), HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(HUD.instance.arrColor.getColour(), HUD.instance.arrColor.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * HUD.instance.arrGradientDiff.getValFloat()) / 60.0).getColor(), 255).getRGB());

                        if(HUD.instance.glow.getValBoolean()) {
                            double offset = HUD.instance.glowOffset.getValDouble() + HUD.instance.offsets.getValDouble() / 2;
                            if(HUD.instance.glowV2.getValBoolean()) {
                                double offset1 = HUD.instance.offsets.getValDouble() / 2 + 1;
                                Render2DUtil.drawRoundedRect((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name) - 1) - offset1 - HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) - offset1 - HUD.instance.glowRadius.getValInt(), (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset1 + HUD.instance.glowRadius.getValInt(), HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset1 - 1 + HUD.instance.glowRadius.getValInt(), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(HUD.instance.arrColor.getColour(), HUD.instance.arrColor.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * HUD.instance.arrGradientDiff.getValFloat()) / 60.0).getColor(), HUD.instance.glowAlpha.getValInt()).getRGB(), HUD.instance.glowOffset.getValInt());
                            } else Render2DUtil.drawGlow((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)), HUD.instance.arrY.getValDouble() + (heigth * count) - offset, ((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)) + CustomFontUtil.getStringWidth(name)), offset + HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight(), ColorUtils.injectAlpha(ColorUtils.twoColorEffect(HUD.instance.arrColor.getColour(), HUD.instance.arrColor.getColour().setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * HUD.instance.arrGradientDiff.getValFloat()) / 60.0).getColor(), HUD.instance.glowAlpha.getValInt()).getRGB());
                        }
                        break;
                    }
                    case "Sideway": {
                        int update = (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name));

                        if(HUD.instance.background.getValBoolean()) {
                            double offset = HUD.instance.offsets.getValDouble() / 2 + 1;
                            drawBackground((HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 : sr.getScaledWidth() - CustomFontUtil.getStringWidth(name)) - offset, HUD.instance.arrY.getValDouble() + (heigth * count) - offset, (HUD.instance.arrMode.getValString().equalsIgnoreCase("LEFT") ? 1 + CustomFontUtil.getStringWidth(name) : sr.getScaledWidth()) + offset, HUD.instance.arrY.getValDouble() + (heigth * count) + CustomFontUtil.getFontHeight() + offset - 1);
                        }

                        for(int i = 0; i < mod.getName().length(); ++i) {
                            String str = String.valueOf(mod.getName().charAt(i));

                            CustomFontUtil.drawStringWithShadow(str, update, HUD.instance.arrY.getValDouble() + (heigth * count), ColorUtils.injectAlpha(ColorUtils.rainbow(i * HUD.instance.arrGradientDiff.getValInt(), hsb[1], 1f), 255).getRGB());

                            update += CustomFontUtil.getStringWidth(str);
                        }
                        break;
                    }
                }

                count++;
            }
        }
    }

    private void drawBackground(double x, double y, double x1, double y1) {Render2DUtil.drawRect(x, y, x1, y1, ColorUtils.injectAlpha(Color.BLACK, HUD.instance.bgAlpha.getValInt()).getRGB());}
}
