package com.kisman.cc.module.chat;

import com.kisman.cc.module.*;

public class Notification extends Module {
    public static Notification instance;

    public Notification() {
        super("Notification", "kgdrklbdf", Category.CHAT);

        instance = this;
    }
}
