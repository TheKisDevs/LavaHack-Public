package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Radar extends HudModule {
    private int x = 0, y = 0;
    private final String[] directions = new String[] {"X+", "Z+", "X-", "Z-"};

    public Radar() {
        super("Radar", HudCategory.RENDER, true);

        setX(0);
        setY(0);
        setW(150);
        setH(150);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        x = (int) getX();
        y = (int) getY();

        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;

        //draw background
        Gui.drawRect(x, y, 150 + x, 150 + y, new Colour(70, 70, 70, 150).getRGB());

        //draw crosshair
        Render2DUtil.drawRect(150 / 2 - 0.5 + x, 3 + CustomFontUtil.getFontHeight() + y, 150 / 2 + 0.5 + x, 150 - 3 - CustomFontUtil.getFontHeight() + y, new Colour(50, 50, 50, 165).getRGB());
        Render2DUtil.drawRect(3 + CustomFontUtil.getStringWidth(directions[3]) + x, 150 / 2 - 0.5 + y, 150 - 3 - CustomFontUtil.getStringWidth(directions[1]) + x, 150 / 2 + 0.5 + y, new Colour(50, 50, 50, 165).getRGB());

        //draw entities points
        for(Entity entity : mc.world.loadedEntityList) if(entity instanceof EntityPlayer) if(entity != mc.player) renderEntityPoint(entity);

        //draw facing's crosshair
        boolean isNorth = isFacing(EnumFacing.NORTH);//Z-
        boolean isSouth = isFacing(EnumFacing.SOUTH);//Z+
        boolean isEast = isFacing(EnumFacing.EAST);//X+
        boolean isWest = isFacing(EnumFacing.WEST);//X-

        if(isNorth) Render2DUtil.drawRect(150 / 2 - 0.5 - 6 + x, 150 / 2 - 0.5 + y, 150 / 2 - 0.5 + x, 150 / 2 + 0.5 + y, color);
        else if(isSouth) Render2DUtil.drawRect(150 / 2 + 0.5 + x, 150 / 2 - 0.5 + y, 150 / 2 + 0.5 + 6 + x, 150 / 2 - 0.5 + y, color);
        else if(isEast) Render2DUtil.drawRect(150 / 2 - 0.5 + x, 150 / 2 - 0.5 - 6 + y, 150 / 2 + 0.5 + x, 150 / 2 - 0.5 + y, color);
        else if(isWest) Render2DUtil.drawRect(150 / 2 - 0.5 + x, 150 / 2 + 0.5 + y, 150 / 2 + 0.5 + x, 150 / 2 + 0.5 + 6 + y, color);


        //draw direction's names
        CustomFontUtil.drawStringWithShadow(directions[0], 150 / 2 - (CustomFontUtil.getStringWidth(directions[0]) / 2) + x, 2 + y, color);
        CustomFontUtil.drawStringWithShadow(directions[2], 150 / 2 - (CustomFontUtil.getStringWidth(directions[2]) / 2) + x, 150 - 2 - CustomFontUtil.getFontHeight() + y, color);
        CustomFontUtil.drawStringWithShadow(directions[1], 2 + x, 150 / 2 - CustomFontUtil.getFontHeight() / 2 + y, color);
        CustomFontUtil.drawStringWithShadow(directions[3], 150 - 2 - CustomFontUtil.getStringWidth(directions[3]) + x, 150 / 2 - CustomFontUtil.getFontHeight() / 2 + y, color);

        //draw outline
        drawLine(x, y, 1 + x, 150 + y, color);
        drawLine(x, y, 150 + x, 1 + y, color);
        drawLine(149 + x, y, 150 + x, 150 + y, color);
        drawLine(x, 149 + y, 150 + x, 150 + y, color);
    }

    private boolean isFacing(EnumFacing enumFacing) {
        return mc.player.getHorizontalFacing().equals(enumFacing);
    }

    private void renderEntityPoint(Entity entity) {
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
        int distanceX = findDistance1D(mc.player.posX, entity.posX);
        int distanceY = findDistance1D(mc.player.posZ, entity.posZ);
        int maxRange = HUD.instance.radarDist.getValInt();

        if (distanceX > maxRange || distanceY > maxRange || distanceX < -maxRange || distanceY < -maxRange) return;

        //draw entity's crosshair
        Render2DUtil.drawRect(150 / 2 + distanceX + x - 0.5, 150 / 2 + distanceY + y - 1.5, 150 / 2 + distanceX + x + 0.5, 150 / 2 + distanceY + y + 1.5, color);
        Render2DUtil.drawRect(150 / 2 + distanceX + x - 1.5, 150 / 2 + distanceY + y - 0.5, 150 / 2 + distanceX + x + 1.5, 150 / 2 + distanceY + y + 0.5, color);
    }

    private void drawLine(int x, int y, int x1, int y1, int color) {Gui.drawRect(x, y, x1, y1, color);}

    private int findDistance1D(double player, double entity) {
        double player1 = player;
        double entity1 = entity;

        if (player1 < 0) player1 = player1 * -1;
        if (entity1 < 0) entity1 = entity1 * -1;

        int value = (int) (entity1 - player1);

        if (player > 0 && entity < 0 || player < 0 && entity > 0) value = (int) ((-1 * player) + entity);
        if ((player > 0 || player < 0) && entity < 0 && entity1 != player1) value = (int) ((-1 * player) + entity);
        if ((player < 0 && entity == 0) || (player == 0 && entity < 0)) value = (int) (-1 * (entity1 - player1));

        return value;
    }
}
