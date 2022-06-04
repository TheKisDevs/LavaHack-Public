package com.kisman.cc.util

import com.kisman.cc.util.render.objects.AbstractGradient
import com.sun.javafx.geom.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Render2DUtilKt {
    companion object {
        fun getDeltas(ticks: Float, player: EntityPlayer): List<Double> {
            return listOf(
                MathHelper.clampedLerp(player.lastTickPosX, player.posX, ticks.toDouble()),
                MathHelper.clampedLerp(player.lastTickPosY, player.posY, ticks.toDouble()),
                MathHelper.clampedLerp(player.lastTickPosZ, player.posZ, ticks.toDouble())
            )
        }

        fun drawShadowRect(start: Vec2d, end: Vec2d, color: Colour, radius: Int) {
            Render2DUtil.drawRect(start.x, start.y, end.x, end.y, color.rgb)

            AbstractGradient.drawGradientRect(start.x, start.y - radius, end.x, start.y, false, true, color.withAlpha(100).rgb, color.withAlpha(0).rgb)
            AbstractGradient.drawGradientRect(start.x, end.y, end.x, end.y + radius, false, false, color.withAlpha(100).rgb, color.withAlpha(0).rgb)

//            drawSector2(end, 0, 90, radius)
//            drawSector2(Vec2d(end.x, start.y), 90, 180, radius)
//            drawSector2(start, 180, 270, radius)
//            drawSector2(Vec2d(start.x, end.y), 270, 360, radius)

            //TODO: доделать
        }

        fun drawSector2(pos: Vec2d?, start: Int, end: Int, radius: Int, color: Colour) {
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.disableAlpha()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.shadeModel(GL11.GL_SMOOTH)

            GL11.glBegin(GL11.GL_TRIANGLE_FAN)

            color.glColor()
            GL11.glVertex2d(pos?.x ?: 0.0, pos?.y ?: 0.0)
            color.withAlpha(0).glColor()
            for(i in start..end) {
                GL11.glVertex2d((pos?.x ?: 0.0) + sin(i * PI / 280) * radius, (pos?.y ?: 0.0) + cos(i * PI / 280) * radius)
            }

            GL11.glEnd()

            GlStateManager.disableBlend()
            GlStateManager.enableTexture2D()
            GlStateManager.enableAlpha()
            GlStateManager.shadeModel(GL11.GL_FLAT)
        }
    }
}