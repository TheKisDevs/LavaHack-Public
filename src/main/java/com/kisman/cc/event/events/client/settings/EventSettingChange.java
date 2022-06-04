package com.kisman.cc.event.events.client.settings;

import com.kisman.cc.event.Event;
import com.kisman.cc.settings.Setting;

public class EventSettingChange extends Event {
    public Setting setting;
    public Type type;
    public Action action;
    public EventSettingChange(Setting setting, Type type, Action action) {
        this.setting = setting;
        this.type = type;
        this.action = action;
    }

    public static class BooleanSetting extends EventSettingChange {
        public BooleanSetting(Setting setting) {
            super(setting, Type.Boolean, Action.Default);
        }
    }

    public static class ModeSetting extends EventSettingChange {
        public ModeSetting(Setting setting) {
            super(setting, Type.Boolean, Action.Default);
        }
    }

    public static class ColorSetting extends EventSettingChange {
        public ColorSetting(Setting setting) {
            super(setting, Type.Boolean, Action.EndFocused);
        }
    }

    public static class NumberSetting extends EventSettingChange {
        public NumberSetting(Setting setting) {
            super(setting, Type.Boolean, Action.EndFocused);
        }
    }

    public enum Type {Boolean, Mode, Color, Number}
    public enum Action {Default, EndFocused, EveryTime}
}
