package com.kisman.cc.gui.vega.component.components.sub;

import com.kisman.cc.gui.vega.component.Component;
import com.kisman.cc.gui.vega.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    public Button b;
    public Setting s;
    public int offset;

    private int x, y;
    private int width, height;
    private double renderWidth;
    private boolean drag = false;

    public Slider(Button b, Setting s, int offset) {
        this.b = b;
        this.s = s;
        this.offset = offset;

        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
    }

    public void renderComponent() {
        int height = this.height-1;
        Gui.drawRect(this.x -3, this.y + 3 + offset, (int)((double)this.x + (double)this.width + 3), this.y + this.height + 3 + offset, (ColorUtils.getColor(40, 40, 50)));
        Gui.drawRect(this.x - 2, this.y + 4 + offset, (int)((double)this.x + (double)this.width + 2), this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + 5 + offset, (int)((double)this.x + (double)this.width + 1), this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));
        Gui.drawRect(this.x - 1, this.y + 5 + offset, (int)((double)this.x + this.renderWidth + 1), this.y + this.height + offset, (ColorUtils.getColor(24, 24, 30)));
        Gui.drawRect(this.x, this.y + 6 + offset, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 1 + offset, (ColorUtils.getColor(65, 65, 80)));
        Gui.drawRect(this.x, this.y + 7 + offset, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 2 + offset, (ColorUtils.getColor(80, 80, 95)));
        Gui.drawRect(this.x, this.y + 8 + offset, (int)((double)this.x + 3 + this.renderWidth - 3), this.y + this.height - 3 + offset, (ColorUtils.getColor(95, 95, 115)));

        CustomFontUtil.drawCenteredStringWithShadow(s.getName() + ": " + s.getValDouble(), x + (width / 2), y + 3 + offset + ((height - CustomFontUtil.getFontHeight()) / 2), drag ? ColorUtils.astolfoColors(100, 100) : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.x = b.parent.x;
        this.y = b.parent.y;

        double diff = Math.min(88, Math.max(0, mouseX - this.x));
        double min = s.getMin();
        double max = s.getMax();

        renderWidth = width * (s.getValDouble() - min) / (max - min);
        if (drag) {
            System.out.println(diff);
            if (diff == 0) s.setValDouble(s.getMin());
            else s.setValDouble(roundToPlace(((diff / width) * (max - min) + min), 2));
        }
    }

    public void newOff(int newOff) {
        offset = newOff;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) drag = true;
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag = false;
    }


    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + this.width && y > this.y + offset && y < this.y + this.height + this.offset;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
