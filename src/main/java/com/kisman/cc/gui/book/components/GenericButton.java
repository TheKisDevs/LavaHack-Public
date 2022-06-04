package com.kisman.cc.gui.book.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GenericButton extends ActionButton {

  public FontRenderer fontRenderer;

  public GenericButton(int id, int x, int y, String text) {
    super(id, x, y, 0, 0, text);
    this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    this.width = this.fontRenderer.getStringWidth(this.displayString) + 4;
    this.height = this.fontRenderer.FONT_HEIGHT + 4;
  }

  public void drawButton(Minecraft client, int mouseX, int mouseY, float partialTicks) {
    if(this.enabled) {
      int x_end = this.x + this.width;
      int y_end = this.y + this.height;

      this.drawRect(this.x, this.y, x_end, y_end, new Color(0, 0, 0, 255).getRGB());
      this.drawRect(this.x, this.y, x_end, this.y + 1, -1);
      this.drawRect(this.x, this.y, this.x + 1, y_end, -1);
      this.drawRect(this.x, y_end - 1, x_end, y_end, -1);
      this.drawRect(x_end - 1, this.y, x_end, y_end, -1);
      this.fontRenderer.drawString(this.displayString, this.x + 2, this.y + 2, -1);
    }
  }

  public void onClick(GuiScreen parent) {}

}
