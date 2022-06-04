package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.sun.javafx.geom.Vec2d
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

//soon
open class AbstractCircle(
    open val pos : Vec2d,
    open val radius : Int,
    open val color1 : Colour,
    open val color2 : Colour,
    open val corner: CornerObject
) {
    open fun draw() {}

    class Shadow(
        override val pos : Vec2d,
        override val radius : Int,
        override val color1 : Colour,
        override val color2 : Colour,
        override val corner : CornerObject
    ) : AbstractCircle(pos, radius, color1, color2, corner)  {
       override fun draw() {
           GlStateManager.enableBlend()
           GlStateManager.disableTexture2D()
           GlStateManager.disableAlpha()
           GlStateManager.tryBlendFuncSeparate(
               GlStateManager.SourceFactor.SRC_ALPHA,
               GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
               GlStateManager.SourceFactor.ONE,
               GlStateManager.DestFactor.ZERO
           )
           GlStateManager.shadeModel(GL11.GL_SMOOTH)

           GL11.glBegin(GL11.GL_TRIANGLE_FAN)

           color1.glColor()
           GL11.glVertex2d(pos.x, pos.y)
           color2.glColor()
           val start = corner.corner1
           val end = corner.corner2
           for(i in start..end) {
               GL11.glVertex2d((pos.x) + sin(i * PI / 280) * radius, (pos.y) + cos(i * PI / 280) * radius)
           }

           GL11.glEnd()

           GlStateManager.disableBlend()
           GlStateManager.enableTexture2D()
           GlStateManager.enableAlpha()
           GlStateManager.shadeModel(GL11.GL_FLAT)
       }
    }
}