package com.kisman.cc.gui.halq.component.components;

import com.kisman.cc.Kisman;
import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.gui.halq.component.components.sub.lua.ActionButton;
import com.kisman.cc.gui.halq.component.components.sub.modules.BindModeButton;
import com.kisman.cc.gui.halq.component.components.sub.modules.VisibleBox;
import com.kisman.cc.gui.halq.util.LayerMap;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.gui.halq.component.components.sub.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Button extends Component {
    public final ArrayList<Component> comps = new ArrayList<>();
    public final Module mod;
    public final Description description;
    public int x, y, offset, count;
    public boolean open = false;

    public Button(Module mod, int x, int y, int offset, int count) {
        this.mod = mod;
        this.description = new Description(mod.getDescription(), count);
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;

        int offsetY = offset + HalqGui.height;
        int count1 = 0;

        if(mod instanceof ModuleScript) {
            comps.add(new ActionButton((ModuleScript) mod, ActionButton.Action.RELOAD, x, y, offsetY, count1++));
            offsetY += HalqGui.height;
            comps.add(new ActionButton((ModuleScript) mod, ActionButton.Action.UNLOAD, x, y, offsetY, count1++));
        } else {
            comps.add(new BindButton(mod, x, y, offsetY, count1++));
            offsetY += HalqGui.height;
            comps.add(new VisibleBox(mod, x, y, offsetY, count1++));
            offsetY += HalqGui.height;
            comps.add(new BindModeButton(mod, x, y, offsetY, count1++));
            offsetY += HalqGui.height;

            if (Kisman.instance.settingsManager.getSettingsByMod(mod) != null) {
                for (Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
                    if (set == null) continue;
                    if (set.isSlider()) {
                        comps.add(new Slider(set, x, y, offsetY, count1++));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCheck()) {
                        comps.add(new CheckBox(set, x, y, offsetY, count1++));
                        offsetY += HalqGui.height;
                    }
                    if (set.isBind()) {
                        comps.add(new BindButton(set, x, y, offsetY, count1++));
                        offsetY += HalqGui.height;
                    }
                    if (set.isCombo()) {
                        comps.add(new ModeButton(set, x, y, offsetY, count1++));
                        offsetY += HalqGui.height;
                    }
                    if (set.isColorPicker()) {
                        comps.add(new ColorButton(set, x, y, offsetY, count1++));
                        offset += HalqGui.height;
                    }
                }
            }
            for(Component comp : comps) {
                comp.setLayer(1);
                comp.setWidth(90);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(mod.isToggled()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, mod.isToggled() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        String text = mod.getName() + (Config.instance.guiShowBinds.getValBoolean() && mod.getKey() != Keyboard.KEY_NONE ? " [" + Keyboard.getKeyName(mod.getKey()) + "]" : "");

        HalqGui.drawString(text, x, y + offset, HalqGui.width, HalqGui.height);

        if(mod.isBeta()) {
            GL11.glPushMatrix();
            GL11.glScaled(0.5, 0.5, 1);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("beta", (x + CustomFontUtil.getStringWidth(text)) * 2, (y + offset) * 2, HalqGui.primaryColor.getRGB());
            GL11.glPopMatrix();
        }

         if(open && !comps.isEmpty()) {
             int height = 0;
             for(Component comp : comps) {
                 if(!comp.visible()) continue;
                 comp.drawScreen(mouseX, mouseY);
                 height += comp.getHeight();
             }
             if(HalqGui.test) {
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height, HalqGui.width, 1, HalqGui.getGradientColour(count).getRGB());
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height + height - 1, HalqGui.width, 1, HalqGui.getGradientColour(getLastColorCount()).getRGB());
             }
         }
    }

    private int getLastColorCount() {
        return comps.get(comps.size() - 1).getCount();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) mod.toggle();
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.updateComponent(x + LayerMap.getLayer(comp.getLayer()).modifier, y);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open && !comps.isEmpty()) for(Component comp : comps) if(comp.visible()) comp.keyTyped(typedChar, key);
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height + getSize() * HalqGui.height;
    }

    public void setCount(int count) {
        this.count = count;
        description.setCount(count);
    }

    public int getCount() {return count;}

    public int getSize() {
        int i = 0;
        for(Component comp : comps) if(comp.visible()) i++;
        return i;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + HalqGui.height + offset;
    }
}
