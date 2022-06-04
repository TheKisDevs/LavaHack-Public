package com.kisman.cc.util.shaders.hotbar.shaders;

import com.kisman.cc.module.render.shader.FramebufferShader;
import com.kisman.cc.util.shaders.hotbar.FramebufferShaderTex;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class PositionTex extends FramebufferShaderTex {
    public PositionTex() {
        super("position_tex.fsh");
    }

    @Override
    public void setupUniforms() {
        setupUniform("ColorModulator");
        setupUniform("ScreenSize");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform4f(getUniform("ColorModulator"), 1f, 1f, 1f, 1f);
        GL20.glUniform2f(getUniform("ScreenSize"), (float)new ScaledResolution(mc).getScaledWidth(), (float)new ScaledResolution(mc).getScaledHeight());
    }
}
