package com.kisman.cc.module.render;

import com.kisman.cc.module.*;

public class CameraClip extends Module {
    public static CameraClip instance;

    public CameraClip() {
        super("CameraClip", Category.RENDER);

        instance = this;
    }
}
