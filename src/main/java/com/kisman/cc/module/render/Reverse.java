package com.kisman.cc.module.render;

import com.kisman.cc.module.*;

public class Reverse extends Module {
    public static Reverse instance;

    public Reverse() {
        super("Reverse", "Reverse", Category.RENDER);

        instance = this;
    }
}
