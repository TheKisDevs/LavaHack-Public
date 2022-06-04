package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class CheckBox extends Component {
    private final Setting setting;
    private int x, y, offset, count;
    private int width = HalqGui.width;
    private int layer;

    public CheckBox(Setting setting, int x, int y, int offset, int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(setting.getValBoolean()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + width / 2, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x + width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.getGradientColour(count).getColor()));
        } else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, setting.getValBoolean() ? HalqGui.getGradientColour(count).getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(setting.getName(), x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            setting.setValBoolean(!setting.getValBoolean());
            Kisman.EVENT_BUS.post(new EventSettingChange.BooleanSetting(setting));
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

    public boolean visible() {return setting.isVisible();}

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
