package com.kisman.cc.module.render.shader;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public abstract class Shader {
    public int program;
    public Map<String, Integer> uniformsMap;

    public Shader(final String fragmentShader) {
        int vertexShaderID;
        int fragmentShaderID;
        try {
            final InputStream vertexStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/vertex.vert");
            vertexShaderID = this.createShader(IOUtils.toString(vertexStream, Charset.defaultCharset()), 35633);
            IOUtils.closeQuietly(vertexStream);
            final InputStream fragmentStream = this.getClass().getResourceAsStream("/assets/kismancc/shader/fragment/" + fragmentShader);
            fragmentShaderID = this.createShader(IOUtils.toString(fragmentStream, Charset.defaultCharset()), 35632);
            IOUtils.closeQuietly(fragmentStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (vertexShaderID == 0 || fragmentShaderID == 0) {
            return;
        }
        this.program = ARBShaderObjects.glCreateProgramObjectARB();
        if (this.program == 0) {
            return;
        }
        ARBShaderObjects.glAttachObjectARB(this.program, vertexShaderID);
        ARBShaderObjects.glAttachObjectARB(this.program, fragmentShaderID);
        ARBShaderObjects.glLinkProgramARB(this.program);
        ARBShaderObjects.glValidateProgramARB(this.program);
    }

    public void startShader() {
        GL11.glPushMatrix();
        GL20.glUseProgram(this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<>();
            this.setupUniforms();
        }
        this.updateUniforms();
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        GL11.glPopMatrix();
    }

    public void setupUniforms() {}
    public void updateUniforms() {}

    public int createShader(final String shaderSource, final int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) return 0;
            ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) throw new RuntimeException("Error creating shader: " + this.getLogInfo(shader));
            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    public String getLogInfo(final int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, 35716));
    }

    public void setUniform(final String uniformName, final int location) {
        this.uniformsMap.put(uniformName, location);
    }

    public void setupUniform(final String uniformName) {
        this.setUniform(uniformName, GL20.glGetUniformLocation(this.program, uniformName));
    }

    public int getUniform(final String uniformName) {
        return this.uniformsMap.get(uniformName);
    }

    public int getProgramId() {
        return this.program;
    }
}
