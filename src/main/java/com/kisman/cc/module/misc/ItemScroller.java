package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class ItemScroller extends Module {
    public static ItemScroller instance;

    public Setting scrollSpeed = new Setting("ScrollSpeed", this, 20, 1, 100, true);

    public ItemScroller() {
        super("ItemScroller", "", Category.MISC);

        instance = this;

        setmgr.rSetting(scrollSpeed);
    }
}
