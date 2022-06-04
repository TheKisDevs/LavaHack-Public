package com.kisman.cc.util.render

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.glUseProgram

/**
 * @author TrollHack 0.0.2
 */
object GlStateUtils {

    private var bindProgram = 0

    fun useProgramForce(id: Int) {
        glUseProgram(id)
        bindProgram = id
    }

    fun useProgram(id: Int) {
        if (id != bindProgram) {
            glUseProgram(id)
            bindProgram = id
        }
    }

    fun alpha(state: Boolean) {
        if (state) {
            GlStateManager.enableAlpha()
        } else {
            GlStateManager.disableAlpha()
        }
    }

    fun blend(state: Boolean) {
        if (state) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }
    }

    fun smooth(state: Boolean) {
        if (state) {
            GlStateManager.shadeModel(GL_SMOOTH)
        } else {
            GlStateManager.shadeModel(GL_FLAT)
        }
    }

    fun lineSmooth(state: Boolean) {
        if (state) {
            glEnable(GL_LINE_SMOOTH)
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
        } else {
            glDisable(GL_LINE_SMOOTH)
        }
    }

    fun depth(state: Boolean) {
        if (state) {
            GlStateManager.enableDepth()
        } else {
            GlStateManager.disableDepth()
        }
    }

    fun texture2d(state: Boolean) {
        if (state) {
            GlStateManager.enableTexture2D()
        } else {
            GlStateManager.disableTexture2D()
        }
    }

    fun cull(state: Boolean) {
        if (state) {
            GlStateManager.enableCull()
        } else {
            GlStateManager.disableCull()
        }
    }

    fun lighting(state: Boolean) {
        if (state) {
            GlStateManager.enableLighting()
        } else {
            GlStateManager.disableLighting()
        }
    }

    fun pushMatrixAll() {
        glMatrixMode(GL_PROJECTION)
        glPushMatrix()
        glMatrixMode(GL_MODELVIEW)
        glPushMatrix()
    }

    fun popMatrixAll() {
        glMatrixMode(GL_PROJECTION)
        glPopMatrix()
        glMatrixMode(GL_MODELVIEW)
        glPopMatrix()
    }

    fun rescale(width: Double, height: Double) {
        GlStateManager.clear(GL_DEPTH_BUFFER_BIT)
        GlStateManager.matrixMode(GL_PROJECTION)
        GlStateManager.loadIdentity()
        GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0)
        GlStateManager.matrixMode(GL_MODELVIEW)
        GlStateManager.loadIdentity()
        GlStateManager.translate(0.0f, 0.0f, -2000.0f)
    }
}