package com.kisman.cc.util.shaders;

import java.util.*;
import java.io.*;

public class Shaders {
    public GLSLSandboxShader currentshader;
    public long time;
    
    public void init() {
        try {
            final Object[] shader = this.getRandomShader();
            if (shader == null) {
                this.currentshader = null;
            }

            else {
                final String name = (String)shader[0];
                final InputStream is = (InputStream)shader[1];
                this.currentshader = new GLSLSandboxShader(name, is);
                if (!this.currentshader.initialized) {
                    this.currentshader = null;
                }
                else {
                    this.time = System.currentTimeMillis();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.currentshader = null;
        }
    }
    
    public Object[] getRandomShader() throws FileNotFoundException {
        final File folder = new File("assets/kismancc/mainmenu/sandboxshaders/");
        if (!folder.exists()) {
            return null;
        }
        final List<String> shaders = new ArrayList<String>();
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final String name = file.getName();
            if (name.endsWith(".fsh")) {
                shaders.add(name);
            }
        }
        if (shaders.size() == 0) {
            return null;
        }
        final String randomname = shaders.get(new Random().nextInt(shaders.size()));
        final FileInputStream fis = new FileInputStream(new File("assets/kismancc/shaders/" + randomname));
        return new Object[] { randomname, fis };
    }
}
