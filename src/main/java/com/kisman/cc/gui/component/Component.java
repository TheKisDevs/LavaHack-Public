package com.kisman.cc.gui.component;

public class Component {
	public int x;
	public int y;
	public int offset;

	public void renderComponent() {}
	public void updateComponent(int mouseX, int mouseY) {}
	public void mouseClicked(int mouseX, int mouseY, int button) {}
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
	public int getParentHeight() {return 0;}
	public void keyTyped(char typedChar, int key) {}
	public int getOff() { return 0; }
	public void setOff(int newOff) {}
	public int getHeight() {return 0;}
}
