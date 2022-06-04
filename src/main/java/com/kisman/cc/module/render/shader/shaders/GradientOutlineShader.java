package com.kisman.cc.module.render.shader.shaders;

import com.kisman.cc.module.render.shader.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.*;

import java.awt.*;

public final class GradientOutlineShader extends FramebufferShader {
    public static final GradientOutlineShader INSTANCE;
    public float time = 0;

    public Color color;
    public float radius;
    public float quality;
    public boolean gradientAlpha;
    public int alphaOutline;
    public float duplicate;
    public float moreGradient;
    public float creepy;
    public float alpha;
    public int numOctaves;

    public GradientOutlineShader() {
        super("outlineGradient.frag");
    }

    @Override public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform( "resolution" );
        this.setupUniform( "time" );
        this.setupUniform("moreGradient");
        this.setupUniform("Creepy");
        this.setupUniform("alpha");
        this.setupUniform("NUM_OCTAVES");
    }

    @Override public void updateUniforms() {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / this.mc.displayWidth * (radius * quality), 1.0f / this.mc.displayHeight * (radius * quality));
        GL20.glUniform3f(this.getUniform("color"), color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("alpha0"), gradientAlpha ? -1.0f : alphaOutline / 255.0f);
        GL20.glUniform2f( getUniform( "resolution" ), new ScaledResolution( mc ).getScaledWidth( )/duplicate, new ScaledResolution( mc ).getScaledHeight( )/duplicate );
        GL20.glUniform1f( getUniform( "time" ), time );
        GL20.glUniform1f(getUniform("moreGradient"), moreGradient);
        GL20.glUniform1f(getUniform("Creepy"), creepy);
        GL20.glUniform1f(getUniform("alpha"), alpha);
        GL20.glUniform1i(getUniform("NUM_OCTAVES"), numOctaves);

    }

    static {
        INSTANCE = new GradientOutlineShader();
    }

    public void update(double speed) {
        this.time += speed;
    }
}