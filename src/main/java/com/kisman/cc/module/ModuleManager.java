package com.kisman.cc.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.kisman.cc.module.Debug.HelloWorld;
import com.kisman.cc.module.chat.*;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.combat.autocrystal.AutoCrystal;
import com.kisman.cc.module.exploit.*;
import com.kisman.cc.module.misc.*;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.*;
import com.kisman.cc.module.render.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.*;

public class ModuleManager {
	public List<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		modules.clear();

//		Loader<Module> loader = new Loader<>();
//		loader.exclude("com.kisman.cc.module.Module");
//		loader.filter(Module::isToggled);
//		modules = loader.loadAllFromPackage("com.kisman.cc.module");

		//Debug
		modules.add(new HelloWorld());

		//combat
		modules.add(new AutoCrystal());
//		modules.add(new AimAssist());
		modules.add(new AimBot());
		modules.add(new AntiBot());
		modules.add(new AntiBow());
		modules.add(new AntiTrap());
		modules.add(new AutoArmor());
		modules.add(new AutoClicker());
		modules.add(new AutoFirework());
		//modules.add(new AutoObsidian());
		modules.add(new AutoPot());
		modules.add(new AutoRer());
		modules.add(new AutoTotem());
		modules.add(new AutoTrap());
		modules.add(new AntiTrapDoorBurrow());
		modules.add(new BowAimBot());
		modules.add(new BowSpam());
		modules.add(new BreakAlert());
		modules.add(new Criticals());
		modules.add(new CrystalFiller());
		modules.add(new HoleFiller());
		modules.add(new KillAura());
		modules.add(new NoFriendDamage());
		modules.add(new OffHand());
		modules.add(new SelfTrap());
		modules.add(new SilentXp());
		modules.add(new Surround());
//		modules.add(new TrapDoorBurrow());
		//client
		modules.add(new Cape());
		modules.add(new Changer());
		modules.add(new Config());
		modules.add(new CustomFontModule());
		modules.add(new CustomMainMenuModule());
		modules.add(new DiscordRPCModule());
		modules.add(new GuiModule());
		modules.add(new HUD());
		modules.add(new HudEditorTest());
		modules.add(new KotlinTest());
		modules.add(new NotEnoughCoinsModule());
//		modules.add(new ExampleModule());
//		modules.add(new Test());
		modules.add(new ToolTip());
		modules.add(new VegaGui());
		//chat
		modules.add(new AntiSpammer());
		modules.add(new AutoEZ());
		modules.add(new ChatModifier());
		modules.add(new Credits());
		modules.add(new Notification());
		modules.add(new Spammer());
		modules.add(new TotemPopCounter());
		modules.add(new TraceTeleport());
		//render
		modules.add(new Animation());
		modules.add(new BlockHighlight());
		//modules.add(new BlockLiner());
		modules.add(new BlockOutline());
		modules.add(new CameraClip());
		modules.add(new Charms());
		modules.add(new CityESP());
		modules.add(new ContainerModifier());
		modules.add(new CrystalModifier());
		modules.add(new CustomFog());
		modules.add(new DamageESP());
		modules.add(new EyeFinder());
		modules.add(new EntityESP());
		modules.add(new HoleESP());
		modules.add(new HotbarModifier());
		modules.add(new ItemESP());
		modules.add(new JumpCircle());
		modules.add(new LogoutSpots());
		modules.add(new NameTags());
		modules.add(new NameTagsRewrite());
		modules.add(new NoRender());
		modules.add(new Particle());
		modules.add(new PopCharms());
		modules.add(new PortalESP());
		modules.add(new RangeVisualisator());
		modules.add(new Reverse());
		modules.add(new SelfCityESP());
		modules.add(new ShaderCharms());
		modules.add(new SkyColor());
		modules.add(new SpawnsESP());
		modules.add(new Spin());
		modules.add(new StorageESP());
		modules.add(new SwingAnimation());
		modules.add(new Trails());
		modules.add(new Trajectories());
		modules.add(new TrajectoriesRewrite());
		modules.add(new ViewModel());
		//modules.add(new Weather());
		modules.add(new XRay());
		//movement
		modules.add(new AirJump());
		modules.add(new Anchor());
		modules.add(new AutoJump());
		modules.add(new AutoWalk());
		modules.add(new BoatFly());
		modules.add(new ElytraFly());
		modules.add(new FastSwim());
		modules.add(new Fly());
		modules.add(new HoleSnap());
		modules.add(new IceSpeed());
		modules.add(new Jesus());
		modules.add(new LongJump());
		modules.add(new NoFall());
		modules.add(new NoJumpDelay());
		modules.add(new NoRotate());
		modules.add(new NoSlow());
		modules.add(new NoSlowBypass());
		modules.add(new NoSlowSneak());
		modules.add(new NoStepDelay());
		modules.add(new NoWeb());
		modules.add(new Parkour());
		modules.add(new ReverseStep());
		modules.add(new Scaffold());
		modules.add(new Speed());
		modules.add(new Spider());
		modules.add(new Sprint());
		modules.add(new Step());
		modules.add(new TargetStrafe());
		//player
		modules.add(new ElytraEquip());
		modules.add(new FastBreak());
		modules.add(new FastLadder());
		modules.add(new FastPlace());
		modules.add(new ForgeBypass());
		modules.add(new FreeCamBypass());
		modules.add(new NoInteract());
		modules.add(new PacketCancel());
		modules.add(new PacketLogger());
		modules.add(new Reach());
		modules.add(new Refill());
		modules.add(new Replenish());
		modules.add(new RoofInteract());
		modules.add(new Swing());
		modules.add(new TeleportBack());
		modules.add(new Velocity());
		//exploit
		modules.add(new AntiLogger());
		modules.add(new AntiVanish());
		modules.add(new AutoKick());
		modules.add(new BowExploit());
		modules.add(new BowExploitRewrite());
		modules.add(new CactusLeave());
		modules.add(new Disabler());
		modules.add(new Ghost());
		modules.add(new KismansDupe());
		modules.add(new MiddleClick());
		modules.add(new MultiTask());
		modules.add(new NoClip());
		modules.add(new NoGlitchBlocks());
		modules.add(new NoMiningTrace());
		modules.add(new NoSwing());
		modules.add(new PacketEat());
		modules.add(new PacketMine());
		modules.add(new Rubberband());
		modules.add(new SilentClose());
		modules.add(new SoundCoordLogger());
		modules.add(new Teleport());
		modules.add(new TickShift());
		modules.add(new Timer());
		modules.add(new WaterLeave());
		modules.add(new WebLeave());
		//misc
		modules.add(new AntiRegear());
		modules.add(new AutoLog());
		modules.add(new BetterScreenshot());
		modules.add(new BurrowCounter());
		modules.add(new DDOSModule());
		modules.add(new FakePlayer());
		modules.add(new FreeLook());
		modules.add(new MurderFinder());
		modules.add(new NameProtect());
		modules.add(new Optimizer());
		modules.add(new PigPOV());
		modules.add(new SelfDamage());
		modules.add(new SkylightFix());
		modules.add(new TeamRusherLag());
		modules.add(new Tracker());
		modules.add(new VisualRange());
		modules.add(new WeaknessLog());
		modules.add(new WebhookSpammer());
		modules.add(new XCarry());
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}
	
	public List<Module> getModuleList() {
		return modules;
	}

	public ArrayList<Module> getModulesInCategory(Category c) {
		ArrayList<Module> mods = new ArrayList<>();
		for (Module m : this.modules) if (m.getCategory() == c) mods.add(m);
		return mods;
	}

	public ArrayList<Module> getEnabledModules() {
		ArrayList<Module> enabled = new ArrayList<>();
		modules.stream().filter(Module::isToggled).forEach(enabled::add);
		return enabled;
	}

	public ArrayList<Module> getSortModuleList(boolean reverse) {
		ArrayList<Module> sorted = new ArrayList<>();
		getEnabledModules().stream().filter(module -> module.visible)
				.sorted(Comparator.comparing(module -> CustomFontUtil.getStringWidth(module.getName() + " " + module.getDisplayInfo()) * (reverse ? -1 : 1)))
				.collect(Collectors.toList());

		return sorted;
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(Module m : modules) if(m.isToggled()) m.update();
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		for(Module m : modules) if(m.isToggled()) m.render();
	}

	public void key(char typedChar, int key, Module mod) {
		if(mod.isToggled()) {
			mod.key();
			mod.key(key);
			mod.key(typedChar, key);
		}
	}

	public String[] getCategories() {
		String[] cats = new String[Category.values().length];
		int i = 0;
		for(Category cat : Category.values()) {
			cats[i] = cat.name();
			i++;
		}
		return cats;
	}
}
