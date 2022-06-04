package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.hud.hudmodule.render.packetchat.Log;
import com.kisman.cc.hud.hudmodule.render.packetchat.Message;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class PacketChat extends HudModule {

    public static PacketChat Instance;

    private int width = 200;

    private final String header = "PacketChat";
    private double borderOffset = 5;

    public Log logs = new Log();

    public PacketChat() {
        super("PacketChat", "", HudCategory.PLAYER);
        logs.ActiveMessages.add(new Message("lol"));
        logs.ActiveMessages.add(new Message("lmao"));
        Instance = this;
    }

    public void update() {
        setX(3);
        setY(8);
        setW(width + 4);
        setH(borderOffset * 7 + CustomFontUtil.getFontHeight() * 5);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        drawRewrite();
    }

    private void drawRewrite() {

        if(logs.ActiveMessages.size()>=30) logs.ActiveMessages.clear();
        if(logs.PassiveMessages.size()>=30) logs.PassiveMessages.clear();

        scrollWheelCheck();

        double x = getX();
        double y = getY();
        double width = getW();
        double height = getH();
        double offset = CustomFontUtil.getFontHeight() + borderOffset;
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

        //draw header
        CustomFontUtil.drawCenteredStringWithShadow(header, x + width / 2, y + borderOffset, ColorUtils.astolfoColors(100, 100));

        int pcount = 0;

        //draws messages
        logs.Iterator = logs.ActiveMessages.iterator();
        while(logs.Iterator.hasNext())
        {
            Message message = logs.Iterator.next();
            pcount++;

            CustomFontUtil.drawStringWithShadow(message.message, x + borderOffset, y + CustomFontUtil.getFontHeight() + (offset * count), ColorUtils.astolfoColors(100, 100));
            count++;

            if(pcount>=height/borderOffset)
                up();

            if(logs.ActiveMessages.size()>=30) logs.ActiveMessages.clear();
            if(logs.PassiveMessages.size()>=30) logs.PassiveMessages.clear();

            logs.Iterator = logs.ActiveMessages.iterator();
        }
    }

    public void up()
    {
        if(!logs.ActiveMessages.isEmpty()) {
            logs.PassiveMessages.add(logs.ActiveMessages.get(0));
            logs.ActiveMessages.remove(0);
        }
    }

    public void down()
    {
        if(!logs.PassiveMessages.isEmpty())
        {
            logs.ActiveMessages.add(0, logs.PassiveMessages.get(logs.PassiveMessages.size()-1));
            logs.PassiveMessages.remove(logs.PassiveMessages.size()-1);
        }
    }

    public void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0) {
            up();
        } else if(dWheel > 0) {
            down();
        }
    }
}
