package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.Object3dTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32

abstract class Abstract3dObject {
    abstract val type: Object3dTypes
    abstract val color: Colour
    abstract fun draw(ticks: Float)

    fun prepare(depth: Boolean, alpha: Boolean) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        if(!depth) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        if(alpha) GlStateManager.enableAlpha()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL32.GL_DEPTH_CLAMP)
    }

    fun release(alpha: Boolean) {
        GL11.glDisable(GL32.GL_DEPTH_CLAMP)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        if(alpha) GlStateManager.enableAlpha()
        GlStateManager.enableCull()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.glLineWidth(1.0f)
        GlStateManager.shadeModel(GL11.GL_FLAT)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE)
        GL11.glPopAttrib()
    }

    fun glBillboardDistanceScaled(vec : Vec3d, player : EntityPlayer, scale : Float) {
        glBillboard(vec)
        var scaleDist : Float = player.getDistance(vec.x, vec.y, vec.z).toFloat() / 2f / (2f + (2f - scale))
        GL11.glScalef(scaleDist, scaleDist, scaleDist)
    }

    fun glBillboard(vec : Vec3d) {
        val scale = 0.2666667f
        val mc = Minecraft.getMinecraft()
        GL11.glTranslated(
                vec.x - mc.renderManager.renderViewEntity.posX,
                vec.y - mc.renderManager.renderViewEntity.posY,
                vec.z - mc.renderManager.renderViewEntity.posZ
        )
        GL11.glNormal3f(0.0f, 0.0f, 0.0f)
        GL11.glRotatef(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(
                mc.player.rotationPitch,
                if(mc.gameSettings.thirdPersonView == 2) -1f else 1f,
                0f,
                0f
        )
        GL11.glScalef(-scale, -scale, -scale)
    }

    class CubicRendering {
        companion object {
            fun setup() {
                setup(true)
            }

            fun release() {
                release(true)
            }

            fun setup(customFont : Boolean) {
                GlStateManager.pushMatrix()
                GlStateManager.enableBlend()
                GlStateManager.disableDepth()
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE)
                if(customFont) GlStateManager.enableTexture2D() else GlStateManager.disableTexture2D()
                GlStateManager.depthMask(false)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
                GL11.glLineWidth(1.5f)
            }

            fun release(customFont : Boolean) {
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
                GlStateManager.depthMask(true)
                GlStateManager.enableDepth()
                if(customFont) GlStateManager.disableTexture2D() else GlStateManager.enableTexture2D()
                GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }
        }
    }
}