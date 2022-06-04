package com.kisman.cc.hud.hudmodule;

import com.kisman.cc.hud.hudmodule.combat.*;
import com.kisman.cc.hud.hudmodule.movement.Speed;
import com.kisman.cc.hud.hudmodule.player.*;
import com.kisman.cc.hud.hudmodule.render.*;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.*;

public class HudModuleManager {
    public java.util.ArrayList<HudModule> modules;
	
	public HudModuleManager() {
		modules = new java.util.ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		//combat
		modules.add(new ArmorHUD());
		modules.add(new CrystalPerSecond());
		modules.add(new PvpInfo());
		modules.add(new PvpResources());
		modules.add(new TargetHUD());

		//movement
		modules.add(new Speed());

		//render
        modules.add(ArrayListModule.instance);
		modules.add(new Coord());
		modules.add(new Fps());
		modules.add(new Logo());
		modules.add(new NotificationsModule());
		modules.add(new Radar());
		modules.add(new Welcomer());
		modules.add(new PacketChat());

		//player
		modules.add(new Indicators());
		modules.add(new Ping());
		modules.add(new ServerIp());
		modules.add(new Tps());
	}
	
	public HudModule getModule(String name) {
		for (HudModule m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}

	public java.util.ArrayList<HudModule> getModulesInCategory(HudCategory c) {
		java.util.ArrayList<HudModule> mods = new java.util.ArrayList<>();
		for (HudModule m : this.modules) if (m.getCategoryHud() == c) mods.add(m);
		return mods;
	}

	@SubscribeEvent public void onKey(InputEvent.KeyInputEvent event) {}
	@SubscribeEvent public void onTick(TickEvent.ClientTickEvent event) {for(HudModule m : modules) if(m.isToggled()) m.update();}
	@SubscribeEvent public void onRender(RenderGameOverlayEvent event) {for(HudModule m : modules) if(m.isToggled()) m.render();}
}
