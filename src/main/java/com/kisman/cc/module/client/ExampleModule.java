package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventWorldRender;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.gui.ClickGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import com.kisman.cc.gui.*;

/*import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.text.ITextComponent;*/
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.Color;
import java.awt.event.MouseEvent;

//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExampleModule extends Module {
    private Setting exampleBind = new Setting("ExampleBind", this, Keyboard.KEY_NONE);

    private Setting exampleEntityPreview = new Setting("ExampleEntityPreview", this, "Example", new EntityEnderCrystal(mc.world));

    public ExampleModule() {
        super("ExampleModule", "example", Category.CLIENT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));

        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory", this, "ExampleCategory", true));
//        Kisman.instance.settingsManager.rSubSetting(new Setting(
//                "ExampleCLine",
//                this,
//                Kisman.instance.settingsManager.getSettingByName(this, "ExampleCategory"),
//                "ExampleLine"
//                )
//        );

        Kisman.instance.settingsManager.rSetting(new Setting("ExampleString", this, "kisman", "kisman", true));
//        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory", this, 1, "ExampleCategory"));
//        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCCheckBox", this, 1, "ExampleCCheckBox", false));
//        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory1", this, 2, "ExampleCategory1"));
//        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCLine1", this, "ExampleCLine1", 2));
//        Kisman.instance.settingsManager.rSetting(new Setting("ExampleColorPicker", this, "ExampleColorPicker", new float[] {3f, 0.03f, 0.33f, 1f}, false));
        //        Kisman.instance.settingsManager.rSetting(new Setting("ExampleSimpleColorPicker", this, "ExampleSimpleColorPicker", new float[] {3f, 0.03f, 0.33f, 1f}, true));
//        Kisman.instance.settingsManager.rSetting(new Setting(this));

        setmgr.rSetting(exampleBind);
    }

    // @SubscribeEvent
    // public void onRender(EventWorldRender event) {
    //     RenderUtil.drawLine(1, 1, 100, 100, 10f, 0xFFFFFF);
    // }


    public void onEnable() {
//        mc.displayGuiScreen(Kisman.instance.guiSvipix);
//        Kisman.EVENT_BUS.subscribe(playerMotionUpdateListener);

//        mc.player

/*        mc.displayGuiScreen(Kisman.instance.newGui);
        this.setToggled(false);*/

/*        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem().processPacket(new INetHandlerPlayServer() {

            @Override
            public void processTryUseItem(CPacketPlayerTryUseItem packetIn) {

            }
        }));*/
//        mc.displayGuiScreen(Kisman.instance.blockGui);
        // super.onEnable();
        // mc.displayGuiScreen(Kisman.instance.guiConsole);
        // this.setToggled(false);
        //XRayManager.add();
        //ClickGui.
        //this.subcomponents.add(new ColorPicker(s, this, opY, ));
        //ColorPicker colorPicker = new ColorPicker(5, 85 / 2, Color.WHITE, this::setColor);
        //colorPicker.draw(Mouse.getX(), Display.getHeight() - Mouse.getY());
        //ColorPicker colorPicker = new ColorPicker();
        //mc.displayGuiScreen(colorPicker);//Kisman.instance.colorPicker
    }

    public void onDisable() {
//        Kisman.EVENT_BUS.unsubscribe(playerMotionUpdateListener);
    }

    public void key(int key) {
        mc.player.sendChatMessage("Typed Key " + key + " | " + Keyboard.getKeyName(key));
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> playerMotionUpdateListener = new Listener<>(event -> {
        mc.player.sendChatMessage("event player motion update is done!");

        if(event.getEra() != Event.Era.PRE) {
            return;
        }

        mc.player.sendChatMessage("6666");
    });

    public void motion(EventPlayerMotionUpdate event) {
        mc.player.sendChatMessage(
                "?"
        );
    }

    public boolean isVisible() {return false;}
}
