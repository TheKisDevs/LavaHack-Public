package com.kisman.cc.viaforge.gui;

import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.viaforge.ViaForge;
import com.kisman.cc.viaforge.protocol.ProtocolCollection;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiProtocolSelector extends GuiScreen {
    public SlotList list;
    private GuiScreen parent;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 27, 200, 20, "Back"));
        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        list.actionPerformed(p_actionPerformed_1_);
        if (p_actionPerformed_1_.id == 1) mc.displayGuiScreen(parent);
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        list.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        CustomFontUtil.drawCenteredStringWithShadow("ViaForge", width / 4, 6, ColorUtils.astolfoColors(100, 100));
        GL11.glPopMatrix();

        CustomFontUtil.drawString("by EnZaXD/Flori2007", 1, 1, -1);
        CustomFontUtil.drawString("Discord: EnZaXD#6257", 1, 11, -1);

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    class SlotList extends GuiSlot {
        public SlotList(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
            super(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_);
        }

        @Override
        protected int getSize() {
            return ProtocolCollection.values().length;
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {
            ViaForge.getInstance().setVersion(ProtocolCollection.values()[i].getVersion().getVersion());
        }

        @Override
        protected boolean isSelected(int i) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5, float v) {
            CustomFontUtil.drawCenteredStringWithShadow(ProtocolCollection.getProtocolById(ProtocolCollection.values()[i].getVersion().getVersion()).getName(), width / 2, i2, ViaForge.getInstance().getVersion() == ProtocolCollection.values()[i].getVersion().getVersion() ? ColorUtils.astolfoColors(100, 100) : -1);
        }
    }
}
