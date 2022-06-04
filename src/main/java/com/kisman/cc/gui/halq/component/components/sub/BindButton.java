package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.module.Module;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import org.lwjgl.input.Keyboard;

public class BindButton extends Component {
    private final Setting setting;
    private final Module module;
    private int x, y, offset, count;
    private boolean changing;
    private int width = HalqGui.width;
    private int layer;

    public BindButton(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.module = null;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    public BindButton(Module module, int x, int y, int offset, int count) {
        this.setting = null;
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(setting == null && module == null) return;

        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(changing) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, changing ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(changing ? "Press a key..." : module != null ? "Bind: " + Keyboard.getKeyName(module.getKey()) : setting.getName() + ": " + Keyboard.getKeyName(setting.getKey()) , x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) changing = !changing;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            changing = false;
            if(module != null) module.setKey(Keyboard.KEY_NONE);
            else setting.setKey(Keyboard.KEY_NONE);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(changing) {
            if(module == null && setting == null) return;
            if(module != null) module.setKey(key);
            else setting.setKey(key);
            changing = false;
        }
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    public boolean visible() {return setting == null || setting.isVisible();}

    public void setCount(int count) {this.count = count;}
    public int getCount() {return count;}
    public void setWidth(int width) {this.width = width;}
    public void setX(int x) {this.x = x;}
    public int getX() {return x;}
    public void setLayer(int layer) {this.layer = layer;}
    public int getLayer() {return layer;}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }
}
