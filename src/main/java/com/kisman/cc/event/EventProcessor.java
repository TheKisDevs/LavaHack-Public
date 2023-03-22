package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;

import com.kisman.cc.event.events.subscribe.TotemPopEvent;
import com.kisman.cc.hypixel.util.ConfigHandler;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.player.ElytraEquip;
import com.kisman.cc.gui.auth.AuthGui;
import com.kisman.cc.util.TickRateUtil;

import com.kisman.cc.util.modules.CustomMainMenu;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventProcessor {
    private final Minecraft mc = Minecraft.getMinecraft();

    //NEC vars
    public boolean hasRan = false;

    public AtomicBoolean ongoing;

    public int oldWidth = -1, oldHeight = -1;

    public EventProcessor() {
        MinecraftForge.EVENT_BUS.register(this);
        Kisman.EVENT_BUS.subscribe(totempop);
        Kisman.EVENT_BUS.subscribe(TickRateUtil.INSTANCE.listener);
        Kisman.EVENT_BUS.subscribe(packet);

        ongoing = new AtomicBoolean(false);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if(Kisman.instance.aiImpr != null) Kisman.instance.aiImpr.onEntityJoinWorld(event);
    }

    @SubscribeEvent public void onGuiOpen(GuiOpenEvent event) {if(!(event.getGui() instanceof AuthGui) && Kisman.isOpenAuthGui && !Kisman.allowToConfiguredAnotherClients) event.setCanceled(true);}

    public void onInit() {
        if(Kisman.allowToConfiguredAnotherClients) return;
        mc.displayGuiScreen(new AuthGui());
        Kisman.isOpenAuthGui = true;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        try {
            if (AutoRer.instance.lagProtect.getValBoolean()) disableCa();
            AutoRer.instance.placePos = null;
            Kisman.instance.configManager.getSaver().init();
        } catch(Exception ignored) {}
    }

    private void disableCa() {
        AutoRer.instance.setToggled(false);
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(AutoRer.instance.lagProtect.getValBoolean()) disableCa();
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Kisman.EVENT_BUS.post(this);
        if(oldWidth != mc.displayWidth || oldHeight != mc.displayHeight) {
            oldWidth = mc.displayWidth;
            oldHeight = mc.displayHeight;
            new EventResolutionUpdate(oldWidth, oldHeight).post();
        }
        if(CustomMainMenuModule.instance != null) CustomMainMenu.update();
        if(ElytraEquip.instance != null) ElytraEquip.instance.updateState();
        if(Config.instance != null) Kisman.canUseImprAstolfo = Config.instance.astolfoColorMode.checkValString(Config.AstolfoColorMode.Impr.name());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Kisman.EVENT_BUS.post(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ClientChatEvent event) {
        if(event.getMessage().startsWith(Kisman.instance.commandManager.cmdPrefixStr)) {
            try {
                Kisman.instance.commandManager.runCommands(event.getMessage().substring(0));
                event.setCanceled(true);
            } catch (Exception ignored) {}
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packet = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketRespawn && AutoRer.instance.lagProtect.getValBoolean()) disableCa();
        if(event.getPacket() instanceof SPacketChat && !Kisman.allowToConfiguredAnotherClients && Config.instance.configurate.getValBoolean()) {
            SPacketChat packet = (SPacketChat) event.getPacket();
            String message = packet.chatComponent.getUnformattedText();
            if(message.contains("+")) {
                String formattedMessage = message.substring(message.indexOf("+"));
                try {
                    String[] args = formattedMessage.split(" ");
                    if (args[0] != null && args[1] != null) {
                        Module module = Kisman.instance.moduleManager.getModule(args[1]);
                        if (module == null) return;
                        if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("+disable")) module.setToggled(false);
                        else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("+enable")) module.setToggled(true);
                        else if (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("+block")) module.block = true;
                        else if (args[0].equalsIgnoreCase("unblock") || args[0].equalsIgnoreCase("+unlock")) module.block = true;
                    }
                } catch(Exception ignored) {}
            }
        }
    });

    //NEC events
    @SubscribeEvent
    public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if(ConfigHandler.hasKey(Configuration.CATEGORY_GENERAL, "Flip")){
            Timer timer = new Timer();
            hasRan = true;
            timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Flip.flip(mc.player);
                        }
                    }, 2000);
        } else ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "Flip", "true");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chat(ClientChatReceivedEvent event) {
        if (!event.getMessage().getUnformattedText().startsWith("Your new API key is ")) return;
        String key = event.getMessage().getUnformattedText().split("key is ")[1];
        ConfigHandler.writeConfig(Configuration.CATEGORY_GENERAL, "APIKey", key);
        ChatUtils.complete(TextFormatting.GRAY + "[" + TextFormatting.GOLD + "NEC for 1.12.2 by _kisman_" + TextFormatting.GRAY + "]" + TextFormatting.GRAY + " API Key set to " + TextFormatting.GREEN + key);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> totempop = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            TotemPopEvent totemPopEvent = new TotemPopEvent(((SPacketEntityStatus) event.getPacket()).getEntity(mc.world));
            MinecraftForge.EVENT_BUS.post(totemPopEvent);
            if(totemPopEvent.isCanceled()) event.cancel();
        }
    });
}