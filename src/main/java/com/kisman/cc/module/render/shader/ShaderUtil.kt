package com.kisman.cc.module.render.shader

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL20

class ShaderUtil {
    companion object {
        fun setupUniforms(shader: Shader, uniforms: Array<Uniform>) {
            for(uniform in uniforms) {
                if(uniform is UniformFloat) {
                    GL20.glUniform1f(shader.getUniform(uniform.name), uniform.value)
                }
                if(uniform is UniformInt) {
                    GL20.glUniform1i(shader.getUniform(uniform.name), uniform.value)
                }
            }
        }

        fun setupUniforms(shader: net.minecraft.client.shader.Shader, uniforms: Array<Uniform>) {
            for(uniform in uniforms) {
                if(uniform is UniformFloat) {
                    shader.shaderManager.getShaderUniform(uniform.name)?.set(uniform.value)
                }
                if(uniform is UniformInt) {
                    shader.shaderManager.getShaderUniform(uniform.name)?.set(uniform.value.toFloat())
                }
            }
        }

        fun clearFrameBuffer(frameBuffer: Framebuffer) {
            frameBuffer!!.apply {
                bindFramebuffer(true)
                GlStateManager.clearColor(framebufferColor[0], framebufferColor[1], framebufferColor[2], framebufferColor[3])

                var mask = 16384
                if (useDepth) {
                    GlStateManager.clearDepth(1.0)
                    mask = mask or 256
                }

                GlStateManager.clear(mask)
            }
        }
    }

    open class Uniform
    class UniformFloat(val name: String, val value: Float) : Uniform()
    class UniformInt(val name: String, val value: Int) : Uniform()
}