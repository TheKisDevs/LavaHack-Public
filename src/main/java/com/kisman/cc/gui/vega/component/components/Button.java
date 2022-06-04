package com.kisman.cc.gui.vega.component.components;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.VegaGui;
import com.kisman.cc.gui.vega.component.*;
import com.kisman.cc.gui.vega.component.components.sub.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Button {
    public ArrayList<Component> comp;

    public int x, y;
    public Frame parent;
    public Module mod;
    public boolean open;
    public int width, height;
    public int animation = 0;
    public int offset = 0;

    public Button(int x, int y, int offset, int width, int height, Frame parent, Module mod) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.mod = mod;
        this.width = width;
        this.height = height;
        this.offset = offset;

        this.comp = new ArrayList<>();

        int opY = offset + 12;

        if(mod != null) {

            comp.add(new KeyBind(this, opY));
            opY += 12;

            if(Kisman.instance.settingsManager.getSettingsByMod(mod) != null) {
                for(Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
                    if(set.isCheck()) {
                        comp.add(new ModeButton(this, set, opY));
                        opY += height;
                    }

                    if(set.isSlider()) {
                        comp.add(new Slider(this, set, opY));
                        opY += height;
                    }

                    if(set.isCombo()) {
                        comp.add(new StringButton(this, set, opY));
                        opY += height;
                    }
                }
            }
        }
    }

    public void renderComponent() {
        Gui.drawRect(this.x - 3, this.y + 3 + offset, this.x + this.width + 3, this.y + this.height + 3 + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y + offset, this.x + this.width + 3, this.y + this.height + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y + 2 + offset, this.x + this.width + 2, this.y + this.height + 2 + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y + offset, this.x + this.width + 2, this.y + this.height + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y + 1 + offset, this.x + this.width + 1, this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + offset, this.x + this.width + 1, this.y + this.height + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y + offset, this.x + this.width, this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));

        GL11.glPushMatrix();
        Gui.drawRect(this.x + 3, this.y + offset, this.x + this.animation, this.y + this.height + offset,(ColorUtils.getColor(60, 60, 70)));
        Render2DUtil.drawRect(this.x + 2, this.y + 0.5 + offset, this.x + this.animation, this.y + this.height + offset,(ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x, this.y + 1 + offset, this.x + this.animation, this.y + this.height + offset, (ColorUtils.astolfoColors(100, 100)));
        GL11.glPopMatrix();

        if(mod.isToggled() && animation <= width - 113) ++animation;
        if(!mod.isToggled()) {
            --animation;
            if(animation < 0) animation = 0;
        }

        CustomFontUtil.drawStringWithShadow(mod.getName(), x + 6, y + (float) (((VegaGui.instance.test.getValBoolean() ? height : height / 2) - CustomFontUtil.getFontHeight()) / 2) + offset, mod.isToggled() ? ColorUtils.astolfoColors(100, 100) : -1);

        if(Kisman.instance.settingsManager.getSettingsByMod(mod) != null && Kisman.instance.settingsManager.getSettingsByMod(mod).size() > 2) CustomFontUtil.drawStringWithShadow(open ? "<" : "=", x + width - 8, y + (float) (((VegaGui.instance.test.getValBoolean() ? height : height / 2) - CustomFontUtil.getFontHeight()) / 2) + offset, open ? ColorUtils.astolfoColors(100, 100) : -1);
        if(open && !comp.isEmpty()) for(Component comp : comp) comp.renderComponent();
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.x = parent.x;
        this.y = parent.y;
        for(Component comp : comp) {
            comp.updateComponent(mouseX, mouseY);
            parent.refresh();
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) mod.toggle();
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            open = !open;
            parent.refresh();
        }
        if(!comp.isEmpty()) for(Component comp : comp) comp.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if(!comp.isEmpty()) for(Component comp : comp) comp.mouseReleased(mouseX, mouseY, button);
    }

    public void keyTyped(char typedChar, int key) {
        if(!comp.isEmpty()) for(Component comp : comp) comp.keyTyped(typedChar, key);
    }

    public void newOff(int newOff) {
        offset = newOff;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y + offset && y <= this.y + offset + height;
    }
}
