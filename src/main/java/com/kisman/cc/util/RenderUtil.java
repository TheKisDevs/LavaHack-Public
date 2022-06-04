package com.kisman.cc.util;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.text.*;
import java.util.*;

import com.kisman.cc.catlua.lua.utils.LuaBox;
import com.kisman.cc.module.render.NameTags;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.customfont.norules.CFontRenderer;
import com.kisman.cc.util.render.objects.Vec3dSimple;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.lwjgl.util.glu.*;

import javax.annotation.Nullable;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Frustum frustrum = new Frustum();
	
	private static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    public static ICamera camera = new Frustum();

    public static void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        drawRect2(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect2(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect2(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect2(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        drawRect2(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawBoxESP(final AxisAlignedBB pos, final Color color, final Color line, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha, final int outlineAlpha) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.minX - mc.getRenderManager().viewerPosX, pos.minY - mc.getRenderManager().viewerPosY, pos.minZ - mc.getRenderManager().viewerPosZ, pos.maxX - mc.getRenderManager().viewerPosX, pos.maxY - mc.getRenderManager().viewerPosY, pos.maxZ - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(pos)) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            glDisable(GL11.GL_LIGHTING);
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            if (box) RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            if (outline) RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, line.getRed() / 255.0f, line.getGreen() / 255.0f, line.getBlue() / 255.0f, outlineAlpha / 255.0f);
            GL11.glDisable(2848);
            glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawProgressBox(AxisAlignedBB pos, float progress, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        float nxOff = ( float ) (pos.minX + (pos.getCenter().x - pos.minX) * progress);
        float nyOff = ( float ) (pos.minY + (pos.getCenter().y - pos.minY) * progress);
        float nzOff = ( float ) (pos.minZ + (pos.getCenter().z - pos.minZ) * progress);
        float mxOff = ( float ) (pos.maxX + (pos.getCenter().x - pos.maxX) * progress);
        float myOff = ( float ) (pos.maxY + (pos.getCenter().y - pos.maxY) * progress);
        float mzOff = ( float ) (pos.maxZ + (pos.getCenter().z - pos.maxZ) * progress);

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(nxOff, nyOff, nzOff, mxOff, myOff, mzOff);

        drawBoxESP(axisAlignedBB, color, color, 1f, true, true, color.getAlpha(), 255);

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRect2(float left, float top, float right, float bottom, int color) {
        enableGL2D1();
        glColor(color);
        GL11.glBegin(7);
        GL11.glVertex2f(left, bottom);
        GL11.glVertex2f(right, bottom);
        GL11.glVertex2f(right, top);
        GL11.glVertex2f(left, top);
        GL11.glEnd();
        disableGL2D1();
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void enableGL2D1() {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    public static void disableGL2D1() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawESP(double d, double d1, double d2, double r, double b, double g) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(r, g, b, 0.1825F);
        drawColorBox(new AxisAlignedBB(d, d1, d2, d + 1.0, d1 + 1.0, d2 + 1.0), 0F, 0F, 0F, 0F);
        GL11.glColor4d(0, 0, 0, 0.5);
        drawSelectionBoundingBox(new AxisAlignedBB(d, d1, d2, d + 1.0, d1 + 1.0, d2 + 1.0));
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }


    public static void drawBoxESP(final AxisAlignedBB pos, final Color color, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha, final int outlineAlpha) {
        drawBoxESP(pos, color, color, lineWidth, outline, box, boxAlpha, outlineAlpha);
    }

    public static void drawBoxESP(final BlockPos pos, final Color color, Color line, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha, final int outlineAlpha, final float height) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + height - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            if (box) {
                RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            }
            if (outline) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, line.getRed() / 255.0f, line.getGreen() / 255.0f, line.getBlue() / 255.0f, outlineAlpha / 255.0f);
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }



    public static String DF (Number value, int maxvalue) {
	     DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	     df.setMaximumFractionDigits(maxvalue);
	     return df.format(value);
	}
	
	public static void drawStringWithRect(String string, int x, int y, int colorString, int colorRect, int colorRect2) {
        drawBorderedRect(x - 2, y - 2, x + Minecraft.getMinecraft().fontRenderer.getStringWidth(string) + 2, y + 10, 1, colorRect, colorRect2);
        Minecraft.getMinecraft().fontRenderer.drawString(string, x, y, colorString);
    }

	public static void drawBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2) {
        Render2DUtil.drawRect((int)x, (int)y, (int)x2, (int)y2, col2);

        float f = (float)(col1 >> 24 & 0xFF) / 255F;
        float f1 = (float)(col1 >> 16 & 0xFF) / 255F;
        float f2 = (float)(col1 >> 8 & 0xFF) / 255F;
        float f3 = (float)(col1 & 0xFF) / 255F;

        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

    public static void drawTracer(Entity entity, Colour color, float ticks) {
        drawTracer(entity, color.getR(), color.getG(), color.getB(), color.getA(), ticks);
    }
	
	public static void drawTracer(Entity entity, float red, float green, float blue, float alpha, float ticks) {
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
        double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
        double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)  + entity.height / 2.0f - renderPosY;
        double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        Vec3d eyes = null;
        // if(KillAura.facingCam != null) {
        // 	eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(KillAura.facingCam[1])).rotateYaw(-(float) Math.toRadians(KillAura.facingCam[0]));
        // }
        // else 
        // 	if(Scaffold.facingCam != null) {
        //     	eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Scaffold.facingCam[1])).rotateYaw(-(float) Math.toRadians(Scaffold.facingCam[0]));
        //     }
        // 	else
        // {
        // 	eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Wrapper.INSTANCE.player().rotationPitch)).rotateYaw(-(float) Math.toRadians(Wrapper.INSTANCE.player().rotationYaw));
        // }
        
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(eyes.x, Minecraft.getMinecraft().player.getEyeHeight() + eyes.y, eyes.z);
        GL11.glVertex3d(xPos, yPos, zPos);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
    }
	
	public static void drawESP(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks) {
    	try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)  + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Minecraft.getMinecraft().getRenderManager().playerViewY;
            float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
            boolean thirdPersonView = Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2;

        	GL11.glPushMatrix();
        	
            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float)(thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin((int) 1);
            
            GL11.glVertex3d((double) 0, (double) 0+1, (double) 0.0);
            GL11.glVertex3d((double) 0-0.5, (double) 0+0.5, (double) 0.0);
            GL11.glVertex3d((double) 0, (double) 0+1, (double) 0.0);
            GL11.glVertex3d((double) 0+0.5, (double) 0+0.5, (double) 0.0);
            
            GL11.glVertex3d((double) 0, (double) 0, (double) 0.0);
            GL11.glVertex3d((double) 0-0.5, (double) 0+0.5, (double) 0.0);
            GL11.glVertex3d((double) 0, (double) 0, (double) 0.0);
            GL11.glVertex3d((double) 0+0.5, (double) 0+0.5, (double) 0.0);
            
            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
        	exception.printStackTrace();
        }
    }

    public static void drawBlockFlatESP(BlockPos pos, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glTranslated(-renderPosX, -renderPosY, -renderPosZ);
        glTranslated(pos.getX(), pos.getY(), pos.getZ());

        glColor4f(red, green, blue, 0.30F);
        drawSolidBox();
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBlockOutlineESP(BlockPos pos, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glTranslated(-renderPosX, -renderPosY, -renderPosZ);
        glTranslated(pos.getX(), pos.getY(), pos.getZ());

        glColor4f(red, green, blue, 0.7F);
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBox(BlockPos blockPos, double height, Colour color, int sides) {
        drawBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, height, 1, color, color.getAlpha(), sides);
    }

    public static void drawBox(AxisAlignedBB bb, boolean check, double height, Colour color, int alpha, int sides) {
        if (check) drawBox(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ, color, alpha, sides);
        else drawBox(bb.minX, bb.minY, bb.minZ, bb.maxX - bb.minX, height, bb.maxZ - bb.minZ, color, alpha, sides);
    }

    public static void drawBox(double x, double y, double z, double w, double h, double d, Colour color, int alpha, int sides) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);

        GlStateManager.disableAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        color.glColor();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides, false);
        tessellator.draw();
        GlStateManager.enableAlpha();

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBlockESP(BlockPos pos, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glTranslated(-renderPosX, -renderPosY, -renderPosZ);
        glTranslated(pos.getX(), pos.getY(), pos.getZ());

        glColor4f(red, green, blue, 0.30F);
        drawSolidBox();
        glColor4f(red, green, blue, 0.7F);
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBlockESP(Vec3dSimple pos, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glTranslated(-renderPosX, -renderPosY, -renderPosZ);
        glTranslated(pos.x, pos.y, pos.z);

        glColor4f(red, green, blue, 0.30F);
        drawSolidBox();
        glColor4f(red, green, blue, 0.7F);
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBoxESP(Entity entity, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glTranslated(-renderPosX, -renderPosY, -renderPosZ);
        glTranslated(entity.posX, entity.posY, entity.posZ);

        glColor4f(red, green, blue, 0.30F);
        drawSolidBox();
        glColor4f(red, green, blue, 0.7F);
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBoundingBox(BlockPos bp, double height, float width, Colour color) {
        drawBoundingBox(getBoundingBox(bp, 1, height, 1), width, color, color.getAlpha());
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, Colour color) {
        drawBoundingBox(bb, width, color, color.getAlpha());
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);

        glTranslated(bb.minX, bb.minY, bb.minZ);
        glTranslated(bb.maxX, bb.maxY, bb.maxZ);

        glColor4f(red, green, blue, 0.7F);
        drawOutlinedBox();

        glColor4f(1, 1, 1, 1);

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, Colour color, int alpha) {
        drawBoundingBox(bb, width, color.r, color.g, color.b, alpha);
    }

    public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, Colour color, int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1, 1, 1), width, color, color.getAlpha(), sides);
    }

    public static void drawBoundingBoxWithSides(BlockPos blockPos, int width, Colour color, int alpha, int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1, 1, 1), width, color, alpha, sides);
    }

    public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, Colour color, int sides) {
        drawBoundingBoxWithSides(axisAlignedBB, width, color, color.getAlpha(), sides);
    }

    public static void drawBoundingBoxWithSides(AxisAlignedBB axisAlignedBB, int width, Colour color, int alpha, int sides) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth(width);
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        doVerticies(axisAlignedBB, color, alpha, bufferbuilder, sides, true);
        tessellator.draw();
    }
	
	public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
		tessellator.draw();
		vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
		tessellator.draw();
		vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
		vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
		tessellator.draw();
	}

    private static void colorVertex(double x, double y, double z, float red, float green, float blue, float alpha, BufferBuilder bufferbuilder) {
        bufferbuilder.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ).color(red, green, blue, alpha).endVertex();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, double width, float red, float green, float blue, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth((float) width);
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        colorVertex(bb.minX, bb.minY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.minY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.minY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.maxX, bb.maxY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        colorVertex(bb.minX, bb.maxY, bb.minZ, red, green, blue, alpha, bufferbuilder);
        tessellator.draw();
    }
	
	public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
			Tessellator ts = Tessellator.getInstance();
			BufferBuilder vb = ts.getBuffer();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts X.
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();// Ends X.
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Y.
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();// Ends Y.
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Z.
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
				.color(red, green, blue, alpha).endVertex();
			ts.draw();// Ends Z.
		}
	public static void drawSolidBox() {
        drawSolidBox(DEFAULT_AABB);
    }

    public static void drawSolidBox(AxisAlignedBB bb) {
        glBegin(GL_QUADS);
        {
            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        glEnd();
    }

    public static void drawOutlinedBox() {
        drawOutlinedBox(DEFAULT_AABB);
    }

    public static void drawOutlinedBox(AxisAlignedBB bb) {
        glBegin(GL_LINES);
        {
            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.minZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        glEnd();
    }
	
	public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        tessellator.draw();
        vertexBuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.maxZ);
        vertexBuffer.pos(aa.minX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.minX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.minZ);
        vertexBuffer.pos(aa.maxX, aa.maxY, aa.maxZ);
        vertexBuffer.pos(aa.maxX, aa.minY, aa.maxZ);
        tessellator.draw();
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float) startColor.getRed() / 255.0f;
        float green = (float) startColor.getGreen() / 255.0f;
        float blue = (float) startColor.getBlue() / 255.0f;
        float alpha = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) endColor.getRed() / 255.0f;
        float green1 = (float) endColor.getGreen() / 255.0f;
        float blue1 = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
	
	public static void drawOutlineBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static
    void drawLine ( float x , float y , float x1 , float y1 , float thickness , int hex ) {
        float red = ( hex >> 16 & 0xFF ) / 255.0F;
        float green = ( hex >> 8 & 0xFF ) / 255.0F;
        float blue = ( hex & 0xFF ) / 255.0F;
        float alpha = ( hex >> 24 & 0xFF ) / 255.0F;

        GlStateManager.pushMatrix ( );
        GlStateManager.disableTexture2D ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableAlpha ( );
        GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 1 , 0 );
        GlStateManager.shadeModel ( GL11.GL_SMOOTH );
        GL11.glLineWidth ( thickness );
        GL11.glEnable ( GL11.GL_LINE_SMOOTH );
        GL11.glHint ( GL11.GL_LINE_SMOOTH_HINT , GL11.GL_NICEST );
        final Tessellator tessellator = Tessellator.getInstance ( );
        final BufferBuilder bufferbuilder = tessellator.getBuffer ( );
        bufferbuilder.begin ( GL11.GL_LINE_STRIP , DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos ( (double) x , (double) y , (double) 0 ).color ( red , green , blue , alpha ).endVertex ( );
        bufferbuilder.pos ( (double) x1 , (double) y1 , (double) 0 ).color ( red , green , blue , alpha ).endVertex ( );
        tessellator.draw ( );
        GlStateManager.shadeModel ( GL11.GL_FLAT );
        GL11.glDisable ( GL11.GL_LINE_SMOOTH );
        GlStateManager.disableBlend ( );
        GlStateManager.enableAlpha ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.popMatrix ( );
    }

    public static void drawCircle(float x, float y, float z, float radius, float red, float green, float blue, float alpha){
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = new BlockPos(x, y, z);
        AxisAlignedBB bb = new AxisAlignedBB((double) pos.getX() - mc.getRenderManager().viewerPosX, (double) pos.getY() - mc.getRenderManager().viewerPosY,
                (double) pos.getZ() - mc.getRenderManager().viewerPosZ,
                (double) (pos.getX() + 1) - mc.getRenderManager().viewerPosX,
                (double) (pos.getY() + 1) - mc.getRenderManager().viewerPosY, (double) (pos.getZ() + 1) - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            drawCircleVertices(bb, radius, red, green, blue, alpha);
        }
    }

    public static void drawCircle(BlockPos pos, double radius, Color color) {
        final RenderManager renderManager = mc.renderManager;
        GL11.glPushMatrix();
        GL11.glTranslated(pos.getX() - renderManager.renderPosX, pos.getY() - renderManager.renderPosX, pos.getZ() - renderManager.renderPosX);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(1F);
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < 360; i++) GL11.glVertex2d(Math.cos(i * Math.PI / 180.0) * radius, (Math.sin(i * Math.PI / 180.0) * radius));

        GL11.glVertex2d(Math.cos(360 * Math.PI / 180.0) * radius, (Math.sin(360 * Math.PI / 180.0) * radius));

        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public static void drawColumn(float x, float y, float z, float radius, float red, float green, float blue, float alpha, int amount, double height){
        double Hincrement = height/amount;
        float Rincrement = (radius/amount) * (float) height;

        Minecraft mc = Minecraft.getMinecraft();

        BlockPos pos = new BlockPos(x, y, z);
        AxisAlignedBB bb = new AxisAlignedBB((double) pos.getX() - mc.getRenderManager().viewerPosX, (double) pos.getY() - mc.getRenderManager().viewerPosY,
                (double) pos.getZ() - mc.getRenderManager().viewerPosZ,
                (double) (pos.getX() + 1) - mc.getRenderManager().viewerPosX,
                (double) (pos.getY() + 1) - mc.getRenderManager().viewerPosY, (double) (pos.getZ() + 1) - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            for (int i =0; i<=amount;i++) {
                bb = new AxisAlignedBB(bb.minX, bb.minY + Hincrement*i , bb.minZ, bb.maxX, bb.maxY+ Hincrement*i, bb.maxZ);
                drawCircleVertices(bb, Rincrement * i, red, green, blue, alpha);
            }
        }
    }

    public static void drawCircleVertices(AxisAlignedBB bb, float radius, float red, float green, float blue, float alpha){
        float r = red;
        float g = green;
        float b = blue;
        float a = alpha;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1f);
        for (int i = 0; i < 360; i++) {
            buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(bb.getCenter().x + (Math.sin((i * 3.1415926D / 180)) * radius), bb.minY, bb.getCenter().z + (Math.cos((i * 3.1415926D / 180)) * radius)).color(r, g, b, a).endVertex();
            buffer.pos(bb.getCenter().x + (Math.sin(((i + 1) * 3.1415926D / 180)) * radius), bb.minY, bb.getCenter().z + (Math.cos(((i + 1) * 3.1415926D / 180)) * radius)).color(r, g, b, a).endVertex();
            tessellator.draw();
        }
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawNametag(Entity entity, String[] text, Colour color, int type) {
        Vec3d pos = EntityUtil.getInterpolatedPos(entity, mc.getRenderPartialTicks());
        drawNametag(pos.x, pos.y + entity.height, pos.z, text, color, type);
    }

    public static void drawNametag(double x, double y, double z, String[] text, Colour color, int type) {
        double dist = mc.player.getDistance(x, y, z);
        double scale = 1, offset = 0;
        int start = 0;
        switch (type) {
            case 0:
                scale = dist / 20 * Math.pow(1.2589254, 0.1 / (dist < 25 ? 0.5 : 2));
                scale = Math.min(Math.max(scale, .5), 5);
                offset = scale > 2 ? scale / 2 : scale;
                scale /= 40;
                start = 10;
                break;
            case 1:
                scale = -((int) dist) / 6.0;
                if (scale < 1) scale = 1;
                scale *= 2.0 / 75.0;
                break;
            case 2:
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) scale = 0.0245;
                start = -8;
                break;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y + offset - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);
        if (type == 2) {
            double width = 0;
            Colour colour = new Colour(0, 0, 0, 52);
            NameTags nametags = NameTags.instance;

            for (int i = 0; i < text.length; i++) {
                double w = CustomFontUtil.getStringWidth(text[i]) / 2;
                if (w > width) width = w;
            }
            drawBorderedRect(-width - 1, -mc.fontRenderer.FONT_HEIGHT, width + 2, 1, 1.8f, new Colour(0, 4, 0, 255).getRGB(), new Colour(244, 4, 0, 255).getRGB());
        }
        GlStateManager.enableTexture2D();
        for (int i = 0; i < text.length; i++) CustomFontUtil.drawStringWithShadow(text[i], -CustomFontUtil.getStringWidth(text[i]) / 2, i * (mc.fontRenderer.FONT_HEIGHT + 1) + start, -1);
        GlStateManager.disableTexture2D();
        if (type != 2) GlStateManager.popMatrix();
    }

    public static void prepare() {
        glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        glEnable(GL11.GL_LINE_SMOOTH);
        glEnable(GL32.GL_DEPTH_CLAMP);
    }

    public static void drawPenis(EntityPlayer player, double x, double y, double z, float spin, float cumsize, float amount) {
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)2848);
        GL11.glDepthMask((boolean)true);
        GL11.glLineWidth((float)1.0f);
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glRotatef((float)(-player.rotationYaw), (float)0.0f, (float)player.height, (float)0.0f);
        GL11.glTranslated((double)(-x), (double)(-y), (double)(-z));
        GL11.glTranslated((double)x, (double)(y + (double)(player.height / 2.0f) - (double)0.225f), (double)z);
        GL11.glColor4f((float)1.38f, (float)0.55f, (float)2.38f, (float)1.0f);
        GL11.glRotated((double)((float)(player.isSneaking() ? 35 : 0) + spin), (double)(1.0f + spin), (double)0.0, (double)cumsize);
        GL11.glTranslated((double)0.0, (double)0.0, (double)0.075f);
        Cylinder shaft = new Cylinder();
        shaft.setDrawStyle(100013);
        shaft.draw(0.1f, 0.11f, 0.4f, 25, 20);
        GL11.glTranslated((double)0.0, (double)0.0, (double)-0.12500000298023223);
        GL11.glTranslated((double)-0.09000000074505805, (double)0.0, (double)0.0);
        Sphere right = new Sphere();
        right.setDrawStyle(100013);
        right.draw(0.14f, 10, 20);
        GL11.glTranslated((double)0.16000000149011612, (double)0.0, (double)0.0);
        Sphere left = new Sphere();
        left.setDrawStyle(100013);
        left.draw(0.14f, 10, 20);
        GL11.glColor4f((float)1.35f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslated((double)-0.07000000074505806, (double)0.0, (double)0.589999952316284);
        Sphere tip = new Sphere();
        tip.setDrawStyle(100013);
        tip.draw(0.13f, 15, 20);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
    }

    private static void colorVertex(double x, double y, double z, Colour color, int alpha, BufferBuilder bufferbuilder) {
        bufferbuilder.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ).color(color.r, color.g, color.b, alpha).endVertex();
    }

    private static void doVerticies(AxisAlignedBB axisAlignedBB, Colour color, int alpha, BufferBuilder bufferbuilder, int sides, boolean five) {
        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
        }
        if ((sides & GeometryMasks.Quad.UP) != 0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, color, alpha, bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ, color, alpha, bufferbuilder);
        }
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
            if (five)
                colorVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ, color, color.getAlpha(), bufferbuilder);
        }
    }

    public static AxisAlignedBB getBoundingBox(BlockPos bp, double width, double height, double depth) {
        double x = bp.getX();
        double y = bp.getY();
        double z = bp.getZ();
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
    }

    public static void drawEntityOnScreen(Entity entity, int mouseX, int mouseY, int scale, float posX, float posY) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)posX), (float)((float)posY), (float)Float.intBitsToFloat(Float.floatToIntBits(1.6824397f) ^ 0x7D9F5A2F));
        GlStateManager.scale((float)((float)(-scale)), (float)((float)scale), (float)((float)scale));
        GlStateManager.rotate((float)Float.intBitsToFloat(Float.floatToIntBits(0.010010559f) ^ 0x7F100354), (float)Float.intBitsToFloat(Float.floatToIntBits(3.2874774E38f) ^ 0x7F775283), (float)Float.intBitsToFloat(Float.floatToIntBits(1.277499E38f) ^ 0x7EC03779), (float)Float.intBitsToFloat(Float.floatToIntBits(7.556555f) ^ 0x7F71CF4C));
        GlStateManager.rotate((float)Float.intBitsToFloat(Float.floatToIntBits(0.08215728f) ^ 0x7EAF4213), (float)Float.intBitsToFloat(Float.floatToIntBits(3.2189796E38f) ^ 0x7F722B4B), (float)Float.intBitsToFloat(Float.floatToIntBits(11.650432f) ^ 0x7EBA682B), (float)Float.intBitsToFloat(Float.floatToIntBits(1.8456703E38f) ^ 0x7F0ADA51));
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate((float)Float.intBitsToFloat(Float.floatToIntBits(-0.07293611f) ^ 0x7E925F87), (float)Float.intBitsToFloat(Float.floatToIntBits(4.4185975E37f) ^ 0x7E04F7A3), (float)Float.intBitsToFloat(Float.floatToIntBits(7.4331884f) ^ 0x7F6DDCAE), (float)Float.intBitsToFloat(Float.floatToIntBits(1.0855388E38f) ^ 0x7EA3556F));
        GlStateManager.rotate((float)(-((float)Math.atan((double)(mouseY / Float.intBitsToFloat(Float.floatToIntBits(0.04850099f) ^ 0x7F66A8F9)))) * Float.intBitsToFloat(Float.floatToIntBits(0.45494023f) ^ 0x7F48EDED)), (float)Float.intBitsToFloat(Float.floatToIntBits(7.337801f) ^ 0x7F6ACF44), (float)Float.intBitsToFloat(Float.floatToIntBits(5.3653236E37f) ^ 0x7E2174F3), (float)Float.intBitsToFloat(Float.floatToIntBits(2.4617955E38f) ^ 0x7F393475));
        GlStateManager.translate((float)Float.intBitsToFloat(Float.floatToIntBits(9.835158E37f) ^ 0x7E93FBA7), (float)Float.intBitsToFloat(Float.floatToIntBits(2.4084867E38f) ^ 0x7F3531C4), (float)Float.intBitsToFloat(Float.floatToIntBits(2.8240162E38f) ^ 0x7F547493));
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(Float.intBitsToFloat(Float.floatToIntBits(0.009158009f) ^ 0x7F220B79));
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity, Double.longBitsToDouble(Double.doubleToLongBits(1.5829156308224974E308) ^ 0x7FEC2D44F635CC31L), Double.longBitsToDouble(Double.doubleToLongBits(1.7339437041438026E308) ^ 0x7FEEDD7F3831FB25L), Double.longBitsToDouble(Double.doubleToLongBits(8.225454125242748E307) ^ 0x7FDD4899452B1C5FL), Float.intBitsToFloat(Float.floatToIntBits(4.1008535E37f) ^ 0x7DF6CFA7), Float.intBitsToFloat(Float.floatToIntBits(4.240404f) ^ 0x7F07B164), false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture((int) OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
    }

    public static void GLPre(float lineWidth) {
        boolean depth = GL11.glIsEnabled((int)2896);
        boolean texture = GL11.glIsEnabled((int)3042);
        boolean clean = GL11.glIsEnabled((int)3553);
        boolean bind = GL11.glIsEnabled((int)2929);
        boolean override = GL11.glIsEnabled((int)2848);
        GLPre(depth, texture, clean, bind, override, lineWidth);
    }

    public static void GlPost() {
        GLPost(false, false, false, false, false);
    }

    public static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
        if (depth) GL11.glDisable((int)2896);
        if (!texture) GL11.glEnable((int)3042);
        GL11.glLineWidth((float)lineWidth);
        if (!clean) GL11.glDisable((int)3553);
        if (bind) GL11.glDisable((int)2929);
        if (!override) GL11.glEnable((int)2848);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.depthMask((boolean)false);
    }

    public static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
        GlStateManager.depthMask((boolean)true);
        if (override == false) {
            GL11.glDisable((int)2848);
        }
        if (bind != false) {
            GL11.glEnable((int)2929);
        }
        if (clean != false) {
            GL11.glEnable((int)3553);
        }
        if (texture == false) {
            GL11.glDisable((int)3042);
        }
        if (!depth) return;
        GL11.glEnable((int)2896);
    }

    public static void drawSphere(double red, double green, double blue, double alpha, double x, double y, double z,
                                  float size, int slices, int stacks, float lWidth) {
        Sphere sphere = new Sphere();

        enableDefaults();
        GL11.glColor4d(red, green, blue, alpha);
        GL11.glTranslated(x, y, z);
        GL11.glLineWidth(lWidth);
        sphere.setDrawStyle(GLU.GLU_SILHOUETTE);
        sphere.draw(size, slices, stacks);
        disableDefaults();
    }

    public static void enableDefaults() {
        mc.entityRenderer.disableLightmap();
        GL11.glEnable(3042 /* GL_BLEND */);
        GL11.glDisable(3553 /* GL_TEXTURE_2D */);
        GL11.glDisable(2896 /* GL_LIGHTING */);
        // GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848 /* GL_LINE_SMOOTH */);
        GL11.glPushMatrix();
    }

    public static void disableDefaults() {
        GL11.glPopMatrix();
        GL11.glDisable(2848 /* GL_LINE_SMOOTH */);
        GL11.glDepthMask(true);
        // GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3553 /* GL_TEXTURE_2D */);
        GL11.glEnable(2896 /* GL_LIGHTING */);
        GL11.glDisable(3042 /* GL_BLEND */);
        mc.entityRenderer.enableLightmap();
    }

    public static Vec3d getRenderPos(double x, double y, double z) {

        x = x - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;

        return new Vec3d(x, y, z);
    }

    public static void putVertex3d(double x, double y, double z) {
        GL11.glVertex3d(x, y, z);
    }

    public static void putVertex3d(Vec3d vec) {
        GL11.glVertex3d(vec.x, vec.y, vec.z);
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return RenderUtil.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void setupColor(int color) {
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        GL11.glColor4f(f, f1, f2, f3);
    }

    public static void drawGradientFilledBox(final BlockPos pos, final Color startColor, final Color endColor) {
        final IBlockState iblockstate = mc.world.getBlockState(pos);
        final Vec3d interp = EntityUtil.getInterpolatedPos(mc.player, mc.getRenderPartialTicks());
        drawGradientFilledBox(iblockstate.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), startColor, endColor);
    }

    public static void glBillboard(final float x, final float y, final float z) {
        final float scale = 0.02666667f;
        GlStateManager.translate(x - mc.getRenderManager().renderViewEntity.posX, y - mc.getRenderManager().renderViewEntity.posY, z - mc.getRenderManager().renderViewEntity.posZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(final float x, final float y, final float z, final EntityPlayer player, final float scale) {
        glBillboard(x, y, z);
        final int distance = (int)player.getDistance(x, y, z);
        float scaleDistance = distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) scaleDistance = 1.0f;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void drawText(final BlockPos pos, final String text, int color) {
        if (pos == null || text == null) return;
        GlStateManager.pushMatrix();
        glBillboardDistanceScaled(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-(CustomFontUtil.getStringWidth(text) / 2.0), 0.0, 0.0);
        CustomFontUtil.drawStringWithShadow(text, 0.0f, 0.0f, color);
        GlStateManager.popMatrix();
    }

    public static void drawGradientFilledBox(final AxisAlignedBB bb, final Color startColor, final Color endColor) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        final float alpha = endColor.getAlpha() / 255.0f;
        final float red = endColor.getRed() / 255.0f;
        final float green = endColor.getGreen() / 255.0f;
        final float blue = endColor.getBlue() / 255.0f;
        final float alpha2 = startColor.getAlpha() / 255.0f;
        final float red2 = startColor.getRed() / 255.0f;
        final float green2 = startColor.getGreen() / 255.0f;
        final float blue2 = startColor.getBlue() / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void Method1385() {
        GL11.glDisable(34383);
        GL11.glDisable(2848);
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(7424);
        GL11.glHint(3154, 4352);
        GL11.glPopAttrib();
    }

    public static void Method1386() {
        GL11.glPushAttrib(1048575);
        GL11.glHint(3154, 4354);
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GL11.glEnable(2848);
        GL11.glEnable(34383);
    }

    public static void renderItemOverlays(CFontRenderer cfr, ItemStack stack, int xPosition, int yPosition) {
        renderItemOverlayIntoGUI(cfr, stack, xPosition, yPosition, null);
    }

    public static void renderItemOverlayIntoGUI(CFontRenderer cfr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, boolean showDurBar) {
        if (!stack.isEmpty()) {
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                cfr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - cfr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
            }
            if (stack.getItem().showDurabilityBar(stack) && showDurBar) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());
            if (f3 > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    public static void renderItemOverlayIntoGUI(CFontRenderer cfr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                cfr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - cfr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
            }
            if (stack.getItem().showDurabilityBar(stack)) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());
            if (f3 > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    public static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((x), (y), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((x + width), (y), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }
}
