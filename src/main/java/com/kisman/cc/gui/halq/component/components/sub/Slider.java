package com.kisman.cc.gui.halq.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.gui.halq.util.TextUtil;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    private final Setting setting;
    private int x, y, offset, count;
    private boolean dragging;
    private int width = HalqGui.width;
    private int layer;

    private String customValue = "";
    private boolean hasDot = false;
    private boolean bool = false;

    public Slider(Setting setting, int x, int y, int offset,  int count) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.count = count;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        double min = setting.getMin();
        double max = setting.getMax();

        if(bool) dragging = false;
        else customValue = "";

        if (dragging) {
            double diff = Math.min(width, Math.max(0, mouseX - this.x));
            if (diff == 0) setting.setValDouble(setting.getMin());
            else setting.setValDouble(roundToPlace(((diff / width) * (max - min) + min), 2));
        }

        String toRender = bool ? customValue + "_" : setting.getName() + ": " + setting.getNumberType().getFormatter().apply(setting.getValDouble());

        Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.backgroundColor.getRGB());

        int width = (int) (this.width * (setting.getValDouble() - min) / (max - min));
        if(HalqGui.shadowCheckBox) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x, y + offset}, new double[] {x + width, y + offset}, new double[] {x + width, y + offset + HalqGui.height}, new double[] {x, y + offset + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.backgroundColor, 4)));
        else Render2DUtil.drawRectWH(x, y + offset, width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());

        HalqGui.drawString(toRender, x, y + offset, width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) dragging = true;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) bool = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
        Kisman.EVENT_BUS.post(new EventSettingChange.NumberSetting(setting));
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public void keyTyped(char typedChar, int key) {
        if(bool) {
            if(key == Keyboard.KEY_RETURN) {
                bool = false;
                if(!customValue.isEmpty() && customValue.matches(TextUtil.Companion.getDoubleRegex())) setting.setValDouble(TextUtil.Companion.parseNumber(customValue));
                return;
            }

            if(key == 14 && !customValue.isEmpty()) {
                customValue = customValue.substring(0, customValue.length() - 1);
                return;
            }

            if(customValue.isEmpty()) {
                if (TextUtil.Companion.isNumberChar(typedChar) && typedChar != '0') customValue += typedChar;
            } else {
                if(TextUtil.Companion.isNumberChar(typedChar)) customValue += typedChar;
                else if(typedChar == '.' && !hasDot) {
                    customValue += typedChar;
                    hasDot = true;
                }
            }
        }
    }
    @Override public void setOff(int newOff) {this.offset = newOff;}
    @Override public int getHeight() {return HalqGui.height;}
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

    private static double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
