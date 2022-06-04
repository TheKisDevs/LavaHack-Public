package com.kisman.cc.catlua.lua.tables;

import com.kisman.cc.catlua.lua.utils.LuaUtils;
import com.kisman.cc.catlua.lua.utils.LuaVec2d;
import com.kisman.cc.util.Globals;
import net.minecraft.client.gui.GuiScreen;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;

public class GuiBuilder implements Globals {

    private static LuaValue value;
    private final String title;

    private boolean prepareImGui;
    private LuaClosure init;
    private LuaClosure render;
    private LuaClosure keyPressed;
    private LuaClosure keyReleased;
    private LuaClosure charTyped;
    private LuaClosure mouseClicked;
    private LuaClosure mouseReleased;
    private LuaClosure mouseScrolled;

    private boolean isPauseScreen = true;

    public GuiBuilder(String title) {
        this.title = title;
    }

    public GuiBuilder setRender(LuaClosure render) {
        this.render = render;
        return this;
    }

    public GuiBuilder setInit(LuaClosure init) {
        this.init = init;
        return this;
    }

    public void prepareImGui(boolean prepareImGui) {
        this.prepareImGui = prepareImGui;
    }

    public GuiBuilder isPauseScreen(boolean isPauseScreen) {
        this.isPauseScreen = isPauseScreen;
        return this;
    }

    public GuiBuilder setCharTyped(LuaClosure charTyped) {
        this.charTyped = charTyped;
        return this;
    }

    public GuiBuilder setMouseClicked(LuaClosure mouseClicked) {
        this.mouseClicked = mouseClicked;
        return this;
    }

    public GuiBuilder setMouseReleased(LuaClosure mouseReleased) {
        this.mouseReleased = mouseReleased;
        return this;
    }

    public void setKeyPressed(LuaClosure keyPressed) {
        this.keyPressed = keyPressed;
    }

    public void setKeyReleased(LuaClosure keyReleased) {
        this.keyReleased = keyReleased;
    }

    public void setMouseScrolled(LuaClosure mouseScrolled) {
        this.mouseScrolled = mouseScrolled;
    }

    public GuiScreen build() {
        return new GuiScreen() {
            @Override public void initGui() {
                LuaUtils.safeCall(init);
            }

            @Override public void drawScreen(int mouseX, int mouseY, float delta) {
                drawDefaultBackground();
                LuaUtils.safeCall(render,
                        CoerceJavaToLua.coerce(new LuaVec2d(mouseX, mouseY))
                );
            }

            @Override public boolean doesGuiPauseGame() {
                return isPauseScreen;
            }

            @Override public void mouseClicked(int mouseX, int mouseY, int button) {
                LuaUtils.safeCall(mouseClicked,
                        CoerceJavaToLua.coerce(new LuaVec2d(mouseX, mouseY)),
                        LuaValue.valueOf(button)
                );
                try {super.mouseClicked(mouseX, mouseY, button);} catch (IOException ignored) {}
            }

            @Override public void mouseReleased(int mouseX, int mouseY, int button) {
                LuaUtils.safeCall(mouseReleased,
                        CoerceJavaToLua.coerce(new LuaVec2d(mouseX, mouseY)),
                        LuaValue.valueOf(button)
                );
                super.mouseReleased(mouseX, mouseY, button);
            }

            @Override public void keyTyped(char ch, int keyCode) {
                LuaUtils.safeCall(keyPressed, LuaValue.valueOf(keyCode), LuaValue.valueOf(ch));
                try {
                    super.keyTyped(ch, keyCode);
                } catch (IOException ignored) {}
            }
        };
    }

    public static LuaValue getLua() {
        if (value == null) {
            LuaTable table = new LuaTable();
            table.set("new", new New());
            table.set("__index", table);
            value = table;
        }
        return value;
    }

    public static class New extends OneArgFunction {
        public LuaValue call(LuaValue name) {
            if (!name.isstring()) throw new IllegalArgumentException("Invalid arguments.");
            GuiBuilder builder = new GuiBuilder(name.toString());
            return CoerceJavaToLua.coerce(builder);
        }
    }
}
