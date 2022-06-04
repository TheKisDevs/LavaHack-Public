package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class BreakAlert extends Module {
    private Setting messageType = new Setting("Message Type", this, MessageType.Chat);
    private Setting displayShowDelay = new Setting("Display Show Delay", this, 1000, 1, 5000, Slider.NumberType.TIME);

    private ArrayList<BlockPos> blocksBeginBroken = new ArrayList<>();
    private TimerUtils renderTimer = new TimerUtils();

    public BreakAlert() {
        super("BreakAlert", Category.COMBAT);

        setmgr.rSetting(messageType);
        setmgr.rSetting(displayShowDelay);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(receive);
        blocksBeginBroken.clear();
    }

    public void update() {
        if(mc.player == null || mc.world == null || blocksBeginBroken.isEmpty()) return;

        for(int i = 0; i < blocksBeginBroken.size(); i++) if(messageType.getValString().equalsIgnoreCase(MessageType.Chat.name())) ChatUtils.warning(TextFormatting.DARK_PURPLE + "Break Alert! " + TextFormatting.LIGHT_PURPLE + "Your surround blocks is mining!");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(renderTimer.passedMillis(displayShowDelay.getValLong())){
            for(int i = 0; i < blocksBeginBroken.size(); i++) {
               if(messageType.getValString().equalsIgnoreCase(MessageType.Display.name())) {
                    ScaledResolution sr = new ScaledResolution(mc);
                    CustomFontUtil.comfortaab72.drawCenteredStringWithShadow(TextFormatting.DARK_PURPLE + "Break Alert!", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - CustomFontUtil.getFontHeight(CustomFontUtil.comfortaab72), -1);
                    CustomFontUtil.comfortaab55.drawCenteredStringWithShadow(TextFormatting.LIGHT_PURPLE + "Your surround blocks is mining!", sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 5, -1);
                }
            }
        } else renderTimer.reset();
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();

            if(getSurroundBlocks().isEmpty() || !getSurroundBlocks().contains(packet.getPosition())) return;
            if(!blocksBeginBroken.contains(packet.getPosition()) && packet.getProgress() > 0 && packet.getProgress() <= 10) blocksBeginBroken.add(packet.getPosition());
            else if(packet.getProgress() <= 0 || packet.getProgress() > 10) blocksBeginBroken.remove(packet.getPosition());
        }
    });

    private ArrayList<BlockPos> getSurroundBlocks() {
        int z;
        int x;
        double decimalX = Math.abs(mc.player.posX) - Math.floor(Math.abs(mc.player.posX));
        double decimalZ = Math.abs(mc.player.posZ) - Math.floor(Math.abs(mc.player.posZ));
        int lengthX = calculateLength(decimalX, false);
        int negativeLengthX = calculateLength(decimalX, true);
        int lengthZ = calculateLength(decimalZ, false);
        int negativeLengthZ = calculateLength(decimalZ, true);
        ArrayList<BlockPos> tempOffsets = new ArrayList<>();
        for (x = 1; x < lengthX + 1; ++x) {
            tempOffsets.add(addToPosition(getPlayerPosition(), x, 1 + lengthZ));
            tempOffsets.add(addToPosition(getPlayerPosition(), x, -(1 + negativeLengthZ)));
        }
        for (x = 0; x <= negativeLengthX; ++x) {
            tempOffsets.add(addToPosition(getPlayerPosition(), -x, 1 + lengthZ));
            tempOffsets.add(addToPosition(getPlayerPosition(), -x, -(1 + negativeLengthZ)));
        }
        for (z = 1; z < lengthZ + 1; ++z) {
            tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, z));
            tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), z));
        }
        for (z = 0; z <= negativeLengthZ; ++z) {
            tempOffsets.add(addToPosition(getPlayerPosition(), 1 + lengthX, -z));
            tempOffsets.add(addToPosition(getPlayerPosition(), -(1 + negativeLengthX), -z));
        }
        return tempOffsets;
    }

    private int calculateLength(double decimal, boolean negative) {
        if (negative) return decimal <= Double.longBitsToDouble(Double.doubleToLongBits(30.561776836994962) ^ 0x7FEDBCE3A865B81CL) ? 1 : 0;
        return decimal >= Double.longBitsToDouble(Double.doubleToLongBits(22.350511399288944) ^ 0x7FD03FDD7B12B45DL) ? 1 : 0;
    }

    private BlockPos getPlayerPosition() {
        return new BlockPos(mc.player.posX, mc.player.posY - Math.floor(mc.player.posY) > Double.longBitsToDouble(Double.doubleToLongBits(19.39343307331816) ^ 0x7FDAFD219E3E896DL) ? Math.floor(mc.player.posY) + Double.longBitsToDouble(Double.doubleToLongBits(4.907271931218261) ^ 0x7FE3A10BE4A4A510L) : Math.floor(mc.player.posY), mc.player.posZ);
    }

    private BlockPos addToPosition(BlockPos pos, double x, double z) {
        block1: {
            if (pos.getX() < 0) x = -x;
            if (pos.getZ() >= 0) break block1;
            z = -z;
        }
        return pos.add(x, Double.longBitsToDouble(Double.doubleToLongBits(1.4868164896774578E308) ^ 0x7FEA7759ABE7F7C1L), z);
    }

    public enum MessageType {Chat, Display}
}
