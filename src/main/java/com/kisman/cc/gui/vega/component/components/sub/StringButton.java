package com.kisman.cc.gui.vega.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.StringEvent;
import com.kisman.cc.gui.ClickGui;
import com.kisman.cc.gui.vega.component.Component;
import com.kisman.cc.gui.vega.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;


public class StringButton extends Component {
    private Minecraft mc = Minecraft.getMinecraft();
    private FontRenderer fr = mc.fontRenderer;
    private int modeIndex = 0;
    public Setting set;
    public Button b;
    public int offset;
    public int x, y;
    private int width, height;
    private String currentString = "";
    private String dString;
    private String regex = "-*[1-9][0-9]*";
    public boolean drag = false;
    private boolean active = false;

    public StringButton(Button b, Setting set, int offset) {
        this.set = set;
        this.b = b;
        this.offset = offset;
        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
        this.dString = set.getdString();
    }

    public void setOff(int offset) {
        this.offset = offset;
    }

    public void renderComponent() {
        /*
        GuiScreen.drawRect(this.button.parent.x, this.button.parent.y + offset, this.button.parent.x + 88, this.button.parent.y + 12 + offset, this.active ? new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());

        if(this.active) {
            fr.drawStringWithShadow(this.currentString + "_", button.parent.x + 4, button.parent.y + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        } else if(!this.active){
            fr.drawStringWithShadow(this.currentString.isEmpty() ? this.set.getdString() : this.currentString, button.parent.x + 4, button.parent.y + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        }

        Gui.drawRect(button.parent.x + 2, button.parent.y + offset, button.parent.x + 3, button.parent.y + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

        if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
            Gui.drawRect(
                    button.parent.x + 88 - 3,
                    button.parent.y + offset,
                    button.parent.x + button.parent.width - 2,
                    button.parent.y + offset + 12,
                    new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
            );
        }

         */
        int height = this.height-1;
        Gui.drawRect(this.x - 3, this.y + 3 + offset, this.x + this.width + 3, this.y + this.height + 3 + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 3, this.y + offset, this.x + this.width + 3, this.y + this.height + offset, (ColorUtils.getColor(33, 33, 42)));
        Gui.drawRect(this.x - 2, this.y + 2 + offset, this.x + this.width + 2, this.y + this.height + 2 + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 2, this.y + offset, this.x + this.width + 2, this.y + this.height + offset, (ColorUtils.getColor(45, 45, 55)));
        Gui.drawRect(this.x - 1, this.y + 1 + offset, this.x + this.width + 1, this.y + this.height + 1 + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x - 1, this.y + offset, this.x + this.width + 1, this.y + this.height + offset, (ColorUtils.getColor(60, 60, 70)));
        Gui.drawRect(this.x, this.y + offset, this.x + this.width, this.y + this.height + offset, (ColorUtils.getColor(34, 34, 40)));

        CustomFontUtil.drawCenteredStringWithShadow(set.getName() + ": " + set.getValString(), x + (width / 2), y + 3 + offset + ((height - CustomFontUtil.getFontHeight()) / 2), drag  ? ColorUtils.astolfoColors(100, 100) : -1);
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.x = b.parent.x;
        this.y = b.parent.y;
        this.width = b.parent.width;
        this.height = b.parent.height;
    }


    public void mouseReleased(int mouseX, int mouseY, int button) {
        drag = false;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            if(set.getOptions() != null) {
                int maxIndex = set.getOptions().size();

                if(modeIndex + 1 >= maxIndex)
                    modeIndex = 0;
                else {
                    modeIndex++;
                }
                set.setValString(set.getOptions().get(modeIndex));
            }
        }

        if(button == 0) {
            if(isMouseOnButton(mouseX, mouseY)) {
                drag = true;
            }
        }
    }

    @Override
    public void newOff(int newOff) {
        this.offset = newOff;
    }

    public void keyTyped(char typedChar, int key) {
        StringEvent event1 = new StringEvent(set, "" + typedChar, com.kisman.cc.event.Event.Era.PRE, active);
        Kisman.EVENT_BUS.post(event1);

        if(event1.isCancelled()) {
            return;
        }

        if(key == 1) return;

        if(Keyboard.KEY_RETURN == key && this.active) {
            this.enterString();
        } else if(key == 14 && this.active) {
            if(currentString != null){
                if (!this.currentString.isEmpty()) {
                    this.currentString = this.currentString.substring(0, this.currentString.length() - 1);
                }
            } else {
                active = false;
            }
        } else if(ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.active && !set.isOnlyNumbers()) {
            this.setString(this.currentString + typedChar);

            StringEvent event2 = new StringEvent(set, "" + typedChar, Event.Era.POST, active);
            Kisman.EVENT_BUS.post(event2);

            if(event2.isCancelled()) {
                active = false;
                return;
            }

            if(set.isOnlyOneWord() && this.active) {
                this.active = false;
            }
        }
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > this.b.parent.x && x < this.b.parent.x + 88 && y > this.b.parent.y + offset && y < this.b.parent.y + 12 + offset) return true;

        return false;
    }

    private void setString(String newString) {
        this.currentString = newString;
    }

    private String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void enterString() {
        this.active = false;

        if (this.currentString.isEmpty()) {
            this.set.setValString(this.set.getdString());
        } else {
            this.set.setValString(this.currentString);
        }
    }
}
