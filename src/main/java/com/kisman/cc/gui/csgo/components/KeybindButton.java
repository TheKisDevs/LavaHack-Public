package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.gui.csgo.IRenderer;
import org.lwjgl.input.Keyboard;

import java.util.function.Function;

public class KeybindButton extends Button {
    private ValueChangeListener<Integer> listener;
    private boolean listening;
    private Function<Integer, String> keyNameResolver;
    private int value;

    public KeybindButton(IRenderer renderer, int preferredWidth, int preferredHeight, Function<Integer, String> keyNameResolver) {
        super(renderer, "", preferredWidth, preferredHeight);
        this.keyNameResolver = keyNameResolver;

        initialize();
    }

    public KeybindButton(IRenderer renderer, Function<Integer, String> keyNameResolver) {
        super(renderer, "");
        this.keyNameResolver = keyNameResolver;

        initialize();
    }

    private void initialize() {
        setOnClickListener(() -> {
            listening = !listening;

            updateState();
        });

        setOnClickListener2(() -> {
            if(listener != null) listener.onValueChange(Keyboard.KEY_NONE);
            updateState();
        });

        updateState();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void setOnClickListener(ActionEventListener listener) {
        if (getOnClickListener() != null) {
            ActionEventListener old = getOnClickListener();

            super.setOnClickListener(() -> {
                listener.onActionEvent();
                old.onActionEvent();
            });
        } else super.setOnClickListener(listener);
    }

    @Override
    public void setOnClickListener2(ActionEventListener listener) {
        if (getOnClickListener2() != null) {
            ActionEventListener old = getOnClickListener2();

            super.setOnClickListener2(() -> {
                listener.onActionEvent();
                old.onActionEvent();
            });
        } else super.setOnClickListener2(listener);
    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (listening) {
            listening = false;

            if (Keyboard.getEventKey() != 256 && Keyboard.getEventCharacter() != 0) {
                int newValue = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

                if (listener != null) if (listener.onValueChange(newValue)) this.value = newValue;
            }

            updateState();
        }

        return super.keyPressed(key, c);
    }

    @Override
    public int getEventPriority() {
        return listening ? super.getEventPriority() + 1 : super.getEventPriority();
    }

    private void updateState() {
        if (listening) setTitle("Press a button...");
        else setTitle(keyNameResolver.apply(value));
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;

        updateState();
    }

    public Function<Integer, String> getKeyNameResolver() {
        return keyNameResolver;
    }

    public void setKeyNameResolver(Function<Integer, String> keyNameResolver) {
        this.keyNameResolver = keyNameResolver;
    }

    public void setListener(ValueChangeListener<Integer> listener) {
        this.listener = listener;
    }
}