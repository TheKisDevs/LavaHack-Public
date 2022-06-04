package com.kisman.cc.gui.vega.component.components.sub;

import com.kisman.cc.gui.vega.component.Component;
import com.kisman.cc.gui.vega.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.Gui;

public class ModeButton extends Component {
    public Button b;
    public Setting set;
    public int offset;
    public boolean drag = false;
    private int x, y;
    private int width, height;

    public ModeButton(Button b, Setting set, int offset) {
        this.b = b;
        this.set = set;
        this.offset = offset;

        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
    }

    public void renderComponent() {
        int height = this.height-1;
        Gui.drawRect(this.x - 3, this.y + 3 + offset, this.x + this.width + 3, this.y + this.height + 3 + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y + offset, this.x + this.width + 3, this.y + this.height + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y + 2 + offset, this.x + this.width + 2, this.y + this.height + 2 + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y + offset, this.x + this.width + 2, this.y + this.height + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y + 1 + offset, this.x + this.width + 1, this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + offset, this.x + this.width + 1, this.y + this.height + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y + offset, this.x + this.width, this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));

        CustomFontUtil.drawCenteredStringWithShadow(set.getName() + ": " + set.getValBoolean(), x + (width / 2), y + 3 + offset + ((height - CustomFontUtil.getFontHeight()) / 2), drag  ? ColorUtils.astolfoColors(100, 100) : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
    }

    public void newOff(int newOff) {
        this.offset = newOff;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) set.setValBoolean(!set.getValBoolean());
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag = false;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + this.width && y > this.y + offset && y < this.y + this.height + this.offset;
    }
}
