package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CustomHotbar extends Module {
    public static int hotbarX, hotbarY;
    public static CustomHotbar instance;
    private int x = 3, y, width = 75, height = 70;
    private final ScaledResolution sr = new ScaledResolution(mc);

    private Setting yPos = new Setting("Y Pos", this, sr.getScaledHeight() - 100, 0, sr.getScaledHeight(), true);

    public CustomHotbar() {
        super("CustomHotbar", Category.RENDER);

        instance = this;

        setmgr.rSetting(yPos);

        y = yPos.getValInt();
    }

    public void update() {
        y = yPos.getValInt();
        hotbarX = x * 2 + width;
        hotbarY = y + (16 + height) / 2 - 182 / 2;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
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

        int offset = (75 - 25) / 2;
        int allHeight = height + 8 * 2;

        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(mc.getConnection().getPlayerInfo(mc.player.getName()).getLocationSkin());
        GL11.glColor4f(1, 1, 1, 1);
        Gui.drawScaledCustomSizeModalRect(x + offset, y - 5 + offset, 8, 8, 8, 8, 25, 25, 64, 64);
        GL11.glPopMatrix();
    }
}
