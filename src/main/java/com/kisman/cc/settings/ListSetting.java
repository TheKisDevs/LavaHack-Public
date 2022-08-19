package com.kisman.cc.settings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import com.kisman.cc.settings.Setting;

public class ListSetting
        extends Setting2 {
    public final List<String> modes;
    public String currentMode;
    public int index;

    public ListSetting(String name, String currentMode, Supplier<Boolean> visible, String ... options) {
        this.name = name;
        this.modes = Arrays.asList(options);
        this.index = this.modes.indexOf(currentMode);
        this.currentMode = this.modes.get(this.index);
        this.setVisible(visible);
    }

    public ListSetting(String name, String currentMode, String ... options) {
        this.name = name;
        this.modes = Arrays.asList(options);
        this.index = this.modes.indexOf(currentMode);
        this.currentMode = this.modes.get(this.index);
        this.setVisible(() -> true);
    }

    public void changeMode() {
        if (this.index < this.modes.size() - 1) {
            ++this.index;
            this.currentMode = this.modes.get(this.index);
        } else if (this.index >= this.modes.size() - 1) {
            this.index = 0;
            this.currentMode = this.modes.get(0);
        }
    }

    public String getCurrentMode() {
        return this.currentMode;
    }

    public void setCurrentMode(String selected) {
        this.currentMode = selected;
        this.index = this.modes.indexOf(selected);
    }

    public List<String> getModes() {
        return this.modes;
    }

    public String getOptions() {
        return this.modes.get(this.index);
    }
}
