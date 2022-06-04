package com.kisman.cc.gui.halq.component.components.sub.lua;

import com.kisman.cc.catlua.module.ModuleScript;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class ActionButton extends Component {
    private final ModuleScript script;
    private final Action action;
    private int x, y, count, offset;
    private int width = HalqGui.width;
    private int layer;

    public ActionButton(ModuleScript script, Action action, int x, int y, int offset, int count) {
        this.script = script;
        this.action = action;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + width / 2, y + offset}, new double[] {x + width / 2, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
            Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1)));
        } else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(action.name, x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            switch(action) {
                case RELOAD:
                    script.reload();
                    break;
                case UNLOAD:
                    script.unload(true);
                    break;
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }

    public enum Action {
        RELOAD("Reload"),
        UNLOAD("Unload");

        String name;
        Action(String name) {this.name = name;}
    }
}
