package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.ai.autorer.AutoRerAI;
import com.kisman.cc.event.events.*;
import com.kisman.cc.event.events.lua.EventRender3D;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.combat.autorer.*;
import com.kisman.cc.module.combat.autorer.render.AutoRerRenderer;
import com.kisman.cc.module.render.shader.FramebufferShader;
import com.kisman.cc.module.render.shader.shaders.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.RenderingRewritePattern;
import com.kisman.cc.util.*;
import com.kisman.cc.util.bypasses.SilentSwitchBypass;
import com.kisman.cc.util.enums.ShaderModes;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author _kisman_(Logic, Renderer logic), Cubic(Renderer)
 */
public class AutoRer extends Module {
    public final Setting lagProtect = new Setting("Lag Protect", this, false);
    public final Setting placeRange = new Setting("Place Range", this, 6, 0, 6, false);
    private final Setting recalcPlaceRange = new Setting("Recalc Place Range", this, 4, 0, 6, false);
    private final Setting placeWallRange = new Setting("Place Wall Range", this, 4.5f, 0, 6, false);
    private final Setting breakRange = new Setting("Break Range", this, 6, 0, 6, false);
    private final Setting breakWallRange = new Setting("Break Wall Range", this, 4.5f, 0, 6, false);
    private final Setting targetRange = new Setting("Target Range", this, 9, 0, 20, false);
    private final Setting logic = new Setting("Logic", this, LogicMode.PlaceBreak);
    public final Setting terrain = new Setting("Terrain", this, false);
    private final Setting switch_ = new Setting("Switch", this, SwitchMode.None);
    private final Setting fastCalc = new Setting("Fast Calc", this, true);
    private final Setting recalc = new Setting("ReCalc", this, false);
    private final Setting motionCrystal = new Setting("Motion Crystal", this, false);
    private final Setting motionCalc = new Setting("Motion Calc", this, false).setVisible(motionCrystal::getValBoolean);
    private final Setting swing = new Setting("Swing", this, SwingMode.PacketSwing);
    private final Setting instant = new Setting("Instant", this, true);
    private final Setting instantCalc = new Setting("Instant Calc", this, true).setVisible(instant::getValBoolean);
    private final Setting instantRotate = new Setting("Instant Rotate", this, true).setVisible(instant::getValBoolean);
    private final Setting inhibit = new Setting("Inhibit", this, true);
    private final Setting sound = new Setting("Sound", this, true);
    public final Setting syns = new Setting("Syns", this, true);
    private final Setting rotate = new Setting("Rotate", this, Rotate.Place);
    private final Setting rotateMode = new Setting("Rotate Mode", this, RotateMode.Silent).setVisible(() -> !rotate.checkValString("None"));
    private final Setting ai = new Setting("AI", this, false);
    private final Setting calcDistSort = new Setting("Calc Dist Sort", this, false);

    private final Setting placeLine = new Setting("PlaceLine", this, "Place");
    private final Setting place = new Setting("Place", this, true);
    public final Setting secondCheck = new Setting("Second Check", this, false);
    private final Setting thirdCheck = new Setting("Third Check", this, false);
    public final Setting armorBreaker = new Setting("Armor Breaker", this, 100, 0, 100, Slider.NumberType.PERCENT);
    private final Setting multiPlace = new Setting("Multi Place", this, false);
    private final Setting firePlace = new Setting("Fire Place", this, false);

    private final Setting breakLine = new Setting("BreakLine", this, "Break");
    private final Setting break_ = new Setting("Break", this, true);
    private final Setting breakPriority = new Setting("Break Priority", this, BreakPriority.Damage);
    private final Setting friend_ = new Setting("Friend", this, FriendMode.AntiTotemPop);
    private final Setting clientSide = new Setting("Client Side", this, false);
    private final Setting manualBreaker = new Setting("Manual Breaker", this, false);
    private final Setting removeAfterAttack = new Setting("Remove After Attack", this, false);
    private final Setting antiCevBreakerMode = new Setting("Anti Cev Breaker", this, AntiCevBreakerMode.None);

    private final Setting delayLine = new Setting("DelayLine", this, "Delay");
    private final Setting placeDelay = new Setting("Place Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting breakDelay = new Setting("Break Delay", this, 0, 0, 2000, Slider.NumberType.TIME);
    private final Setting calcDelay = new Setting("Calc Delay", this, 0, 0, 20000, Slider.NumberType.TIME);
    private final Setting clearDelay = new Setting("Clear Delay", this, 500, 0, 2000, Slider.NumberType.TIME);
    private final Setting multiplication = new Setting("Multiplication", this, 1, 1, 10, true);

    private final Setting dmgLine = new Setting("DMGLine", this, "Damage");
    public final Setting minDMG = new Setting("Min DMG", this, 6, 0, 37, true);
    public final Setting maxSelfDMG = new Setting("Max Self DMG", this, 18, 0, 37, true);
    private final Setting maxFriendDMG = new Setting("Max Friend DMG", this, 10, 0, 37, true);
    public final Setting lethalMult = new Setting("Lethal Mult", this, 0, 0, 6, false);

    private final Setting threadLine = new Setting("ThreadLine", this, "Thread");
    private final Setting threadMode = new Setting("Thread Mode", this, ThreadMode.None);
    private final Setting threadDelay = new Setting("Thread Delay", this, 50, 1, 1000, Slider.NumberType.TIME).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadSyns = new Setting("Thread Syns", this, true).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadSynsValue = new Setting("Thread Syns Value", this, 1000, 1, 10000, Slider.NumberType.TIME).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()));
    private final Setting threadPacketRots = new Setting("Thread Packet Rots", this, false).setVisible(() -> !threadMode.getValString().equalsIgnoreCase(ThreadMode.None.name()) && !rotate.checkValString(Rotate.Off.name()));
    private final Setting threadSoundPlayer = new Setting("Thread Sound Player", this, 6, 0, 12, true).setVisible(() -> threadMode.checkValString("Sound"));
    private final Setting threadCalc = new Setting("Thread Calc", this, true).setVisible(() -> !threadMode.checkValString("None"));

    private final Setting renderLine = new Setting("RenderLine", this, "Render");
    private final Setting render = new Setting("Render", this, true);
    private final Setting movingLength = new Setting("Moving Length", this, 400, 0, 1000, true).setVisible(render::getValBoolean);
    private final Setting fadeLength = new Setting("Fade Length", this, 200, 0, 1000, true).setVisible(render::getValBoolean);

    private final Setting text = new Setting("Text", this, true);

    private final Setting targetCharmsLine = new Setting("Target Charms", this, "Target Shader Charms");
    private final Setting targetCharms = new Setting("Target Charms", this, false);
    private final Setting targetCharmsShader = new Setting("TC Shader", this, ShaderModes.SMOKE);

    private final Setting targetCharmsAnimationSpeed = new Setting("Animation Speed", this, 0, 1, 10, true).setVisible(() -> !targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());

    private final Setting targetCharmsBlur = new Setting("Blur", this, true).setVisible(() -> targetCharmsShader.checkValString("ITEMGLOW") && targetCharms.getValBoolean());
    private final Setting targetCharmsRadius = new Setting("Radius", this, 2, 0.1f, 10, false).setVisible(() -> (targetCharmsShader.checkValString("ITEMGLOW") || targetCharmsShader.checkValString("GLOW") || targetCharmsShader.checkValString("OUTLINE") || targetCharmsShader.checkValString("GRADIENT")) && targetCharms.getValBoolean());
    private final Setting targetCharmsMix = new Setting("Mix", this, 1, 0, 1, false).setVisible(() -> targetCharmsShader.checkValString("ITEMGLOW") && targetCharms.getValBoolean());
    private final Setting targetCharmsColor = new Setting("TC Color", this, "TC Color", new Colour(255, 255, 255)).setVisible(() -> (targetCharmsShader.checkValString("ITEMGLOW") || targetCharmsShader.checkValString("GLOW") || targetCharmsShader.checkValString("OUTLINE")) && targetCharms.getValBoolean());

    private final Setting targetCharmsQuality = new Setting("Quality", this, 1, 0, 20, false).setVisible(() -> (targetCharmsShader.checkValString("GRADIENT") || targetCharmsShader.checkValString("ITEMGLOW") || targetCharmsShader.checkValString("GLOW") || targetCharmsShader.checkValString("OUTLINE")) && targetCharms.getValBoolean());
    private final Setting targetCharmsGradientAlpha = new Setting("Gradient Alpha", this, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsAlphaGradient = new Setting("Alpha Gradient Value", this, 255, 0, 255, true).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsDuplicateOutline = new Setting("Duplicate Outline", this, 1, 0, 20, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsMoreGradientOutline = new Setting("More Gradient", this, 1, 0, 10, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsCreepyOutline = new Setting("Creepy", this, 1, 0, 20, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsAlpha = new Setting("Alpha", this, 1, 0, 1, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsNumOctavesOutline = new Setting("Num Octaves", this, 5, 1, 30, true).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());
    private final Setting targetCharmsSpeedOutline = new Setting("Speed", this, 0.1, 0.001, 0.1, false).setVisible(() -> targetCharmsShader.checkValString("GRADIENT") && targetCharms.getValBoolean());

    public static AutoRer instance;

    public final List<PlaceInfo> placedList = new ArrayList<>();
    private final TimerUtils placeTimer = new TimerUtils();
    private final TimerUtils breakTimer = new TimerUtils();
    private final TimerUtils calcTimer = new TimerUtils();
    private final TimerUtils renderTimer = new TimerUtils();
    private final TimerUtils predictTimer = new TimerUtils();
    private final TimerUtils manualTimer = new TimerUtils();
    private final TimerUtils synsTimer = new TimerUtils();
    private ScheduledExecutorService executor;
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    public static EntityPlayer currentTarget;
    private Thread thread;
    public PlaceInfo placePos, renderPos;
    private Entity lastHitEntity = null;
    public boolean rotating;
    private String lastThreadMode = threadMode.getValString();
    private boolean subscribed = false;

    private final AutoRerRenderer renderer = new AutoRerRenderer();
    private RenderingRewritePattern renderer_;

    public AutoRer() {
        super("AutoRer", Category.COMBAT);

        instance = this;

        setmgr.rSetting(lagProtect);

        setmgr.rSetting(placeRange);
        setmgr.rSetting(recalcPlaceRange.setVisible(recalc::getValBoolean));
        setmgr.rSetting(placeWallRange);
        setmgr.rSetting(breakRange);
        setmgr.rSetting(breakWallRange);
        setmgr.rSetting(targetRange);
        setmgr.rSetting(logic);
        setmgr.rSetting(terrain);
        setmgr.rSetting(switch_);
        setmgr.rSetting(fastCalc);
        setmgr.rSetting(recalc);
        setmgr.rSetting(motionCrystal);
        setmgr.rSetting(motionCalc);
        setmgr.rSetting(swing);
        setmgr.rSetting(instant);
        setmgr.rSetting(instantCalc);
        setmgr.rSetting(instantRotate);
        setmgr.rSetting(inhibit);
        setmgr.rSetting(sound);
        setmgr.rSetting(syns);
        setmgr.rSetting(rotate);
        setmgr.rSetting(rotateMode);
//        setmgr.rSetting(ai);
        setmgr.rSetting(calcDistSort);

        setmgr.rSetting(placeLine);
        setmgr.rSetting(place);
        setmgr.rSetting(secondCheck.setVisible(place::getValBoolean));
        setmgr.rSetting(thirdCheck.setVisible(place::getValBoolean));
        setmgr.rSetting(armorBreaker.setVisible(place::getValBoolean));
        setmgr.rSetting(multiPlace.setVisible(place::getValBoolean));
        setmgr.rSetting(firePlace.setVisible(place::getValBoolean));

        setmgr.rSetting(breakLine);
        setmgr.rSetting(break_);
        setmgr.rSetting(breakPriority.setVisible(break_::getValBoolean));
        setmgr.rSetting(friend_.setVisible(break_::getValBoolean));
        setmgr.rSetting(clientSide.setVisible(break_::getValBoolean));
        setmgr.rSetting(manualBreaker.setVisible(break_::getValBoolean));
        setmgr.rSetting(removeAfterAttack.setVisible(break_::getValBoolean));
        setmgr.rSetting(antiCevBreakerMode.setVisible(break_::getValBoolean));

        setmgr.rSetting(delayLine);
        setmgr.rSetting(placeDelay.setVisible(place::getValBoolean));
        setmgr.rSetting(breakDelay.setVisible(break_::getValBoolean));
        setmgr.rSetting(calcDelay);
        setmgr.rSetting(clearDelay);
        setmgr.rSetting(multiplication);

        setmgr.rSetting(dmgLine);
        setmgr.rSetting(minDMG);
        setmgr.rSetting(maxSelfDMG);
        setmgr.rSetting(maxFriendDMG);
        setmgr.rSetting(lethalMult);

        setmgr.rSetting(threadLine);
        setmgr.rSetting(threadMode);
        setmgr.rSetting(threadDelay);
        setmgr.rSetting(threadSyns);
        setmgr.rSetting(threadSynsValue);
        setmgr.rSetting(threadPacketRots);
        setmgr.rSetting(threadSoundPlayer);
        setmgr.rSetting(threadCalc);

        setmgr.rSetting(renderLine);
        setmgr.rSetting(render);
        //New renderer
        renderer_ = new RenderingRewritePattern(this, render::getValBoolean);
        renderer_.init();
        setmgr.rSetting(movingLength);
        setmgr.rSetting(fadeLength);
        setmgr.rSetting(text);

        setmgr.rSetting(targetCharmsLine);
        setmgr.rSetting(targetCharms);
        setmgr.rSetting(targetCharmsShader);
        setmgr.rSetting(targetCharmsAnimationSpeed);
        setmgr.rSetting(targetCharmsBlur);
        setmgr.rSetting(targetCharmsRadius);
        setmgr.rSetting(targetCharmsMix);
        setmgr.rSetting(targetCharmsColor);
        setmgr.rSetting(targetCharmsQuality);
        setmgr.rSetting(targetCharmsGradientAlpha);
        setmgr.rSetting(targetCharmsAlphaGradient);
        setmgr.rSetting(targetCharmsDuplicateOutline);
        setmgr.rSetting(targetCharmsMoreGradientOutline);
        setmgr.rSetting(targetCharmsCreepyOutline);
        setmgr.rSetting(targetCharmsAlpha);
        setmgr.rSetting(targetCharmsNumOctavesOutline);
        setmgr.rSetting(targetCharmsSpeedOutline);
    }

    public void onEnable() {
        renderer.reset();
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        predictTimer.reset();
        manualTimer.reset();
        currentTarget = null;
        rotating = false;
        renderPos = null;

        if(!threadMode.getValString().equalsIgnoreCase("None")) processMultiThreading();

        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(motion);
        Kisman.EVENT_BUS.subscribe(render3d);

        subscribed = true;
    }

    public void onDisable() {
        if(subscribed) {
            Kisman.EVENT_BUS.unsubscribe(listener);
            Kisman.EVENT_BUS.unsubscribe(listener1);
            Kisman.EVENT_BUS.unsubscribe(motion);
            Kisman.EVENT_BUS.unsubscribe(render3d);
        }

        if(thread != null) shouldInterrupt.set(false);
        if(executor != null) executor.shutdown();
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        predictTimer.reset();
        manualTimer.reset();
        currentTarget = null;
        rotating = false;
        renderPos = null;
        renderer.reset();
    }

    private void processMultiThreading() {
        if(threadMode.getValString().equalsIgnoreCase("While")) handleWhile();
        else if(!threadMode.getValString().equalsIgnoreCase("None")) handlePool(false);
    }

    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoRer.getInstance(this), 0L, this.threadDelay.getValLong(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void handleWhile() {
        if(thread == null || thread.isInterrupted() || thread.isAlive() || (synsTimer.passedMillis(threadSynsValue.getValLong()) &&threadSyns.getValBoolean())) {
            if(thread == null) thread = new Thread(RAutoRer.getInstance(this));
            else if(synsTimer.passedMillis(threadSynsValue.getValLong()) && !shouldInterrupt.get() && threadSyns.getValBoolean()) {
                shouldInterrupt.set(true);
                synsTimer.reset();
                return;
            }
            if(thread != null && (thread.isInterrupted() || !thread.isAlive())) thread = new Thread(RAutoRer.getInstance(this));
            if(thread != null && thread.getState().equals(Thread.State.NEW)) {
                try {thread.start();} catch (Exception ignored) {}
                synsTimer.reset();
            }
        }
    }

    private void handlePool(boolean justDoIt) {
        if(justDoIt || executor == null || executor.isTerminated() || executor.isShutdown() || (synsTimer.passedMillis(threadSynsValue.getValLong()) && threadSyns.getValBoolean())) {
            if(executor != null) executor.shutdown();
            executor = getExecutor();
            synsTimer.reset();
        }
    }

    public void update() {
        if(mc.player == null || mc.world == null || mc.isGamePaused) return;

        if(renderTimer.passedMillis(clearDelay.getValLong())) {
            placedList.clear();
            renderTimer.reset();
            renderPos = null;
        }

        currentTarget = EntityUtil.getTarget(targetRange.getValFloat());

        if(!lastThreadMode.equalsIgnoreCase(threadMode.getValString())) {
            if (this.executor != null) this.executor.shutdown();
            if (this.thread != null) this.shouldInterrupt.set(true);
            lastThreadMode = threadMode.getValString();
        }

        if(currentTarget == null) return;
        else super.setDisplayInfo("[" + currentTarget.getName() + " | Thread: " + threadMode.getValString() + "]");

        calc: {
            if (fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
                if (threadCalc.getValBoolean() && !threadMode.getValString().equalsIgnoreCase("None")) break calc;
                doCalculatePlace();
                if (placePos != null) if (!mc.world.getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.BEDROCK)) placePos = null;
                calcTimer.reset();
            }
        }

        if(threadMode.getValString().equalsIgnoreCase("None")) {
            if (manualBreaker.getValBoolean()) manualBreaker();
            if (motionCrystal.getValBoolean()) return;
            else if (motionCalc.getValBoolean() && fastCalc.getValBoolean()) return;
            if (multiplication.getValInt() == 1) doAutoRerLogic(null, false);
            else for (int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, false);
        } else processMultiThreading();
    }

    public synchronized void doAutoRerForThread() {
        if(mc.player == null || mc.world == null) return;
        if(manualBreaker.getValBoolean()) manualBreaker();
        if(fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }

        if(multiplication.getValInt() == 1) doAutoRerLogic(null, true);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(null, true);
    }

    private void manualBreaker() {
        RayTraceResult result = mc.objectMouseOver;
        if(manualTimer.passedMillis(200) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && result != null) {
            if(result.typeOfHit.equals(RayTraceResult.Type.ENTITY) && result.entityHit instanceof EntityEnderCrystal) {
                mc.player.connection.sendPacket(new CPacketUseEntity(result.entityHit));
                manualTimer.reset();
            } else if(result.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                for (Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY() + 1.0, mc.objectMouseOver.getBlockPos().getZ())))) {
                    if(!(target instanceof EntityEnderCrystal)) continue;
                    mc.player.connection.sendPacket(new CPacketUseEntity(target));
                    manualTimer.reset();
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motion = new Listener<>(event -> {
        if(!motionCrystal.getValBoolean() || currentTarget == null) return;
        if(motionCalc.getValBoolean() && fastCalc.getValBoolean() && calcTimer.passedMillis(calcDelay.getValLong())) {
            doCalculatePlace();
            calcTimer.reset();
        }
        if(multiplication.getValInt() == 1) doAutoRerLogic(event, false);
        else for(int i = 0; i < multiplication.getValInt(); i++) doAutoRerLogic(event, false);
    });

    private void doAutoRerLogic(EventPlayerMotionUpdate event, boolean thread) {
        if(logic.getValString().equalsIgnoreCase("PlaceBreak")) {
            doPlace(event, thread);
            if(placePos != null) doBreak();
        } else {
            doBreak();
            doPlace(event, thread);
        }
    }

    @EventHandler
    private final Listener<EventRender3D> render3d = new Listener<>(event -> {
        if(targetCharms.getValBoolean()) {
            if(currentTarget != null) {
                try {
                    {
                        FramebufferShader framebufferShader = null;
                        boolean itemglow = false, gradient = false, glow = false, outline = false;

                        switch (targetCharmsShader.getValString()) {
                            case "AQUA":
                                framebufferShader = AquaShader.AQUA_SHADER;
                                break;
                            case "RED":
                                framebufferShader = RedShader.RED_SHADER;
                                break;
                            case "SMOKE":
                                framebufferShader = SmokeShader.SMOKE_SHADER;
                                break;
                            case "FLOW":
                                framebufferShader = FlowShader.FLOW_SHADER;
                                break;
                            case "ITEMGLOW":
                                framebufferShader = ItemShader.ITEM_SHADER;
                                itemglow = true;
                                break;
                            case "PURPLE":
                                framebufferShader = PurpleShader.PURPLE_SHADER;
                                break;
                            case "GRADIENT":
                                framebufferShader = GradientOutlineShader.INSTANCE;
                                gradient = true;
                                break;
                            case "UNU":
                                framebufferShader = UnuShader.UNU_SHADER;
                                break;
                            case "GLOW":
                                framebufferShader = GlowShader.GLOW_SHADER;
                                glow = true;
                                break;
                            case "OUTLINE":
                                framebufferShader = OutlineShader.OUTLINE_SHADER;
                                outline = true;
                                break;
                            case "BlueFlames":
                                framebufferShader = BlueFlamesShader.BlueFlames_SHADER;
                                break;
                            case "CodeX":
                                framebufferShader = CodeXShader.CodeX_SHADER;
                                break;
                            case "Crazy":
                                framebufferShader = CrazyShader.CRAZY_SHADER;
                                break;
                            case "Golden":
                                framebufferShader = GoldenShader.GOLDEN_SHADER;
                                break;
                            case "HideF":
                                framebufferShader = HideFShader.HideF_SHADER;
                                break;
                            case "HolyFuck":
                                framebufferShader = HolyFuckShader.HolyFuckF_SHADER;
                                break;
                            case "HotShit":
                                framebufferShader = HotShitShader.HotShit_SHADER;
                                break;
                            case "Kfc":
                                framebufferShader = KfcShader.KFC_SHADER;
                                break;
                            case "Sheldon":
                                framebufferShader = SheldonShader.SHELDON_SHADER;
                                break;
                            case "Smoky":
                                framebufferShader = SmokyShader.SMOKY_SHADER;
                                break;
                            case "SNOW":
                                framebufferShader = SnowShader.SNOW_SHADER;
                                break;
                            case "Techno":
                                framebufferShader = TechnoShader.TECHNO_SHADER;
                                break;
                        }

                        if (framebufferShader == null) return;

                        framebufferShader.animationSpeed = targetCharmsAnimationSpeed.getValInt();

                        GlStateManager.matrixMode(5889);
                        GlStateManager.pushMatrix();
                        GlStateManager.matrixMode(5888);
                        GlStateManager.pushMatrix();
                        if (itemglow) {
                            ((ItemShader) framebufferShader).red = targetCharmsColor.getColour().r1;
                            ((ItemShader) framebufferShader).green = targetCharmsColor.getColour().g1;
                            ((ItemShader) framebufferShader).blue = targetCharmsColor.getColour().b1;
                            ((ItemShader) framebufferShader).radius = targetCharmsRadius.getValFloat();
                            ((ItemShader) framebufferShader).quality = targetCharmsQuality.getValFloat();
                            ((ItemShader) framebufferShader).blur = targetCharmsBlur.getValBoolean();
                            ((ItemShader) framebufferShader).mix = targetCharmsMix.getValFloat();
                            ((ItemShader) framebufferShader).alpha = 1f;
                            ((ItemShader) framebufferShader).useImage = false;
                        } else if (gradient) {
                            ((GradientOutlineShader) framebufferShader).color = targetCharmsColor.getColour().getColor();
                            ((GradientOutlineShader) framebufferShader).radius = targetCharmsRadius.getValFloat();
                            ((GradientOutlineShader) framebufferShader).quality = targetCharmsQuality.getValFloat();
                            ((GradientOutlineShader) framebufferShader).gradientAlpha = targetCharmsGradientAlpha.getValBoolean();
                            ((GradientOutlineShader) framebufferShader).alphaOutline = targetCharmsAlphaGradient.getValInt();
                            ((GradientOutlineShader) framebufferShader).duplicate = targetCharmsDuplicateOutline.getValFloat();
                            ((GradientOutlineShader) framebufferShader).moreGradient = targetCharmsMoreGradientOutline.getValFloat();
                            ((GradientOutlineShader) framebufferShader).creepy = targetCharmsCreepyOutline.getValFloat();
                            ((GradientOutlineShader) framebufferShader).alpha = targetCharmsAlpha.getValFloat();
                            ((GradientOutlineShader) framebufferShader).numOctaves = targetCharmsNumOctavesOutline.getValInt();
                        } else if(glow) {
                            ((GlowShader) framebufferShader).red = targetCharmsColor.getColour().r1;
                            ((GlowShader) framebufferShader).green = targetCharmsColor.getColour().g1;
                            ((GlowShader) framebufferShader).blue = targetCharmsColor.getColour().b1;
                            ((GlowShader) framebufferShader).radius = targetCharmsRadius.getValFloat();
                            ((GlowShader) framebufferShader).quality = targetCharmsQuality.getValFloat();
                        } else if(outline) {
                            ((OutlineShader) framebufferShader).red = targetCharmsColor.getColour().r1;
                            ((OutlineShader) framebufferShader).green = targetCharmsColor.getColour().g1;
                            ((OutlineShader) framebufferShader).blue = targetCharmsColor.getColour().b1;
                            ((OutlineShader) framebufferShader).radius = targetCharmsRadius.getValFloat();
                            ((OutlineShader) framebufferShader).quality = targetCharmsQuality.getValFloat();
                        }
                        framebufferShader.startDraw(event.particalTicks);
                        for (Entity entity : mc.world.loadedEntityList) {
                            if (entity == mc.player || entity == mc.getRenderViewEntity() || !entity.equals(currentTarget)) continue;
                            Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.particalTicks);
                            Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.particalTicks);
                        }
                        framebufferShader.stopDraw();
                        if (gradient) ((GradientOutlineShader) framebufferShader).update(targetCharmsSpeedOutline.getValDouble());
                        GlStateManager.color(1f, 1f, 1f);
                        GlStateManager.matrixMode(5889);
                        GlStateManager.popMatrix();
                        GlStateManager.matrixMode(5888);
                        GlStateManager.popMatrix();
                    }
                } catch (Exception ignored) {
                    if(Config.instance.antiOpenGLCrash.getValBoolean() || lagProtect.getValBoolean()) {
                        super.setToggled(false);
                        ChatUtils.error("[AutoRer] Error, Config -> AntiOpenGLCrash disabled AutoRer");
                    }
                }
            }
        }

        if(render.getValBoolean()) if(placePos != null) renderer.onRenderWorld(movingLength.getValFloat(), fadeLength.getValFloat(), renderer_, placePos, text.getValBoolean());
    });

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if(instantRotate.getValBoolean() && !motionCrystal.getValBoolean() && (rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All"))) {
            float[] rots = RotationUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5f), (pos.getY() - 0.5f), (pos.getZ() + 0.5f)));
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }
        CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityID;
        packet.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packet);
        breakTimer.reset();
        predictTimer.reset();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketSpawnObject && instant.getValBoolean()) {
            SPacketSpawnObject packet =  (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                if(!(mc.world.getEntityByID(packet.getEntityID()) instanceof EntityEnderCrystal)) return;
                BlockPos toRemove = null;
                for (PlaceInfo placeInfo : placedList) {
                    BlockPos pos = placeInfo.getBlockPos();
                    boolean canSee = EntityUtil.canSee(pos);
                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) >= (canSee ? breakRange.getValDouble() : breakWallRange.getValDouble())) break;

                    if(instantCalc.getValBoolean() && currentTarget != null) {
                        float targetDamage = CrystalUtils.calculateDamage(pos, currentTarget, terrain.getValBoolean());
                        if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                            float selfDamage = CrystalUtils.calculateDamage(pos, mc.player, terrain.getValBoolean());
                            if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                                toRemove = pos;
                                if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(packet.getEntityID());} catch (Exception ignored) {}
                                attackCrystalPredict(packet.getEntityID(), pos);
                                swing();
                            }
                        }
                    } else {
                        toRemove = pos;
                        if (inhibit.getValBoolean()) try {lastHitEntity = mc.world.getEntityByID(packet.getEntityID());} catch (Exception ignored) {}
                        attackCrystalPredict(packet.getEntityID(), pos);
                        swing();
                    }

                    break;
                }
                if (toRemove != null) placedList.remove(toRemove);
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && ((inhibit.getValBoolean() && lastHitEntity != null) || (sound.getValBoolean()))) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) if (lastHitEntity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) lastHitEntity.setDead();
            if(threadMode.checkValString(ThreadMode.Sound.name()) && isRightThread() && mc.player != null && mc.player.getDistanceSq(new BlockPos(packet.getX(), packet.getY(), packet.getZ())) < MathUtil.square(threadSoundPlayer.getValInt())) handlePool(true);
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) try {
            PlaceInfo info = AutoRerUtil.Companion.getPlaceInfo(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos(), currentTarget, terrain.getValBoolean());
            placedList.add(info);
        } catch (Exception ignored) {}
        if(removeAfterAttack.getValBoolean() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if(packet.getAction().equals(CPacketUseEntity.Action.ATTACK) && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                try {mc.world.removeEntityFromWorld(packet.entityId);} catch(Exception ignored) {}
            }
        }
    });

    private boolean isRightThread() {
        return mc.isCallingFromMinecraftThread() || (!this.threadOngoing.get());
    }

    private void doCalculatePlace() {
        try {
            calculatePlace();
            if(recalc.getValBoolean() && placePos == null) recalculatePlace();
        } catch (Exception e) {if(lagProtect.getValBoolean()) super.setToggled(false);}
    }

    private void recalculatePlace() {
        List<BlockPos> sphere = CrystalUtils.getSphere(recalcPlaceRange.getValFloat(), true, false);
        List<BlockPos> validPos = new ArrayList<>();

        sphere.stream()
                .filter(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean()) > minDMG.getValInt() || CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean()) * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt()))
                .filter(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean()) <= maxSelfDMG.getValInt())
                .filter(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean()) + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount())
                .filter(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean()) < CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean()))
                .forEach(validPos::add);

        validPos.sort((first, second) -> (int) (mc.player.getDistanceSqToCenter(first) - mc.player.getDistanceSqToCenter(second)));

        final double[] maxDamage = {0.5};
        final BlockPos[] placePos = {null};

        validPos.forEach(pos -> {
            if(CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean()) > maxDamage[0]) {
                maxDamage[0] = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean());
                placePos[0] = pos;
            }
        });

        this.placePos = placePos[0] == null ? null : AutoRerUtil.Companion.getPlaceInfo(placePos[0], currentTarget, terrain.getValBoolean());
    }

    private void calculatePlace() {
        double maxDamage = 0.5;
        BlockPos placePos = null;
        List<BlockPos> sphere = CrystalUtils.getSphere(placeRange.getValFloat(), true, false);

        if(calcDistSort.getValBoolean()) {
            Collections.sort(sphere);
            Collections.reverse(sphere);
        }

        for(int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);

            if(thirdCheck.getValBoolean() && !isPosValid(pos)) continue;
            if(CrystalUtils.canPlaceCrystal(pos, secondCheck.getValBoolean(), true, multiPlace.getValBoolean(), firePlace.getValBoolean())) {
                float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, currentTarget, terrain.getValBoolean());

                if(targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                    float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, terrain.getValBoolean());

                    if(selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            placePos = pos;
                        }
                    }
                }
            }
        }
        this.placePos = placePos == null ? null : AutoRerUtil.Companion.getPlaceInfo(placePos, currentTarget, terrain.getValBoolean());
    }

    private boolean isPosValid(BlockPos pos) {
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5) <= (EntityUtil.canSee(pos) ?  placeRange.getValDouble() : placeWallRange.getValDouble());
    }

    private void doPlace(EventPlayerMotionUpdate event, boolean thread) {
        if(!place.getValBoolean() || !placeTimer.passedMillis(placeDelay.getValLong()) || (placePos == null && fastCalc.getValBoolean())) return;
        if(!fastCalc.getValBoolean() || (thread && threadCalc.getValBoolean())) doCalculatePlace();
        if(placePos == null || (!mc.world.getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(placePos.getBlockPos()).getBlock().equals(Blocks.BEDROCK))) return;
        if(syns.getValBoolean() && placedList.contains(placePos)) return;

        SilentSwitchBypass bypass = new SilentSwitchBypass(Items.END_CRYSTAL);
        EnumHand hand = null;
        boolean offhand = mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL);
        boolean silentBypass = switch_.getValString().equals("SilentBypass");
        int oldSlot = mc.player.inventory.currentItem;
        int crystalSlot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9);

        if(crystalSlot == -1 && !silentBypass && !offhand) return;

        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !offhand) {
            if(switch_.getValString().equals("None")) return;
            else if ("Normal".equals(switch_.getValString())) InventoryUtil.switchToSlot(crystalSlot, false);
            else if ("Silent".equals(switch_.getValString())) InventoryUtil.switchToSlot(crystalSlot, true);
            else if (silentBypass) bypass.doSwitch();
        }

        if(mc.player == null) return;
        if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
        if(mc.player.isHandActive()) hand = mc.player.getActiveHand();

        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};

        if(rotate.getValString().equalsIgnoreCase("Place") || rotate.getValString().equalsIgnoreCase("All") && currentTarget != null) {
            try {
                float[] rots = RotationUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((placePos.getBlockPos().getX() + 0.5f), (placePos.getBlockPos().getY() - 0.5f), (placePos.getBlockPos().getZ() + 0.5f)));
                if (!thread) {
                    if (!motionCrystal.getValBoolean()) {
                        mc.player.rotationYaw = rots[0];
                        mc.player.rotationPitch = rots[1];
                    } else if (event != null) {
                        event.setYaw(rots[0]);
                        event.setPitch(rots[1]);
                    }
                } else if (threadPacketRots.getValBoolean()) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            } catch (Exception ignored) {}
        }

        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + ( double ) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(( double ) placePos.getBlockPos().getX() + 0.5, ( double ) placePos.getBlockPos().getY() - 0.5, ( double ) placePos.getBlockPos().getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos.getBlockPos(), facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        if(!swing.checkValString(SwingMode.None.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        placeTimer.reset();

        renderPos = placePos;

        if(ai.getValBoolean()) {
            float targetDamage = CrystalUtils.calculateDamage(mc.world, placePos.getBlockPos().getX() + 0.5, placePos.getBlockPos().getY() + 1, placePos.getBlockPos().getZ() + 0.5, currentTarget, terrain.getValBoolean());
            float selfDamage = CrystalUtils.calculateDamage(mc.world, placePos.getBlockPos().getX() + 0.5, placePos.getBlockPos().getY() + 1, placePos.getBlockPos().getZ() + 0.5, mc.player, terrain.getValBoolean());
            AutoRerAI.collect(placePos.getBlockPos(), targetDamage, selfDamage);
        }

        if((rotate.getValString().equalsIgnoreCase("Place") || rotate.getValString().equalsIgnoreCase("All")) && rotateMode.getValString().equalsIgnoreCase("Silent")) {
            mc.player.rotationYaw = oldRots[0];
            mc.player.rotationPitch = oldRots[1];
        }
        if(hand != null) mc.player.setActiveHand(hand);
        if(oldSlot != -1 && !silentBypass) if (switch_.getValString().equals(SwitchMode.Silent.name())) InventoryUtil.switchToSlot(oldSlot, true);
        else if(silentBypass) bypass.doSwitch();
    }

    private Entity getCrystalForAntiCevBreaker() {
        Entity crystal = null;
        String mode = antiCevBreakerMode.getValString();

        if(!mode.equals("None")) {
            if(mode.equals("Cev") || mode.equals("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Cev.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
            if(mode.equals("Civ") || mode.equals("Both")) {
                for(Vec3i vec : AntiCevBreakerVectors.Civ.vectors) {
                    BlockPos pos = mc.player.getPosition().add(vec);
                    for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                        if(entity instanceof EntityEnderCrystal) {
                            crystal = entity;
                        }
                    }
                }
            }
        }

        return crystal;
    }

    private Entity getCrystalWithMaxDamage() {
        Entity crystal = null;
        double maxDamage = 0.5;

        try {
            for (int i = 0; i < mc.world.loadedEntityList.size(); ++i) {
                Entity entity = mc.world.loadedEntityList.get(i);
                if (entity == null) continue;

                if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValDouble() : breakWallRange.getValDouble())) {
                    Friend friend = getNearFriendWithMaxDamage(entity);
                    double targetDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());

                    if (friend != null && !friend_.getValString().equalsIgnoreCase(FriendMode.None.name())) {
                        if (friend_.getValString().equalsIgnoreCase(FriendMode.AntiTotemPop.name()) && friend.isTotemPopped) return null;
                        else if (friend.isTotemFailed) return null;
                        if (friend.damage >= maxFriendDMG.getValInt()) return null;
                    }

                    if (targetDamage > minDMG.getValInt() || targetDamage * lethalMult.getValDouble() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(currentTarget, armorBreaker.getValInt())) {
                        double selfDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, mc.player, terrain.getValBoolean());

                        if (selfDamage <= maxSelfDMG.getValInt() && selfDamage + 2 <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                            if (maxDamage <= targetDamage) {
                                maxDamage = targetDamage;
                                crystal = entity;
                            }
                        }
                    }
                }
            }
        } catch(NullPointerException ignored) {
            if(lagProtect.getValBoolean()) super.setToggled(false);
        }

        return crystal;
    }

    private void doBreak() {
        if(!break_.getValBoolean() || !breakTimer.passedMillis(breakDelay.getValLong())) return;

        Entity crystal, crystalWithMaxDamage = getCrystalWithMaxDamage();
        
        if(breakPriority.getValString().equals("Damage")) crystal = crystalWithMaxDamage;
        else crystal = getCrystalForAntiCevBreaker();

        if(crystal == null) crystal = crystalWithMaxDamage;
        if(crystal == null) return;

        float[] oldRots = new float[] {mc.player.rotationYaw, mc.player.rotationPitch};

        if(rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All")) {
            float[] rots = RotationUtils.getRotation(crystal);
            mc.player.rotationYaw = rots[0];
            mc.player.rotationPitch = rots[1];
        }

        lastHitEntity = crystal;
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        swing();
        try {if(clientSide.getValBoolean()) mc.world.removeEntityFromWorld(crystal.entityId);} catch (Exception ignored) {}
        breakTimer.reset();

        if((rotate.getValString().equalsIgnoreCase("Break") || rotate.getValString().equalsIgnoreCase("All")) && rotateMode.getValString().equalsIgnoreCase("Silent")) {
            mc.player.rotationYaw = oldRots[0];
            mc.player.rotationPitch = oldRots[1];
        }

        BlockPos toRemove = null;

        if(syns.getValBoolean()) for(PlaceInfo info : placedList) {
            BlockPos pos = info.getBlockPos();
            if(crystal.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= 3) toRemove = pos;
        }
        if(toRemove != null) placedList.remove(toRemove);
    }

    private void swing() {
        if(swing.checkValString(SwingMode.None.name())) return;
        if(swing.getValString().equals(SwingMode.PacketSwing.name())) mc.player.connection.sendPacket(new CPacketAnimation(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        else mc.player.swingArm(swing.getValString().equals(SwingMode.MainHand.name()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
    }

    private Friend getNearFriendWithMaxDamage(Entity entity) {
        ArrayList<Friend> friendsWithMaxDamage = new ArrayList<>();

        for(EntityPlayer player : mc.world.playerEntities) {
            if(mc.player == player) continue;
            if(FriendManager.instance.isFriend(player)) {
                double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
                if(friendDamage <= maxFriendDMG.getValInt() || friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()) friendsWithMaxDamage.add(new Friend(player, friendDamage, friendDamage * lethalMult.getValDouble() >= player.getHealth() + player.getAbsorptionAmount()));
            }
        }

        Friend nearFriendWithMaxDamage = null;
        double maxDamage = 0.5;

        for(Friend friend : friendsWithMaxDamage) {
            double friendDamage = CrystalUtils.calculateDamage(mc.world, entity.posX, entity.posY, entity.posZ, currentTarget, terrain.getValBoolean());
            if(friendDamage > maxDamage) {
                maxDamage = friendDamage;
                nearFriendWithMaxDamage = new Friend(friend.friend, friendDamage);
            }
        }

        return nearFriendWithMaxDamage;
    }

    public enum ThreadMode {None, Pool, Sound, While}
    public enum Render {None, Default, Advanced}
    public enum Rotate {Off, Place, Break, All}
    public enum Raytrace {None, Place, Break, Both}
    public enum SwitchMode {None, Normal, Silent, SilentBypass}
    public enum SwingMode {MainHand, OffHand, PacketSwing, None}
    public enum FriendMode {None, AntiTotemFail, AntiTotemPop}
    public enum LogicMode {PlaceBreak, BreakPlace}
    public enum RotateMode {Normal, Silent}
    public enum AntiCevBreakerMode {None, Cev, Civ, Both}
    public enum BreakPriority {Damage, CevBreaker}

    public enum AntiCevBreakerVectors {
        Cev(Collections.singletonList(new Vec3i(0, 2, 0))),
        Civ(Arrays.asList(new Vec3i(1, 2, 0), new Vec3i(-1, 2, 0), new Vec3i(0, 2, 1), new Vec3i(0, 2, -1), new Vec3i(1, 2, 1), new Vec3i(-1, 2, -1), new Vec3i(1, 2, -1), new Vec3i(-1, 2, 1)));

        public final List<Vec3i> vectors;

        AntiCevBreakerVectors(List<Vec3i> vectors) {
            this.vectors = vectors;
        }
    }

    private static class Friend {
        public final EntityPlayer friend;
        public double damage;
        public boolean isTotemPopped;
        public boolean isTotemFailed = false;

        public Friend(EntityPlayer friend, double damage) {
            this.friend = friend;
            this.damage = damage;
            this.isTotemPopped = false;
        }

        public Friend(EntityPlayer friend, double damage, boolean isTotemPopped) {
            this.friend = friend;
            this.damage = damage;
            if(isTotemPopped) isTotemFailed = !(mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING));
            this.isTotemPopped = isTotemPopped;
        }
    }

    /**
     * @author XuluPlus
     */
    public static class Util {
        public static List<BlockPos> possiblePlacePositions(float placeRange, boolean secondCheck, boolean thirdCheck, boolean multiPlace, boolean firePlace) {
            NonNullList list = NonNullList.create();
            list.addAll(getSphere(mc.player.getPosition(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> CrystalUtils.canPlaceCrystal(pos, secondCheck, thirdCheck, multiPlace, firePlace)).collect(Collectors.toList()));
            return ((List<BlockPos>) list);
        }

        public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
            final ArrayList<BlockPos> circleblocks = new ArrayList<>();
            final int cx = pos.getX();
            final int cy = pos.getY();
            final int cz = pos.getZ();
            for (int x = cx - (int)r; x <= cx + r; ++x) {
                for (int z = cz - (int)r; z <= cz + r; ++z) {
                    int y = sphere ? (cy - (int)r) : cy;
                    while (true) {
                        final float f = (float)y;
                        final float f2 = sphere ? (cy + r) : ((float)(cy + h));
                        if (f >= f2) break;
                        final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                        if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) circleblocks.add(new BlockPos(x, y + plus_y, z));
                        ++y;
                    }
                }
            }
            return circleblocks;
        }
    }

    public static class RAutoRer implements Runnable {
        private static RAutoRer instance;
        private AutoRer autoRer;

        public static RAutoRer getInstance(AutoRer autoRer) {
            if(instance == null) {
                instance = new RAutoRer();
                instance.autoRer = autoRer;
            }
            return instance;
        }

        @Override
        public void run() {
            if(autoRer.threadMode.getValString().equalsIgnoreCase("While")) {
                while (autoRer.isToggled() && autoRer.threadMode.getValString().equalsIgnoreCase("While")) {
                    if(autoRer.shouldInterrupt.get()) {
                        autoRer.shouldInterrupt.set(false);
                        autoRer.synsTimer.reset();
                        autoRer.thread.interrupt();
                    }
                    autoRer.threadOngoing.set(true);
                    autoRer.doAutoRerForThread();
                    autoRer.threadOngoing.set(false);
                    try {Thread.sleep(autoRer.threadDelay.getValLong());} catch (InterruptedException e) {autoRer.thread.interrupt();}
                }
            } else if(!autoRer.threadMode.getValString().equalsIgnoreCase("None")) {
                autoRer.threadOngoing.set(true);
                autoRer.doAutoRerForThread();
                autoRer.threadOngoing.set(false);
            }
        }
    }
}