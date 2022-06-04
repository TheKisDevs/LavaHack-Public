package com.kisman.cc.util.shaders.hotbar.shaders;

import com.kisman.cc.util.shaders.hotbar.FramebufferShaderText;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class RenderTypeText extends FramebufferShaderText {
    public RenderTypeText() {
        super("rendertype_text.vsh");
    }

    public void setupUniforms() {
        setupUniform("ModelViewMat");
        setupUniform("ProjMat");
        setupUniform("IViewRotMat");
        setupUniform("ColorModulator");
        setupUniform("FogStart");
        setupUniform("FogEnd");
        setupUniform("FogColor");
        setupUniform("ScreenSize");
    }

    public void updateUniforms() {
//        glUnifont
        GL20.glUniform2f(getUniform("ScreenSize"), (float)new ScaledResolution(mc).getScaledWidth(), (float)new ScaledResolution(mc).getScaledHeight());
    }
}
