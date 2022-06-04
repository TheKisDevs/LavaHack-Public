package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.gui.csgo.AbstractComponent;
import com.kisman.cc.gui.csgo.IRenderer;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.util.function.Function;

public class StringButton extends Button {
    private ValueChangeListener<String> listener;
    private boolean listening;
    private String value;
    private String dString;

    public StringButton(IRenderer renderer, int preferredWidth, int preferredHeight, String dString) {
        super(renderer, "", preferredWidth, preferredHeight);
        this.dString = dString;

        initialize();
    }

    public StringButton(IRenderer renderer, String dString) {
        super(renderer, "");
        this.dString = dString;

        initialize();
    }

    private void initialize() {
        setOnClickListener(() -> {
            listening = !listening;

            updateState();
        });

        updateState();
    }

    @Override
    public void setOnClickListener(ActionEventListener listener) {
        if (getOnClickListener() != null) {
            ActionEventListener old = getOnClickListener();

            super.setOnClickListener(() -> {
                listener.onActionEvent();
                old.onActionEvent();
            });
        } else {
            super.setOnClickListener(listener);
        }

    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (listening) {
            if(Keyboard.getEventKey() == 1) return super.keyPressed(key, c);

            if(Keyboard.KEY_RETURN == Keyboard.getEventKey()) {
                enterString();
            } else if(Keyboard.getEventKey() == 14) {
                if(!value.isEmpty()) {
                    value = value.substring(0, value.length() - 1);
                }
            } else if(ChatAllowedCharacters.isAllowedCharacter(Keyboard.getEventCharacter())) {
                value += Keyboard.getEventCharacter();
            }

            updateState();
        }

        return super.keyPressed(key, c);
    }

    private void enterString() {
        this.listening = false;

        if (value.isEmpty()) {
            listener.onValueChange(dString);
            setValue(dString);
        } else {
            listener.onValueChange(value);
            setValue(value);
        }
    }

    @Override
    public int getEventPriority() {
        return listening ? super.getEventPriority() + 1 : super.getEventPriority();
    }

    private void updateState() {
        setTitle(value + (listening ? "_" : ""));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;

        updateState();
    }

    public void setListener(ValueChangeListener<String> listener) {
        this.listener = listener;
    }
}
