package com.kisman.cc.util.render;

import com.kisman.cc.util.*;
import com.kisman.cc.util.render.konas.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.*;

public class KonasRenderer implements Globals {
    public static void drawHole(AxisAlignedBB axisAlignedBB, String mode, String lines, double height, Colour color, Colour lineColor, boolean notSelf, int fadeAlpha, boolean sides, boolean noLineDepth, boolean depth, float width) {
        AxisAlignedBB tBB = axisAlignedBB;
        axisAlignedBB = axisAlignedBB.setMaxY(axisAlignedBB.minY + height);

        if (mode.equalsIgnoreCase("FULL")) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBox(axisAlignedBB, true, 1, color, color.a, sides ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            TessellatorUtil.release();
        }

        if (mode.equalsIgnoreCase("FULL") || mode.equalsIgnoreCase("OUTLINE")) {
            TessellatorUtil.prepare();
            TessellatorUtil.drawBoundingBox(axisAlignedBB, width, lineColor);
            TessellatorUtil.release();
        }

        if (mode.equalsIgnoreCase("WIREFRAME")) {
            BlockRenderUtil.prepareGL();
            BlockRenderUtil.drawWireframe(axisAlignedBB.offset(-(mc.getRenderManager()).renderPosX, -(mc.getRenderManager()).renderPosY, -(mc.getRenderManager()).renderPosZ), lineColor.getRGB(), width);
            BlockRenderUtil.releaseGL();
        }

        if (mode.equalsIgnoreCase("FADE")) {
            tBB = tBB.setMaxY(tBB.minY + height);

            if (mc.player.getEntityBoundingBox() != null && tBB.intersects(mc.player.getEntityBoundingBox()) && notSelf) tBB = tBB.setMaxY(Math.min(tBB.maxY, mc.player.posY + 1D));

            TessellatorUtil.prepare();
            if (depth) {
                GlStateManager.enableDepth();
                tBB = tBB.shrink(0.01D);
            }
            TessellatorUtil.drawBox(tBB, true, height, color, fadeAlpha, sides ? FaceMasks.Quad.NORTH | FaceMasks.Quad.SOUTH | FaceMasks.Quad.WEST | FaceMasks.Quad.EAST : FaceMasks.Quad.ALL);
            if (width >= 0.1F) {
                if (lines.equalsIgnoreCase("BOTTOM")) tBB = new AxisAlignedBB(tBB.minX, tBB.minY, tBB.minZ, tBB.maxX, tBB.minY, tBB.maxZ);
                else if (lines.equalsIgnoreCase("TOP")) tBB = new AxisAlignedBB(tBB.minX, tBB.maxY, tBB.minZ, tBB.maxX, tBB.maxY, tBB.maxZ);
                if (noLineDepth) GlStateManager.disableDepth();
                TessellatorUtil.drawBoundingBox(tBB, width, lineColor, fadeAlpha);
            }
            TessellatorUtil.release();
        }
    }
}
