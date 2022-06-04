package com.kisman.cc.module.render.shader.shaders;

import com.kisman.cc.module.render.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class SmokyShader extends FramebufferShader {
    public static SmokyShader SMOKY_SHADER;
    public float time;
    public float timeMult = 0.001f;

    public SmokyShader() {
        super("smoky.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), time);
        time += timeMult * animationSpeed;
    }

    static {
        SMOKY_SHADER = new SmokyShader();
    }
}
