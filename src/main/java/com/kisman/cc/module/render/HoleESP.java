package com.kisman.cc.module.render;

import com.google.common.collect.Sets;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.render.KonasRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HoleESP extends Module {
    private Setting mode = new Setting("Mode", this, "Air", new ArrayList<>(Arrays.asList("Air", "Ground", "Flat", "Slab", "Double", "Konas")));
    private Setting konasMode = new Setting("Konas Mode", this, KonasMode.FULL).setVisible(() -> mode.checkValString("Konas"));
    private Setting konasLine = new Setting("Konas Line", this, KonasLines.BOTTOM).setVisible(() -> mode.checkValString("Konas"));
    private Setting radius = new Setting("Radius", this, 8, 0, 32, true);
    private Setting ignoreOwnHole = new Setting("IgnoreOwnHole", this, false);
    private Setting flatOwn = new Setting("FlatOwn", this, false).setVisible(() -> !mode.checkValString("Konas"));
    private Setting height = new Setting("Height", this, 0.5, 0.1, 2, false);
    private Setting width = new Setting("Width", this, 1, 1, 10, true);
    private Setting type = new Setting("Type", this, "Both", new ArrayList<>(Arrays.asList("Outline", "Fill", "Both"))).setVisible(() -> !mode.checkValString("Konas"));
    private Setting ufoAlpha = new Setting("UFOAlpha", this, 255, 0, 255, true);

    private Setting depth = new Setting("Depth", this, true).setVisible(() -> mode.checkValString("Konas"));
    private Setting noLineDepth = new Setting("No Line", this, true).setVisible(() -> mode.checkValString("Konas") && konasMode.checkValString("FADE") && depth.getValBoolean());
    private Setting notSelf = new Setting("Not Self", this, true).setVisible(() -> mode.checkValString("Konas") && konasMode.checkValString("FADE"));
    private Setting sides = new Setting("Sides", this, false).setVisible(() -> mode.checkValString("Konas") && (konasMode.checkValString("FULL") || konasMode.checkValString("FADE")));

    private Setting obby = new Setting("_ObsidianHoles", this, "ObsidianHoles");
    private Setting obbyHoles = new Setting("ObsidianHoles", this, true);
    private Setting obbyColor = new Setting("ObbyColor", this, "ObbyColor", new Colour(255, 0, 0)).setVisible(obbyHoles::getValBoolean);


    private Setting bedrock = new Setting("_BedrockHoles", this, "BedrockHoles");
    private Setting bedrockHoles = new Setting("BedrockHoles", this, true);
    private Setting bedrockColor = new Setting("BedrockColor", this, "BedrockColor", new Colour(0, 255, 0)).setVisible(bedrockHoles::getValBoolean);


    private Setting custom = new Setting("Custom", this, "CustomHoles");
    private Setting customMode = new Setting("CustomMode", this, "Single", new ArrayList<>(Arrays.asList("Single", "Double", "Custom")));
    private final Setting customColor = new Setting("Custom Color", this, "Custom Color", new Colour(255, 255, 255, 255));

    private ConcurrentHashMap<AxisAlignedBB, Colour> holes;

    public HoleESP() {
        super("HoleESP", "HoleESP", Category.RENDER);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        setmgr.rSetting(mode);
        setmgr.rSetting(konasMode);
        setmgr.rSetting(konasLine);
        setmgr.rSetting(radius);
        setmgr.rSetting(ignoreOwnHole);
        setmgr.rSetting(flatOwn);
        setmgr.rSetting(height);
        setmgr.rSetting(width);
        setmgr.rSetting(type);
        setmgr.rSetting(ufoAlpha);

        setmgr.rSetting(depth);
        setmgr.rSetting(noLineDepth);
        setmgr.rSetting(notSelf);
        setmgr.rSetting(sides);

        setmgr.rSetting(obby);
        setmgr.rSetting(obbyHoles);
        setmgr.rSetting(obbyColor);

        setmgr.rSetting(bedrock);
        setmgr.rSetting(bedrockHoles);
        setmgr.rSetting(bedrockColor);

        setmgr.rSetting(custom);
        setmgr.rSetting(customMode);
        setmgr.rSetting(customColor);
    }

    public void update() {
        if (mc.player == null || mc.world == null) return;
        if (holes == null) holes = new ConcurrentHashMap<>();
        else holes.clear();

        int range = (int) Math.ceil(radius.getValDouble());

        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) continue;
            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) continue;
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) continue;
            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) possibleHoles.add(pos);
        }

        possibleHoles.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centerBlock = holeInfo.getCentre();

                if (centerBlock == null) return;

                Colour colour;

                if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) colour = bedrockColor.getColour();
                else colour = obbyColor.getColour();
                if (holeType == HoleUtil.HoleType.CUSTOM) colour = customColor.getColour();

                String mode = customMode.getValString();
                if (mode.equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) holes.put(centerBlock, colour);
                else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) holes.put(centerBlock, colour);
                else if (holeType == HoleUtil.HoleType.SINGLE) holes.put(centerBlock, colour);
            }
        });
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (mc.player == null || mc.world == null || holes == null || holes.isEmpty()) return;
        holes.forEach(this::renderHoles);
    }

    private void renderHoles(AxisAlignedBB hole, Colour color) {
        if(mode.checkValString("Konas")) KonasRenderer.drawHole(hole, konasMode.getValString(), konasLine.getValString(), height.getValDouble(), color, color, notSelf.getValBoolean(), ufoAlpha.getValInt(), sides.getValBoolean(), noLineDepth.getValBoolean(), depth.getValBoolean(), width.getValFloat());
        else {
            switch (type.getValString()) {
                case "Outline":
                    renderOutline(hole, color);
                    break;
                case "Fill":
                    renderFill(hole, color);
                    break;
                case "Both":
                    renderOutline(hole, color);
                    renderFill(hole, color);
                    break;
            }
        }
    }

    private void renderFill(AxisAlignedBB hole, Colour color) {
        if (ignoreOwnHole.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) return;
        Colour fillColor = new Colour(color, 50);
        int ufoAlpha = (this.ufoAlpha.getValInt() * 50) / 255;

        switch (mode.getValString()) {
            case "Air":
                if (flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                break;
            case "Ground":
                RenderUtil.drawBox(hole.offset(0, -1, 0), true, 1, new Colour(fillColor, ufoAlpha), fillColor.getAlpha(), GeometryMasks.Quad.ALL);
                break;
            case "Flat":
                RenderUtil.drawBox(hole, true, 0.01, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                break;
            case "Slab":
                if (flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBox(hole, false, height.getValDouble(), fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                break;
            case "Double":
                if (flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1), true, 2, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                break;
        }
    }

    private void renderOutline(AxisAlignedBB hole, Colour color) {
        if (ignoreOwnHole.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) return;
        Colour outlineColor = new Colour(color, 255);

        switch (mode.getValString()) {
            case "Air":
                if (flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBoundingBoxWithSides(hole, width.getValInt(), outlineColor, ufoAlpha.getValInt(), GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBoundingBox(hole, width.getValInt(), outlineColor, ufoAlpha.getValInt());
                break;
            case "Ground":
                RenderUtil.drawBoundingBox(hole.offset(0, -1, 0), width.getValInt(), new Colour(outlineColor, ufoAlpha.getValInt()), outlineColor.getAlpha());
                break;
            case "Flat":
                RenderUtil.drawBoundingBoxWithSides(hole.setMaxY(hole.minY + 0.01), width.getValInt(), outlineColor, ufoAlpha.getValInt(), GeometryMasks.Quad.DOWN);
                break;
            case "Slab":
                if (this.flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBoundingBoxWithSides(hole, width.getValInt(), outlineColor, ufoAlpha.getValInt(), GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + height.getValDouble()), width.getValInt(), outlineColor, ufoAlpha.getValInt());
                break;
            case "Double": {
                if (this.flatOwn.getValBoolean() && hole.intersects(mc.player.getEntityBoundingBox())) RenderUtil.drawBoundingBoxWithSides(hole, width.getValInt(), outlineColor, ufoAlpha.getValInt(), GeometryMasks.Quad.DOWN);
                else RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1), width.getValInt(), outlineColor, ufoAlpha.getValInt() / 255);
                break;
            }
        }
    }

    private enum KonasLines {FULL, BOTTOM, TOP}
    private enum KonasMode {OUTLINE, FULL, WIREFRAME, FADE}
}
