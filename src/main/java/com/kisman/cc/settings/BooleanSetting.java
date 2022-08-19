package com.kisman.cc.settings;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import com.kisman.cc.settings.Setting2;

public class BooleanSetting
        extends Setting2 {
    private boolean state;
    private Block block;
    private String desc;

    public BooleanSetting(String name, Block block, boolean state, Supplier<Boolean> visible) {
        this.name = name;
        this.block = block;
        this.state = state;
        this.setVisible(visible);
    }

    public BooleanSetting(String name, String desc, boolean state, Supplier<Boolean> visible) {
        this.name = name;
        this.desc = desc;
        this.state = state;
        this.setVisible(visible);
    }

    public BooleanSetting(String name, boolean state) {
        this.name = name;
        this.state = state;
        this.setVisible(() -> true);
    }

    public BooleanSetting(String name, boolean state, Supplier<Boolean> visible) {
        this.name = name;
        this.state = state;
        this.setVisible(visible);
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getCurrentValue() {
        return this.state;
    }

    public void setValue(boolean state) {
        this.state = state;
    }
}

