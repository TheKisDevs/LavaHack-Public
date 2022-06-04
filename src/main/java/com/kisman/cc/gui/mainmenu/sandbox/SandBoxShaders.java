package com.kisman.cc.gui.mainmenu.sandbox;

import com.kisman.cc.Kisman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SandBoxShaders {
    public GLSLSandboxShader currentshader;
    public long time;

    public void init( ) {
        try {
            Object[ ] shader = getRandomShader( );
            if( shader == null ) {
                currentshader = null;
                Kisman.LOGGER.info("No shaders found in .minecraft/glslmenu/. Not drawing anything");
            } else {
                String name = (String)shader[0];
                InputStream is = (InputStream)shader[1];

                currentshader = new GLSLSandboxShader(name, is);
                if( !currentshader.initialized )
                    currentshader = null;
                else
                    time = System.currentTimeMillis();
            }
        } catch(Exception e) {
            e.printStackTrace();
            currentshader = null;
        }
    }

    public Object[] getRandomShader() throws FileNotFoundException {
        File folder = new File("glslmenu");
        if(!folder.exists()) return null;

        List<String> shaders = new ArrayList<>();

        for(File file : folder.listFiles()) {
            String name = file.getName();
            if(name.endsWith(".fsh"))
                shaders.add(name);
        }

        if(shaders.size() == 0) return null;

        String randomname = shaders.get(new Random().nextInt(shaders.size()));

        FileInputStream fis = new FileInputStream(new File("glslmenu/" + randomname));

//        String randomname = "testshader/fsh";
//        FileInputStream fis = new FileInputStream(new File("assets/kismancc/mainmenu/sandboxshaders/" + randomname));

        return new Object[] {randomname, fis};
    }
}
