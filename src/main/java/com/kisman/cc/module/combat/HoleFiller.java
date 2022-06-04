package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.module.combat.holefiller.Hole;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;

public class HoleFiller extends Module {
    private final Setting range = new Setting("Range", this, 4.8, 1, 6, false);
    private final Setting placeMode = new Setting("PlaceMode", this, PlaceMode.Always);
    private final Setting delay = new Setting("Delay", this, 2, 0, 20, true);
    private final Setting targetHoleRange = new Setting("TargetHoleRange", this, 4.8, 1, 6, false);
    private final Setting switchMode = new Setting("SwitchMode", this, SwitchMode.Silent);
    private final Setting smartWeb = new Setting("SmartWeb", this, false);
    private final Setting render = new Setting("Render", this, true);

    public static HoleFiller instance;

    private final ArrayList<Hole> holes = new ArrayList<>();
    private final TimerUtils timer = new TimerUtils();

    public EntityPlayer target;
    public Hole targetHole;

    public HoleFiller() {
        super("HoleFiller", "HoleFiller", Category.COMBAT);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(placeMode);
        setmgr.rSetting(delay);
        setmgr.rSetting(targetHoleRange);
        setmgr.rSetting(switchMode);
        setmgr.rSetting(smartWeb);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        target = null;
        targetHole = null;
        holes.clear();
    }

    public void onDisable() {
        target = null;
        targetHole = null;
        holes.clear();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        target = EntityUtil.getTarget(range.getValFloat());
        
        if(target == null) super.setDisplayInfo("");
        else super.setDisplayInfo(TextFormatting.GRAY + "[" + TextFormatting.WHITE + target.getName() + TextFormatting.GRAY + "]");

        if(target == null && placeMode.getValString().equals(PlaceMode.Smart.name())) target = EntityUtil.getTarget(range.getValFloat());
        else if(timer.passedMillis(delay.getValLong()) && target != null){
            findHoles(mc.player, (float) range.getValDouble());
            findTargetHole();

            if(targetHole != null) {
                final int obbySlot = InventoryUtil.findBlock(Blocks.OBSIDIAN, 0, 9);
                final int webSlot = InventoryUtil.findBlock(Blocks.WEB, 0, 9);
                final int oldSlot = mc.player.inventory.currentItem;

                BlockUtil.canPlaceBlock(targetHole.pos);

                int requirestSlot = BlockUtil.canPlaceBlock(targetHole.pos) ? obbySlot : smartWeb.getValBoolean() ? webSlot : obbySlot;

                switch (switchMode.getValString()) {
                    case "None": {
                        if (requirestSlot == -1) return;
                        break;
                    }
                    case "Normal": {
                        if(requirestSlot != -1) InventoryUtil.switchToSlot(obbySlot, false);
                        else return;
                        break;
                    }
                    case "Silent": {
                        if(requirestSlot != -1) InventoryUtil.switchToSlot(obbySlot, true);
                        else return;
                        break;
                    }
                }

                BlockUtil.placeBlock(targetHole.pos);

                try{if(switchMode.getValString().equals(SwitchMode.Silent.name()) && oldSlot != -1) InventoryUtil.switchToSlot(oldSlot, true);} catch(Exception ignored) {}

                timer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(render.getValBoolean() && targetHole != null) RenderUtil.drawBlockESP(targetHole.pos, 1, 0, 0);
    }

    private void findTargetHole() {
        targetHole = getNearHole();
    }

    private Hole getNearHole() {
        return holes.stream()
                .filter(this::isValidHole)
                .min(Comparator.comparing(hole -> mc.player.getDistanceSq(hole.pos)))
                .orElse(null);
    }

    private void findHoles(EntityPlayer player, float range) {
        holes.clear();
        for(BlockPos pos : CrystalUtils.getSphere(player, range, true, false)) if(mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) if(isBlockHole(pos)) holes.add(new Hole(pos, (float) mc.player.getDistanceSq(pos), (float) player.getDistanceSq(pos)));
    }

    private boolean isValidHole(Hole hole) {
        if(mc.player.getDistanceSq(hole.pos) > range.getValDouble()) return false;
        if(placeMode.getValString().equals(PlaceMode.Smart.name())) if(WorldUtil.getDistance(target, hole.pos) > targetHoleRange.getValDouble()) return false;

        return mc.world.getBlockState(hole.pos.up(1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(hole.pos.up(2)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(hole.pos.up(3)).getBlock().equals(Blocks.AIR);
        //TODO: update hole validation!!!
        //TODO: added a crystal check!!!
    }

    private boolean isBlockHole(BlockPos blockpos) {
        int holeblocks = 0;

        if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;
        if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.ENDER_CHEST) ++holeblocks;

        return holeblocks >= 9;
    }

    public enum RotateMode {None, Normal, Silent}
    public enum PlaceMode {Always, Smart}
    public enum SwitchMode {None, Normal, Silent}
}