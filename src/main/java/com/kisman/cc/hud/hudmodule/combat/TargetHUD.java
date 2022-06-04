package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class TargetHUD extends HudModule {
    private EntityPlayer target = null;
    private final TimerUtils timer = new TimerUtils();
    private double hpBarWidth;
    private double cdBarWidth;
    private double[] circleProgressBarDegreeses = new double[] {0, 0, 0, 0, 0, 0, 0};
    private double borderOffset = 5;

    public TargetHUD() {
        super("TargetHud", HudCategory.COMBAT, true);

        setX(500);
        setY(300);
    }

    public void update() {
        target = AutoRer.currentTarget;
        if(target == null) target = KillAura.instance.target;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(target == null) return;

        switch (HUD.instance.thudTheme.getValString()) {
            case "Vega":
                drawVega();
                break;
            case "Rewrite":
                drawRewrite();
                break;
            case "Circle":
                drawCircle();
                break;
            case "NoRules":
                drawNoRules(getX(), getY(), 150, 45);
                break;
            case "Simple":
                drawSimple();
                break;
            case "Astolfo":
                drawAstolfo();
                break;
        }
    }

    private void drawAstolfo() {
        Color color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColorsToColorObj(100, 100) : new Color(255, 0, 89);

        float x = (float) getX(), y = (float) getY();
        setW(155);
        setH(60);
        double healthWid = (target.getHealth() / target.getMaxHealth() * 120);
        healthWid = MathHelper.clamp(healthWid, 0.0D, 120.0D);
        double check = target.getHealth() < 18 && target.getHealth() > 1 ? 8 : 0;
        hpBarWidth = AnimationUtils.animate(healthWid, hpBarWidth, 0.05);
        Render2DUtil.drawRectWH(x, y, 155, 60, new Color(20, 20, 20, 200).getRGB());
        mc.fontRenderer.drawStringWithShadow(target.getName(), x + 30, y + 4, color.getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 1);
        GlStateManager.scale(2.5f, 2.5f, 2.5f);
        GlStateManager.translate(-x - 3, -y - 2, 1);
        mc.fontRenderer.drawStringWithShadow(Math.round((target.getHealth() / 2.0f)) + " \u2764", x + 16, y + 10, color.getRGB());
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);

        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, target.getHeldItemOffhand(), (int) x + 137, (int) y + 7);
        mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), (int) x + 137, (int) y + 1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        try {GuiInventory.drawEntityOnScreen( (int) x + 16, (int) y + 55, 25, target.rotationYaw, -target.rotationPitch, target);} catch (Exception ignored) {}
        Render2DUtil.drawRectWH(x + 30, y + 48, 120, 8, color.darker().darker().darker().getRGB());
        Render2DUtil.drawRectWH(x + 30, y + 48, (float) (hpBarWidth + check), 8, color.darker().getRGB());
        Render2DUtil.drawRectWH(x + 30, y + 48, (float) healthWid, 8, color.getRGB());
    }

    private void drawSimple() {
        int x = (int) getX();
        int y = (int) getY();
        setW(25 + borderOffset * 2 + CustomFontUtil.getStringWidth(target.getName()));
        setH(25);
        int width = (int) (getW());
        int height = (int) getH();

        Render2DUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 170).getRGB());

        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(Objects.requireNonNull(mc.player.connection.getPlayerInfo(target.getName())).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            GL11.glPopMatrix();
        } catch (Exception e) {
            GL11.glPopMatrix();
        }

        CustomFontUtil.drawStringWithShadow(target.getName(), x + borderOffset + 25, y + borderOffset, -1);

        Render2DUtil.drawRectWH(x + borderOffset + 25, y + height - borderOffset - 7, (target.getHealth() / target.getMaxHealth()) * CustomFontUtil.getStringWidth(target.getName()), 7, -1);
    }
    
    private void drawCircle() {
        double x = getX();
        double y = getY();
        double radius = 12;
        setW(12 + borderOffset * 9 + radius * 7);
        setH(borderOffset * 4 + CustomFontUtil.getFontHeight() + 27);
        double width = getW();
        double height = getH();
        //img height: borderOffset * 3 + CustomFontUtil.getFontHeight() + 27
        //img width: borderOffset + 27

        //draw background
        Render2DUtil.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Render2DUtil.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //draw target's name
        CustomFontUtil.drawCenteredStringWithShadow(target.getName(), x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

        //draw face background
        Render2DUtil.drawRect(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), (x + borderOffset + radius), borderOffset * 3 + CustomFontUtil.getFontHeight() + radius, ColorUtils.astolfoColors(100, 100));

        //draw face texture
        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(mc.player.connection.getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (x + borderOffset + 1), (int) (y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 1), 8.0F, 8, 8, 8, 10, 10, 64.0F, 64.0F);
            GL11.glPopMatrix();
        } catch (Exception e) {
            GL11.glPopMatrix();
        }


        //draw health circle
        double healthX = x + borderOffset * 2 + 12 + radius, healthY = y + borderOffset * 3 + CustomFontUtil.getFontHeight() + radius, circleOffset = 3, healthDegrees = 360 * (target.getMaxHealth() / target.getHealth());
        Render2DUtil.drawProgressCircle2(healthX, healthY, radius, ColorUtils.astolfoColors(100, 100), healthDegrees, 1);
        double[] circleCentre = MathUtil.getCircleCentre(new double[] {healthX, healthY}, radius);
        String text = String.valueOf((int) target.getHealth());
        CustomFontUtil.drawCenteredStringWithShadow(text, circleCentre[0], circleCentre[1] - CustomFontUtil.getFontHeight() / 2, ColorUtils.astolfoColors(100, 100));

        //draw armor and items in hands
        double posX = healthX;
        for (final ItemStack item : target.getArmorInventoryList()) {
            if(item.isEmpty) continue;
            GL11.glPushMatrix();
            GL11.glTranslated(posX, healthY + circleOffset, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            RenderUtil.renderItemOverlayIntoGUI(CustomFontUtil.comfortaab18, item, 0, 0, null, false);
            GL11.glPopMatrix();
            double[] centre = MathUtil.getCircleCentre(new double[] {posX, healthY + circleOffset}, radius);
            Render2DUtil.drawProgressCircle2(centre[0], centre[1], radius, ColorUtils.astolfoColors(100, 100), InventoryUtil.getDamageInFloat(item), 1);
            posX += 16;
        }
        if(!target.getHeldItemMainhand().isEmpty) {
            GL11.glPushMatrix();
            GL11.glTranslated(posX, healthY + circleOffset, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            RenderUtil.renderItemOverlayIntoGUI(CustomFontUtil.comfortaab18, target.getHeldItemMainhand(), 0, 0, null, false);
            GL11.glPopMatrix();
            double[] centre = MathUtil.getCircleCentre(new double[] {posX, healthY + circleOffset}, radius);
            Render2DUtil.drawProgressCircle2(centre[0], centre[1], radius, ColorUtils.astolfoColors(100, 100), InventoryUtil.getDamageInFloat(target.getHeldItemMainhand()), 1);
            posX += 16;
        }
        if(!target.getHeldItemOffhand().isEmpty){
            GL11.glPushMatrix();
            GL11.glTranslated(posX, healthY + circleOffset, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            RenderUtil.renderItemOverlayIntoGUI(CustomFontUtil.comfortaab18, target.getHeldItemOffhand(), 0, 0, null, false);
            GL11.glPopMatrix();
            double[] centre = MathUtil.getCircleCentre(new double[] {posX, healthY + circleOffset}, radius);
            Render2DUtil.drawProgressCircle2(centre[0], centre[1], radius, ColorUtils.astolfoColors(100, 100), InventoryUtil.getDamageInFloat(target.getHeldItemOffhand()), 1);
        }
    }

    private void drawNoRules(double x, double y, double w, double h) {
        setW(w);
        setH(h);
        double healthOffset = ((target.getHealth() + target.getAbsorptionAmount()) - 0) / (target.getMaxHealth() - 0);
        hpBarWidth += (healthOffset - hpBarWidth) / 4;
        Render2DUtil.drawRoundedRect2(x, y, w, h, 6, new Colour(20, 20, 20, 210).getRGB());
        Render2DUtil.drawRoundedRect2(x + 2, y + (h / 2 - 34 / 2) - 3, 40, 40, 6, 0x40575656);
        Render2DUtil.drawRoundedRect2(x + 45, y + 4, w - 49, 30, 6, 0x40575656);
        if (target != null) {
            CustomFontUtil.drawStringWithShadow("Name: " + ChatFormatting.GRAY + target.getName(), x + 47, y + 4, -1);
            CustomFontUtil.drawStringWithShadow("Distance: " + ChatFormatting.GRAY + MathUtil.round(mc.player.getDistance(target), 2), x + 47, y + 13, -1);
            CustomFontUtil.drawStringWithShadow("Ping: " + ChatFormatting.GRAY + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing()) + " ms", x + 47, y + 22.5, -1);
            Render2DUtil.drawRoundedRect2(x + 45, y + h - 16, w - 49, 10, 6, 0x40575656);
            Render2DUtil.drawRoundedRect2(x + 47, y + h - 12, 95 * hpBarWidth, 3, 4, ColorUtils.healthColor(target.getHealth() + target.getAbsorptionAmount(), target.getMaxHealth()).getRGB());
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            try {GuiInventory.drawEntityOnScreen((int) x + 21, (int) (y + 44), 18, -30, -target.rotationPitch, target);} catch (Exception ignored) {}
        }
    }

    private void drawRewrite() {
        double x = getX();
        double y = getY();
        setW(120);
        double width = getW();
        double maxSlidersWidth = width - borderOffset * 2;
        double offset = 4 + CustomFontUtil.getFontHeight() * 2;
        setH(borderOffset * 4 + CustomFontUtil.getFontHeight() + offset * 2 + 12 + 27);
        double height = getH();

        int count = 0;

        //draw background
        Render2DUtil.drawRect(x + 3, y + 3, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x + 2, y + 2, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x + 1, y + 1, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x + 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 3, y - 8, x + width + 3, y + height - 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 3, y, x + width + 3, y + height, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 7, x + width + 2, y + height - 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 2, y, x + width + 2, y + height, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 6, x + width + 1, y + height - 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x - 1, y, x + width + 1, y + height, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 5, x + width, y + height, (ColorUtils.astolfoColors(100, 100)));
        Render2DUtil.drawRect(x - 3, y - 1, x + width + 3, y + height + 3, (ColorUtils.getColor(33, 33, 42)));
        Render2DUtil.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, (ColorUtils.getColor(45, 45, 55)));
        Render2DUtil.drawRect(x - 1, y - 3, x + width + 1, y + height + 1, (ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(x, y - 4, x + width, y + height, (ColorUtils.getColor(34, 34, 40)));

        //draw target's name
        CustomFontUtil.drawCenteredStringWithShadow(target.getName(), x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

        //draw face background
        Render2DUtil.drawRect(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), (x + borderOffset + 27), y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27, ColorUtils.astolfoColors(100, 100));

        //draw face texture
        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (x + borderOffset + 1), (int) (y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 1), 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
            GL11.glPopMatrix();
        } catch (Exception e){
            GL11.glPopMatrix();
        }

        //draw health & dist & onGround
        CustomFontUtil.drawString("Health: " + (int) target.getHealth(), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight(), ColorUtils.astolfoColors(100, 100));
        CustomFontUtil.drawString("Distance: " + (int) mc.player.getDistance(target), x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 2, ColorUtils.astolfoColors(100, 100));
        CustomFontUtil.drawString("On Ground: " + target.onGround, x + borderOffset + 27 + 4, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 3 + 4, ColorUtils.astolfoColors(100, 100));

        //draw armor and items in hands
        double posX = x + borderOffset;
        for (final ItemStack item : target.getArmorInventoryList()) {
            if(item.isEmpty) continue;
            GL11.glPushMatrix();
            GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
            GL11.glPopMatrix();
            posX += 12;
        }
        if(!target.getHeldItemMainhand().isEmpty) {
            GL11.glPushMatrix();
            GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(target.getHeldItemMainhand(), 0, 0);
            GL11.glPopMatrix();
            posX += 12;
        }
        if(!target.getHeldItemOffhand().isEmpty){
            GL11.glPushMatrix();
            GL11.glTranslated(posX, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), 0, 0);
            GL11.glPopMatrix();
        }

        //draw cooldown slider
        double cooldownPercentage = MathHelper.clamp(target.getCooledAttackStrength(0), 0.1, 1);
        cdBarWidth = AnimationUtils.animate(cooldownPercentage * maxSlidersWidth, cdBarWidth, 0.05);
        CustomFontUtil.drawStringWithShadow("Cooldown", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 4 + 12, ColorUtils.astolfoColors(100, 100));
        drawSlider(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 27 + 6 + 12, cdBarWidth, CustomFontUtil.getFontHeight());
        count++;

        //draw health slider
        if(timer.passedMillis(15)) {
            hpBarWidth = AnimationUtils.animate((target.getHealth() / target.getMaxHealth()) * maxSlidersWidth, hpBarWidth, 0.05);
            timer.reset();
        }
        CustomFontUtil.drawStringWithShadow("Health", x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() + 27 + 4 + (count * offset) + 12, ColorUtils.astolfoColors(100, 100));
        drawSlider(x + borderOffset, y + borderOffset * 3 + CustomFontUtil.getFontHeight() * 2 + 27 + 6 + (count * offset) + 12, hpBarWidth, CustomFontUtil.getFontHeight());
    }

    private void drawSlider(double x, double y, double sliderWidth, double sliderHeight) {
        if(HUD.instance.thudShadowSliders.getValBoolean()) Render2DUtil.drawShadowSliders(x, y, sliderWidth, sliderHeight, ColorUtils.astolfoColors(100, 100), 1);
        else Render2DUtil.drawRect(x, y, x + sliderWidth, y + sliderHeight, ColorUtils.astolfoColors(100, 100));
    }

    private void drawVega() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        double renderX = getX();
        double renderY = getY();
        float maxX = Math.max(40, CustomFontUtil.getStringWidth("HP: " + (int) target.getHealth() + " | Dist: " + mc.player.getDistance(target)) + 70);
        setW(maxX);
        setH(49);
        if(timer.passedMillis(15)) {
            hpBarWidth = AnimationUtils.animate((target.getHealth() / target.getMaxHealth()) * maxX, hpBarWidth, 0.05);
            timer.reset();
        }
        int color  = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : ColorUtils.rainbow(1, 1);
        Render2DUtil.drawRect(renderX - 4, renderY - 3, (renderX + 4 + maxX), (int) renderY + 49, ColorUtils.getColor(55, 55, 63));
        Render2DUtil.drawRect(renderX - 3, renderY - 2, (renderX + 3 + maxX), (int) renderY + 48, ColorUtils.getColor(95, 95, 103));
        Render2DUtil.drawRect(renderX - 2, renderY - 1, (renderX + 2 + maxX), (int) renderY + 47, ColorUtils.getColor(65, 65, 73));
        Render2DUtil.drawRect(renderX - 1, renderY, (renderX + 1 + maxX), (int) renderY + 46, ColorUtils.getColor(25, 25, 33));
        Render2DUtil.drawRect(renderX + 2, renderY + 42, (renderX + maxX), (int) renderY + 45, ColorUtils.getColor(48, 48, 58));
        Render2DUtil.drawRect(renderX + 1, renderY + 2, (renderX + 28), (int) renderY + 29, color);
        Render2DUtil.drawRect(renderX + 2, renderY + 3, (int) (renderX + 27), (int) renderY + 28, ColorUtils.getColor(25, 25, 33));
        Gui.drawRect((int) renderX, (int) renderY + 37 + 5, (int) (renderX + hpBarWidth), (int) renderY + 40 + 5, color);
        Gui.drawRect((int) renderX + 1, (int) renderY + 38 + 5, (int) (renderX - 1 + hpBarWidth), (int) renderY + 39 + 5, ColorUtils.getColor(0, 0, 0));

        try {
            mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(target.getName()).getLocationSkin());
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) (renderX + 2), (int) (renderY + 3), 8.0F, 8, 8, 8, 25, 25, 64.0F, 64.0F);
        } catch (Exception ignored) {}
        CustomFontUtil.drawString("HP: " + (int) target.getHealth() + " | Dist: " + mc.player.getDistance(target), renderX + 1 + 27 + 5, renderY + 2, -1);
        CustomFontUtil.drawString(target.getName(), renderX + 1 + 27 + 5, renderY + 4 + CustomFontUtil.getFontHeight(), -1);
        int posX = scaledResolution.getScaledWidth() / 2 + 53;
        for (final ItemStack item : target.getArmorInventoryList()) {
            if(item.isEmpty) continue;
            GL11.glPushMatrix();
            GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(item, 0, 0);
            GL11.glPopMatrix();
            posX += 12;
        }
        if(!target.getHeldItemMainhand().isEmpty) {
            GL11.glPushMatrix();
            GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(target.getHeldItemMainhand(), 0, 0);
            GL11.glPopMatrix();
            posX += 12;
        }
        if(!target.getHeldItemOffhand().isEmpty){
            GL11.glPushMatrix();
            GL11.glTranslated(posX - 27, renderY + 29 + 0.5, 0);
            GL11.glScaled(0.8, 0.8, 0.8);
            mc.getRenderItem().renderItemIntoGUI(target.getHeldItemOffhand(), 0, 0);
            GL11.glPopMatrix();
        }
    }
}
