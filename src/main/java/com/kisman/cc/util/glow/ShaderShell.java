package com.kisman.cc.util.glow;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderShell {
    public static ShaderShell ROUNDED_RECT;
    private int shaderID;

    public ShaderShell(String shaderName, boolean post) {
        parseShaderFromFile(shaderName, post);
    }

    public static void init() {
        ROUNDED_RECT = new ShaderShell("roundedrect", false);
    }

    public void attach() {
        ARBShaderObjects.glUseProgramObjectARB(shaderID);
    }

    public void set1I(String name, int value0) {
        ARBShaderObjects.glUniform1iARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0);
    }

    public void set1F(String name, float value0) {
        ARBShaderObjects.glUniform1fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0);
    }

    public void set2F(String name, float value0, float value1) {
        ARBShaderObjects.glUniform2fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1);
    }

    public void set3F(String name, float value0, float value1, float value2) {
        ARBShaderObjects.glUniform3fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1,
                value2);
    }

    public void set4F(String name, float value0, float value1, float value2, float value3) {
        ARBShaderObjects.glUniform4fARB(ARBShaderObjects.glGetUniformLocationARB(shaderID, name), value0, value1,
                value2, value3);
    }

    public void detach() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private void parseShaderFromFile(String shaderName, boolean post) {
        if (shaderName.equalsIgnoreCase("roundedrect")) {

            parseShaderFromString("uniform vec4 color;\n" +
                    "uniform vec2 resolution;\n" +
                    "uniform vec2 center;\n" +
                    "uniform vec2 dst;\n" +
                    "uniform float radius;\n" +
                    "\n" +
                    "float rect(vec2 pos, vec2 center, vec2 size) {  \n" +
                    "    return length(max(abs(center - pos) - (size / 2), 0)) - radius;\n" +
                    "}\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec2 pos = gl_FragCoord.xy;\n" +
                    "\tpos.y = resolution.y - pos.y;\n" +
                    "\tgl_FragColor = vec4(vec3(color), (-rect(pos, center, dst) / radius) * color.a);\n" +
                    "}", post);
        }

    }

    private void parseShaderFromString(String str, boolean post) {
        localInit(str);
    }

    void localInit(String str) {
        int shaderProgram = ARBShaderObjects.glCreateProgramObjectARB();
        if (shaderProgram == 0) {
            System.out.println("PC Issued");
            Minecraft.getMinecraft().shutdown();
            return;
        }
        int shader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        ARBShaderObjects.glShaderSourceARB(shader, str);
        ARBShaderObjects.glCompileShaderARB(shader);
        ARBShaderObjects.glAttachObjectARB(shaderProgram, shader);
        ARBShaderObjects.glLinkProgramARB(shaderProgram);
        this.shaderID = shaderProgram;
    }
}