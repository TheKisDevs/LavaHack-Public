package com.kisman.cc.gui.component.components;

import java.awt.Color;
import com.kisman.cc.gui.ClickGui;
import com.kisman.cc.gui.component.*;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;

public class Button extends Component {
	public int x;
	public int y;

	public HudModule hudMod;
	public boolean hud;
	public Frame parent;
	public int offset;
	public int opY;
	private boolean isHovered;
	public boolean open;

	public Button(HudModule mod, Frame parent, int offset) {
		this.hud = true;
		this.hudMod = mod;
		this.parent = parent;
		this.offset = offset;
		this.open = false;
	}

	@Override
	public int getOff() {
		return offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.hudMod.isToggled() ? new Color(ClickGui.getRHoveredModule(),ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).darker().getRGB() : new Color(ClickGui.getRHoveredModule(),ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB()) : (this.hudMod.isToggled() ? new Color(ClickGui.getRNoHoveredModule(),ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).darker().getRGB() : new Color(ClickGui.getRNoHoveredModule(),ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB()));

		if(ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
			Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + 1, this.parent.getY() + this.offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
			Gui.drawRect(this.parent.getX() + parent.getWidth() - 1, this.parent.getY() + offset, this.parent.getX() + parent.getWidth(), parent.getY() + this.offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
			if(parent.components.size() == parent.components.indexOf(this)) Gui.drawRect(parent.getX(), parent.getY() + this.offset - 1, parent.getX() + parent.getWidth(), parent.getY() + this.offset, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
		}

		GL11.glPushMatrix();
		GL11.glScalef(0.5f,0.5f, 0.5f);

		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow((this.hudMod.isToggled() ? TextFormatting.BOLD : "") + this.hudMod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, this.hudMod.isToggled() ? new Color(ClickGui.getAActiveText(), ClickGui.getGActiveText(), ClickGui.getBActiveText(), ClickGui.getAActiveText()).getRGB() : new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
		GL11.glPopMatrix();
		if(this.open && (parent.components.indexOf(this) == parent.components.size()) && ClickGui.getSetLineMode() == LineMode.SETTINGALL) Gui.drawRect(parent.getX(), parent.getY() + offset + 11, parent.getX() + parent.getWidth(), parent.getY() + this.offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
	}
	
	@Override
	public int getHeight() {
		return 12;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) this.hudMod.toggle();
	}
	
	public boolean isMouseOnButton(int x, int y) {
		return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
	}
}
