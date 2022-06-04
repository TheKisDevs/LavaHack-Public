package com.kisman.cc.module.render.shader.shaders;

import com.kisman.cc.module.render.shader.FramebufferShader;
import org.lwjgl.opengl.*;

public class ItemShader extends FramebufferShader {
    public static final ItemShader ITEM_SHADER;

    public boolean blur;
    public float mix = 0.0f;
    public float alpha = 1.0f;
    public float imageMix = 0.0f;
    public boolean useImage;
    public boolean rotate;
    public float red, green, blue;
    public float radius;
    public float quality;

    public ItemShader() {
        super("itemglow.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
        setupUniform("dimensions");
        setupUniform("blur");
        setupUniform("mixFactor");
        setupUniform("minAlpha");
        setupUniform("image");
        setupUniform("imageMix");
        setupUniform("useImage");
        // setupUniform("rotate");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth * (radius * quality), 1F / mc.displayHeight * (radius * quality));
        GL20.glUniform3f(getUniform("color"), red, green, blue);
        GL20.glUniform1f(getUniform("divider"), 140F);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform1f(getUniform("maxSample"), 10F);
        GL20.glUniform2f(getUniform("dimensions"), mc.displayWidth, mc.displayHeight);
        GL20.glUniform1i(getUniform("blur"), blur ? 1 : 0);
        GL20.glUniform1f(getUniform("mixFactor"), mix);
        GL20.glUniform1f(getUniform("minAlpha"), alpha);
        GL13.glActiveTexture(GL13.GL_TEXTURE8);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL20.glUniform1i(getUniform("image"), 8);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL20.glUniform1f(getUniform("imageMix"), imageMix);
        GL20.glUniform1i(getUniform("useImage"), useImage ? 1 : 0);
    }

    static {
        ITEM_SHADER = new ItemShader();
    }
}
