package com.kisman.cc.util.shaders;

import com.kisman.cc.Kisman;
import org.lwjgl.opengl.*;
import java.io.*;
import java.nio.charset.*;

public class GLSLSandboxShader {
    private int programId;
    private int timeUniform;
    private int mouseUniform;
    private int resolutionUniform;
    public boolean initialized;
    
    public GLSLSandboxShader(final String name, final InputStream is) throws IOException {
        this.initialized = false;
        final int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, this.createShader("/shaders/passthrough.vsh", GLSLSandboxShader.class.getResourceAsStream("/shaders/passthrough.vsh"), 35633));
        GL20.glAttachShader(program, this.createShader(name, is, 35632));
        GL20.glLinkProgram(program);
        final int linked = GL20.glGetProgrami(program, 35714);
        if (linked == 0) {
            Kisman.LOGGER.error(GL20.glGetShaderInfoLog(program, GL20.glGetProgrami(program, 35716)));
            return;
        }
        GL20.glUseProgram(this.programId = program);
        this.timeUniform = GL20.glGetUniformLocation(program, (CharSequence)"time");
        this.mouseUniform = GL20.glGetUniformLocation(program, (CharSequence)"mouse");
        this.resolutionUniform = GL20.glGetUniformLocation(program, (CharSequence)"resolution");
        GL20.glUseProgram(0);
        this.initialized = true;
    }
    
    public void useShader(final int width, final int height, final float mouseX, final float mouseY, final float time) {
        GL20.glUseProgram(this.programId);
        GL20.glUniform2f(this.resolutionUniform, (float)width, (float)height);
        GL20.glUniform2f(this.mouseUniform, mouseX / width, 1.0f - mouseY / height);
        GL20.glUniform1f(this.timeUniform, time);
    }
    
    public int createShader(final String check, final InputStream inputStream, final int shaderType) throws IOException {
        final int shader = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shader, this.readStreamToString(inputStream));
        GL20.glCompileShader(shader);
        final int compiled = GL20.glGetShaderi(shader, 35713);
        if (compiled == 0) {
            Kisman.LOGGER.error(GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, 35716)));
            Kisman.LOGGER.error("Caused by {}", check);
            return 0;
        }
        return shader;
    }
    
    public String readStreamToString(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[512];
        int read;
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}