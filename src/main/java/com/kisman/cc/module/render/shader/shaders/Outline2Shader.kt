package com.kisman.cc.module.render.shader.shaders

import com.kisman.cc.mixin.mixins.accessor.AccessorShaderGroup
import com.kisman.cc.module.render.ShaderCharms
import com.kisman.cc.module.render.shader.ShaderUtil
import com.kisman.cc.module.render.shader.shaders.troll.ShaderHelper
import com.kisman.cc.util.EntityUtil
import com.kisman.cc.util.MathUtil
import com.kisman.cc.util.render.GlStateUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11.*
import kotlin.math.pow

object Outline2Shader {
    private val mc = Minecraft.getMinecraft();

    private var outlineAlpha: ShaderUtil.UniformFloat? = null
    private var filledAlpha: ShaderUtil.UniformFloat? = null
    private var width: ShaderUtil.UniformFloat? = null
    private var widthSq: ShaderUtil.UniformFloat? = null
    private var ratio: ShaderUtil.UniformFloat? = null

    fun setupUniforms(outlineAlpha: Float, filledAlpha: Float, width: Float, ratio: Float) {
        this.outlineAlpha = ShaderUtil.UniformFloat("outlineAlpha", outlineAlpha)
        this.filledAlpha = ShaderUtil.UniformFloat("filledAlpha", filledAlpha)
        this.width = ShaderUtil.UniformFloat("width", width);
        this.widthSq = ShaderUtil.UniformFloat("widthSq", width.pow(2));
        this.ratio = ShaderUtil.UniformFloat("ratio", ratio);
    }

    fun drawShader(shaderHelper: ShaderHelper, frameBufferFinal: Framebuffer, partialTicks: Float) {
        // Push matrix
        GlStateUtils.pushMatrixAll()

        GlStateUtils.blend(true)
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)

        shaderHelper.shader!!.render(partialTicks)

        // Re-enable blend because shader rendering will disable it at the end
        GlStateUtils.blend(true)
        GlStateUtils.depth(false)

        // Draw it on the main frame buffer
        mc.framebuffer.bindFramebuffer(false)
        frameBufferFinal!!.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false)

        // Revert states
        GlStateUtils.blend(true)
        GlStateUtils.depth(true)
//        GlStateUtils.texture2d(false)
//        GlStateManager.depthMask(false)

        // Revert matrix
        GlStateUtils.popMatrixAll()
    }

    fun drawEntities(partialTicks: Float, range: Float) {
        GlStateUtils.texture2d(true)
        GlStateUtils.alpha(true)
        GlStateUtils.depth(true)
        GlStateManager.depthMask(true)

        val camera = Frustum()
        val viewEntity = mc.renderViewEntity ?: mc.player
        val partialTicksD = partialTicks.toDouble()
        val x = MathUtil.lerp(viewEntity.lastTickPosX, viewEntity.posX, partialTicksD)
        val y = MathUtil.lerp(viewEntity.lastTickPosY, viewEntity.posY, partialTicksD)
        val z = MathUtil.lerp(viewEntity.lastTickPosZ, viewEntity.posZ, partialTicksD)

        camera.setPosition(x, y, z)

        for(entity in mc.world.loadedEntityList) {
            if(mc.player.getDistance(entity) > range) continue
            if(!ShaderCharms.instance.entityTypeCheck(entity)) continue

            val renderer = mc.renderManager.getEntityRenderObject<Entity>(entity) ?: continue

            if(!renderer.shouldRender(entity, camera, x, y, z)) continue

            val yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
            val pos = EntityUtil.getInterpolatedPos(entity, partialTicks).subtract(mc.renderManager.renderPosX, mc.renderManager.renderPosY, mc.renderManager.renderPosZ)

            renderer.setRenderOutlines(true)
            renderer.doRender(entity, pos.x, pos.y, pos.z, yaw, partialTicks)
        }

        GlStateUtils.texture2d(false)
        GlStateUtils.alpha(false)
    }

    fun updateUniforms(shaderHelper: ShaderHelper) {
        shaderHelper.shader?.let {
            for(shader in (it as AccessorShaderGroup).listShaders) {
                ShaderUtil.setupUniforms(shader, arrayOf<ShaderUtil.Uniform>(outlineAlpha!!, filledAlpha!!, width!!, widthSq!!, ratio!!))
            }
        }
    }
}