package com.kisman.cc.gui.csgo.components.slider;

import com.kisman.cc.gui.csgo.IRenderer;
import com.kisman.cc.gui.csgo.Window;
import com.kisman.cc.gui.csgo.components.Button;
import com.kisman.cc.gui.csgo.components.Pane;
import com.kisman.cc.gui.csgo.layout.FlowLayout;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.util.Render2DUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class AdvancedSliderWindow extends GuiScreen {
    private Minecraft mc = Minecraft.getMinecraft();
    private String title;
    public int x, y, width, height;
    private int headerHeight;

    private Pane buttonPane;
    private IRenderer renderer;

    private boolean beingDragged;
    private int dragX, dragY;
    private int oldMouseX, oldMouseY;

    private GuiTextField guiTextField;

    public AdvancedSliderWindow(IRenderer renderer, String title, int x, int y, int width, int height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.guiTextField = new GuiTextField(1, mc.fontRenderer, 0, 0, 140, 22);
        this.renderer = renderer;
        this.buttonPane = new Pane(renderer, new FlowLayout());
        this.buttonPane.addComponent(new Button(renderer, "Save"));
        this.buttonPane.addComponent(new Button(renderer, "Reset"));
    }

    public void drawScreen(int mouseX, int mouseY) {
        int fontHeight = renderer.getStringHeight(title);
        int headerFontOffset = fontHeight / 4;

        headerHeight = headerFontOffset * 2 + fontHeight;

        renderer.drawRect(x, y, width, height, Window.BACKGROUND);
        renderer.drawRect(x, y, width, headerHeight, Window.SECONDARY_FOREGROUND);

        if(Config.instance.guiGlow.getValBoolean()) Render2DUtil.drawRoundedRect(x / 2, y / 2, (x + width) / 2, (y + headerHeight) / 2, Window.SECONDARY_FOREGROUND, Config.instance.glowBoxSize.getValDouble());

        renderer.drawString(x + width / 2 - renderer.getStringWidth(title) / 2, y + headerFontOffset, title, Config.instance.guiAstolfo.getValBoolean() ? renderer.astolfoColorToObj() : Window.FOREGROUND);

//        drawEntityOnScreen((x + width / 2) / 2, (y + height - 10) / 2, 30, (float)((x + width / 2) / 2) - this.oldMouseX, (float)((y + height - 10) / 2) - this.oldMouseY, mc.player);

        guiTextField.drawTextBox();

        oldMouseX = mouseX;
        oldMouseY = mouseY;
    }

    public void setTitle(String title) {this.title = title;}

    public void mousePressed(int button, int x, int y) {
        guiTextField.mouseClicked(x, y, button);
        if (button == 0) {
            if (x >= this.x && y >= this.y && x <= this.x + this.width && y <= this.y + this.headerHeight) {
                beingDragged = true;

                dragX = this.x - x;
                dragY = this.y - y;
            }
        }
    }

    public void keyTyped(int key, char typedChar) {
        guiTextField.textboxKeyTyped(typedChar, key);
    }

    private void drag(int mouseX, int mouseY) {
        if (beingDragged) {
            this.x = mouseX + dragX;
            this.y = mouseY + dragY;
        }
    }

    public void mouseReleased(int button, int x, int y) {
        if (button == 0) beingDragged = false;
    }

    public void mouseMoved(int x, int y) {
        drag(x, y);
    }
}
