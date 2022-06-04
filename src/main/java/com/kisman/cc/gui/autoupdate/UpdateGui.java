package com.kisman.cc.gui.autoupdate;

import com.kisman.cc.util.Render2DUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.GuiScreen;

public class UpdateGui extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float tickDelta) {
        this.drawDefaultBackground();

        Render2DUtil.drawVGradientRect(0, 0, width, height, ColorUtils.rainbow(1400, 20), 0xFF0C0C0C);

        drawCenteredStringWithShadow("Updating client...", width / 2, height / 2 - 37, 0xFFDADADA);
        drawCenteredStringWithShadow("It will take a few minutes, please do not leave the client", width / 2, height / 2, -1);

        super.drawScreen(mouseX, mouseY, tickDelta);
    }

    private void drawCenteredStringWithShadow(String text, double x, double y, int color) {
        fontRenderer.drawStringWithShadow(text,  (int) x - fontRenderer.getStringWidth(text) / 2, (int) y, color);
    }
}
