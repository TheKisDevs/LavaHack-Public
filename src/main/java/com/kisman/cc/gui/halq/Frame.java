package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.halq.component.components.sub.ColorButton;
import com.kisman.cc.gui.halq.component.components.sub.ModeButton;
import com.kisman.cc.gui.halq.util.LayerMap;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.gui.halq.component.components.Button;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.enums.RectSides;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.ShadowRectObject;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Frame {
    //vars
    public final ArrayList<Component> mods = new ArrayList<>();
    public final Category cat;
    public int x, y, count = 0;
    public boolean reloading = false;

    //logic vars
    public boolean dragging, open = true;
    public int dragX, dragY;

    public Frame(Category cat, int x, int y) {
        int offsetY = HalqGui.height;
        int count1 = 0;

        if(!cat.equals(Category.LUA)) {
            for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                mods.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        } else {
            if(!Kisman.instance.scriptManager.scripts.isEmpty()) {
                for (Module script : Kisman.instance.scriptManager.scripts) {
                    mods.add(new Button(script, x, y, offsetY, count1++));
                    offsetY += HalqGui.height;
                }
            }
        }

        this.cat = cat;
        this.x = x;
        this.y = y;
    }

    public void reload() {
        reloading = true;
        mods.clear();

        int offsetY = HalqGui.height;
        int count1 = 0;

        if(!cat.equals(Category.LUA)) {
            for (Module mod : Kisman.instance.moduleManager.getModulesInCategory(cat)) {
                mods.add(new Button(mod, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        } else {
            for(Module script : Kisman.instance.scriptManager.scripts) {
                mods.add(new Button(script, x, y, offsetY, count1++));
                offsetY += HalqGui.height;
            }
        }
        reloading = false;
    }

    public void render(int mouseX, int mouseY) {
        if(dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        if(HalqGui.shadowRects) {
            ShadowRectObject obj = new ShadowRectObject(x, y, x + HalqGui.width, y + HalqGui.height, HalqGui.getGradientColour(count), HalqGui.getGradientColour(count).withAlpha(0), 5, Collections.singletonList(RectSides.Bottom));
            obj.draw();
        } else {
            Render2DUtil.drawRectWH(x, y, HalqGui.width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());
            if (HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x - HalqGui.headerOffset, y}, new double[]{x, y}, new double[]{x, y + HalqGui.height}, new double[]{x - HalqGui.headerOffset, y + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 0), HalqGui.getGradientColour(count).getColor()));
            if (HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x + HalqGui.width, y}, new double[]{x + HalqGui.width + HalqGui.headerOffset, y}, new double[]{x + HalqGui.width + HalqGui.headerOffset, y + HalqGui.height}, new double[]{x + HalqGui.width, y + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 0)));
        }

        HalqGui.drawString(cat.getName() + (Config.instance.guiRenderSize.getValBoolean() ? " [" + (cat.equals(Category.LUA) ? Kisman.instance.scriptManager.scripts.size() : Kisman.instance.moduleManager.getModulesInCategory(cat).size()) + "]": ""), x, y, HalqGui.width, HalqGui.height);
    }

    public void renderPost(int mouseX, int mouseY) {
        if(open) {
            if(!HalqGui.line || mods.isEmpty()) return;
            int startY = y + HalqGui.height;
            for(Component comp : mods) if(comp instanceof Button) {
                Button button = (Button) comp;
                if(HalqGui.shadowRects) {
                    new ShadowRectObject(x, startY, x + 1, startY + HalqGui.height, HalqGui.getGradientColour(button.getCount()), HalqGui.getGradientColour(button.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                    new ShadowRectObject(x + HalqGui.width - 1, startY, x + HalqGui.width, startY + HalqGui.height, HalqGui.getGradientColour(button.getCount()), HalqGui.getGradientColour(button.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                } else {
                    Render2DUtil.drawRectWH(x, startY, 1, HalqGui.height, HalqGui.getGradientColour(button.getCount()).getRGB());
                    Render2DUtil.drawRectWH(x + HalqGui.width - 1, startY, 1, HalqGui.height, HalqGui.getGradientColour(button.getCount()).getRGB());
                }
                startY += HalqGui.height;
                if(button.open) for(Component comp1 : button.comps) if(comp1.visible()) {
                    boolean open = (comp1 instanceof ModeButton && ((ModeButton) comp1).open) || (comp1 instanceof ColorButton && ((ColorButton) comp1).open);
                    if(HalqGui.shadowRects) {
                        new ShadowRectObject(comp1.getX(), startY, comp1.getX() + 1.5 + (open ? 0.5 : 0), startY + comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()), HalqGui.getGradientColour(comp1.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                        double x__ = comp1.getX() + (HalqGui.width - (LayerMap.getLayer(comp1.getLayer()).modifier * 2)) - 1.5 - (open ? 0.5 : 0);
                        new ShadowRectObject(x__, startY, x + 1.5 + (open ? 0.5 : 0), startY + comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()), HalqGui.getGradientColour(comp1.getCount()).withAlpha(0), 5, Arrays.asList(RectSides.Top, RectSides.Bottom));
                    } else {
                        Render2DUtil.drawRectWH(comp1.getX(), startY, 1.5 + (open ? 0.5 : 0), comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()).getRGB());
                        Render2DUtil.drawRectWH(comp1.getX() + (HalqGui.width - (LayerMap.getLayer(comp1.getLayer()).modifier * 2)) - 1.5 - (open ? 0.5 : 0), startY, 1.5 + (open ? 0.5 : 0), comp1.getHeight(), HalqGui.getGradientColour(comp1.getCount()).getRGB());
                    }
                    startY += comp1.getHeight();
                }
            }
        }
    }

    public void veryRenderPost(int mouseX, int mouseY) {
        if(open && Config.instance.guiDesc.getValBoolean()) for(Component comp : mods) if(comp instanceof Button && ((Button) comp).isMouseOnButton(mouseX, mouseY) && !((Button) comp).description.title.isEmpty()) ((Button) comp).description.drawScreen(mouseX, mouseY);
    }

    public void refresh() {
        int offsetY = HalqGui.height;
        int count1 = count + 1;

        for(Component comp : mods) {
            comp.setOff(offsetY);
            comp.setCount(count1);
            offsetY += HalqGui.height;
            count1++;
            if(comp instanceof Button) {
                Button button = (Button) comp;
                if(button.open) {
                    for (Component comp1 : button.comps) {
                        if(!comp1.visible()) continue;
                        comp1.setCount(count1);
                        comp1.setOff(offsetY);
                        offsetY += comp1.getHeight();
                        count1++;
                    }
                }
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y && y < this.y + HalqGui.height;
    }
}
