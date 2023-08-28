package com.kisman.cc.gui.mainmenu.gui;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.alts.AltManagerGUI;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class KismanMainMenuGui extends GuiScreen {
    private GuiScreen lastGui;

    public KismanMainMenuGui(GuiScreen lastGui) {this.lastGui = lastGui;}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        CustomFontUtil.drawCenteredStringWithShadow(Kisman.getName() + " " + Kisman.getVersion(), width / 4, 6, ColorUtils.astolfoColors(100, 100));
        GL11.glPopMatrix();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch(button.id) {
            case 1:
                mc.displayGuiScreen(new HalqGui(this));
                break;
            case 2:
                Kisman.openLink("https://discord.gg/BEnn5xA3hg");
                break;
            case 3:
                Kisman.openLink("https://www.youtube.com/channel/UCWxQLRT9CXqcK6YyiKHrrNw");
                break;
            case 5:
                mc.displayGuiScreen(new AltManagerGUI(this));
                break;
            case 6:
                mc.displayGuiScreen(lastGui);
                break;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        addButtons(height / 6 + 48 - 6, 72 - 48);
    }

    private void addButtons(int y, int offset) {
        buttonList.add(new GuiButton(1, width / 2 - 100, y, "Gui"));
        buttonList.add(new GuiButton(2, width / 2 - 100, y + offset, "Discord"));
        buttonList.add(new GuiButton(3, width / 2 - 100, y + offset * 2, "YouTube"));
        buttonList.add(new GuiButton(4, width / 2 - 100, y + offset * 4, 98, 20, "Version"));
        buttonList.add(new GuiButton(5, width / 2 + 2, y + offset * 4, 98, 20, "Alts"));
        buttonList.add(new GuiButton(6, width / 2 - 100, y + offset * 5, "Back"));
    }
}
