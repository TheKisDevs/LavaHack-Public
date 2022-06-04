package com.kisman.cc.gui.halq.component.components;

import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

public class Description extends Component {
    public final String title;
    private int count;
    private final int width;

    public Description(String title, int count) {
        this.title = title;
        this.count = count;
        this.width = CustomFontUtil.getStringWidth(title);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Render2DUtil.drawRectWH(mouseX + 5, mouseY, width, HalqGui.height, HalqGui.getGradientColour(count).getRGB());
        if(HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {mouseX - HalqGui.headerOffset + 5, mouseY}, new double[] {mouseX + 5, mouseY}, new double[] {mouseX + 5, mouseY + HalqGui.height}, new double[] {mouseX + 5 - HalqGui.headerOffset, mouseY + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 0), HalqGui.getGradientColour(count).getColor()));
        if(HalqGui.shadow) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {mouseX + 5 + width, mouseY}, new double[] {mouseX + width + 5 + HalqGui.headerOffset, mouseY}, new double[] {mouseX + 5 + width + HalqGui.headerOffset, mouseY + HalqGui.height}, new double[] {mouseX + width + 5, mouseY + HalqGui.height}), HalqGui.getGradientColour(count).getColor(), ColorUtils.injectAlpha(HalqGui.getGradientColour(count).getColor(), 0)));

        HalqGui.drawCenteredString(title, mouseX, mouseY, width + HalqGui.headerOffset * 2, HalqGui.height);
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }
}
