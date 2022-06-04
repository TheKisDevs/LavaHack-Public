package com.kisman.cc.module;

public enum Category {
	COMBAT("Combat"),
	CLIENT("Client"),
	CHAT("Chat"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	RENDER("Render"),
	MISC("Misc"),
	EXPLOIT("Exploit"),
	DEBUG("Debug"),
	LUA("Lua");

	private final String name;
	Category(final String name) { this.name = name;}
	public final String getName() {return name;}
}
